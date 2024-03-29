/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.model;

import static org.phoebus.sns.mpsbypasses.MPSBypasses.logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.phoebus.framework.jobs.JobMonitor;
import org.phoebus.framework.rdb.RDBConnectionPool;
import org.phoebus.sns.mpsbypasses.MPSBypasses;
import org.phoebus.sns.mpsbypasses.modes.MachineMode;

/** Model of all the bypass infos
 *
 *  <p>Meant to be thread-safe
 *
 *  @author Delphy Armstrong - Original MPSBypassModel
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassModel implements BypassListener
{
	/** Listeners */
	final private List<BypassModelListener> listeners = new CopyOnWriteArrayList<>();

	/** Currently selected machine mode */
	private MachineMode machine_mode = MachineMode.Site;

	/** All bypasses for the selected machine mode */
	private Bypass[] mode_bypasses = new Bypass[0];

	/** Bypasses filtered by <code>state_filter</code> */
	private Bypass[] filtered_bypasses = new Bypass[0];

	/** Filter: Which bypass state to show */
	private BypassState state_filter = BypassState.Bypassed;

	/** Filter: Which request type to show */
	private RequestState request_filter = RequestState.All;

	/** Counts of bypass states, computed from <code>mode_bypasses</code> */
	private int bypassed, bypassable, not_bypassable, disconnected, error;

	private boolean running = false;



	/** Initialize */
	public BypassModel()
	{
		updateCounts();
	}

	/** @param listener Listener to add */
    public void addListener(final BypassModelListener listener)
    {
    	listeners.add(listener);
    }

	/** @param listener Listener to remove */
    public void removeListener(final BypassModelListener listener)
    {
    	listeners.remove(listener);
    }

	/** Connect to PVs, ... */
    private synchronized void start()
    {
        logger.log(Level.FINE, "Start: " + this + " on " + Thread.currentThread());
    	if (running)
    	    return;
	    for (Bypass bypass : mode_bypasses)
    		bypass.start();
    	running = true;
    }

	/** Disconnect PVs, ... */
    public synchronized void stop()
    {
        logger.log(Level.FINE, "Stop: " + this + " on " + Thread.currentThread());
    	if (! running)
    		return;
    	for (Bypass bypass : mode_bypasses)
    		bypass.stop();
    	running = false;
    }

	/** @return Current list of bypasses */
    public synchronized Bypass[] getBypasses()
    {
    	return filtered_bypasses;
    }

    /** @return Total number of bypasses in selected machine mode */
	public synchronized int getTotal()
    {
    	return mode_bypasses.length;
    }

    /** @return Count */
	public synchronized int getBypassed()
    {
    	return bypassed;
    }

    /** @return Count */
	public synchronized int getBypassable()
    {
    	return bypassable;
    }

    /** @return Count */
	public synchronized int getNotBypassable()
    {
    	return not_bypassable;
    }

    /** @return Count */
	public synchronized int getDisconnected()
    {
    	return disconnected;
    }

    /** @return Count */
	public synchronized int getInError()
    {
    	return error;
    }

	/** Select the machine mode for which to show bypasses.
	 *
	 *  <p>This is a long running operation because it reads from the RDB.
	 *
	 *  <p>Can be called at any time, but will block until model
	 *  stopped, RDB read, model started.
	 *
	 *  @param monitor Progress monitor
	 *  @param mode
	 *  @see BypassModelListener#modelLoaded(BypassModel)
	 */
	public synchronized void selectMachineMode(final JobMonitor monitor, final MachineMode mode)
	{
	    logger.log(Level.FINE, "Select " + mode);
		monitor.beginTask("Clearing old information");
		stop();

		machine_mode = mode;
        mode_bypasses = new Bypass[] { new Bypass("Reading Bypass Info", mode.toString()) };
		filtered_bypasses = mode_bypasses;
		updateCounts();

		// Notify listeners
		for (BypassModelListener listener : listeners)
			listener.bypassesChanged();

		monitor.beginTask("Reading bypasses from RDB");
		Exception error = null;

		RDBConnectionPool rdb = null;
		try
		{
	        rdb = new RDBConnectionPool(MPSBypasses.url, MPSBypasses.user, MPSBypasses.password);
			final Bypass[] new_bypasses = readBypassInfo(monitor, rdb.getConnection(), mode);
			machine_mode = mode;
            mode_bypasses = new_bypasses;
		}
		catch (Exception ex)
		{
			error = ex;
	        mode_bypasses = new Bypass[0];
		}
		if (rdb != null)
		    rdb.clear();

		if (error != null)
		{
			// Notify listeners of error
			for (BypassModelListener listener : listeners)
				listener.modelLoaded(this, error);
			return;
		}

		updateCounts();
		filter();

		// Notify listeners
		for (BypassModelListener listener : listeners)
			listener.modelLoaded(this, null);

		start();
	}

	/** @return Currently selected machine mode of this model */
	public synchronized MachineMode getMachineMode()
    {
        return machine_mode;
    }

	/** Filter the <code>mode_bypasses</code>
	 *  by <code>state_filter</code>
	 *  and <code>request_filter</code>.
	 */
	private void filter()
    {
		// Fetch thread-safe copies
		final Bypass[] bypasses;
		final BypassState desired_state;
		final RequestState desired_request;
		synchronized (this)
        {
	        bypasses = mode_bypasses;
	        desired_state = state_filter;
	        desired_request = request_filter;
        }

		// Perform filtering
		final List<Bypass> filtered = new ArrayList<>();
		for (Bypass bypass : bypasses)
		{
			final boolean state_ok = desired_state == BypassState.All  ||  bypass.getState() == desired_state;
			if (!state_ok)
				continue;

			final boolean request_ok;
			switch (desired_request)
			{
			case Requested:
				request_ok = bypass.getRequest() != null;
				break;
			case NotRequested:
				request_ok = bypass.getRequest() == null;
				break;
			case All:
			default:
				request_ok = true;
			}
			if (request_ok)
				filtered.add(bypass);
		}
		// Update model
		synchronized (this)
        {
			filtered_bypasses = filtered.toArray(new Bypass[filtered.size()]);
        }
    }

	/** Set a filter on the bypass state,
	 *  i.e. only return info on bypasses in that state
	 *  @param state Desired state, which may be <code>BypassState.All</code> for all
	 *  @param request Desired request, which may be <code><RequestState.All/code> for all
	 */
	public void setFilter(final BypassState state, final RequestState request)
    {
	    logger.log(Level.FINE, "Filter on " + state + ", " + request);
		// Update model
		synchronized (this)
		{
			state_filter = state;
			request_filter = request;
		}

		filter();

		// Notify listeners
		for (BypassModelListener listener : listeners)
			listener.bypassesChanged();
    }

	/** @return Currently active bypass state filter
	 *  @see #setFilter(BypassState, RequestState)
	 */
	public synchronized BypassState getBypassFilter()
	{
		return state_filter;
	}

	/** @return Currently active request state filter
	 *  @see #setFilter(BypassState, RequestState)
	 */
	public synchronized RequestState getRequestFilter()
	{
		return request_filter;
	}

   /** Read bypass info from RDB using MPS tables
    *
    *  @param monitor Progress monitor
    *  @param connection RDB connection
    *  @param mode {@link MachineMode}
    *  @return {@link Bypass} array
    *  @throws Exception on error
    */
   private Bypass[] readBypassInfo(final JobMonitor monitor, final Connection connection, final MachineMode mode) throws Exception
   {
       monitor.beginTask("Read bypass requests");
       final RequestLookup requestors = new RequestLookup(monitor, connection);

       monitor.beginTask("Fetching bypass details from RDB...");
       final List<Bypass> bypasses = new ArrayList<>();

       try (PreparedStatement statement = connection.prepareStatement(
               "SELECT m.MPS_DVC_ID, m.DVC_ID, m.CHANNEL_NBR, c.MPS_CHAIN_ID, c.FPAR_FPL_CONFIG " +
               "FROM EPICS.machine_mode m " +
               "JOIN EPICS.mps_sgnl_param c ON m.DVC_ID = c.DVC_ID " +
               "WHERE m.chan_in_use_ind = 'Y'"  +
               " AND m.MPS_DVC_ID IS NOT NULL " +
               "ORDER BY m.MPS_DVC_ID"))
       {
           try (ResultSet result = statement.executeQuery())
           {
               int i = 0;

               while (result.next())
               {
                   if (i % 100 == 0)
                       monitor.beginTask("Read details for " + i + " bypasses");

                   final String device_id = result.getString(1);
                   final int port = result.getInt(3);
                   final String chain = result.getString(4);
                   final String ar_l_config = result.getString(5);
                   final boolean latch;
                   if (ar_l_config.equalsIgnoreCase("16FPAR"))
                       latch = false;
                   else if (ar_l_config.equalsIgnoreCase("16L"))
                       latch = true;
                   else if (ar_l_config.equalsIgnoreCase("8L8AR"))
                       latch = port >= 8;
                   else
                       throw new Exception("Unknown FPAR_FPL_CONFIG '" + ar_l_config + "' for '" + device_id + "'");

                   if (device_id.contains("_MPS:FPL_")  ||  device_id.contains("_MPS:FPAR_"))
                   {
                       logger.log(Level.WARNING, "Skipping legacy interlink " + device_id);
                       continue;
                   }

                   // Get request info
                   final Request request = requestors.getRequestor(device_id);

                   // For a signal ID 'Ring_Vac:SGV_AB:FPL_Ring_mm',
                   // the base name of the bypass PVs is
                   // 'Ring_Vac:SGV_AB:FPL_Ring',
                   // resulting in Bypass PVs
                   // 'Ring_Vac:SGV_AB:FPL_Ring_sw_jump_status' and
                   // 'Ring_Vac:SGV_AB:FPL_Ring_swmask'
                   final String pv_basename = device_id +
                                              (latch ? ":FPL_" : ":FPAR_") +
                                              chain;
                   final Bypass bypass = new Bypass(pv_basename, request, this);
                   bypasses.add(bypass);

                   ++i;
               }
           }
       }

       return bypasses.toArray(new Bypass[bypasses.size()]);
   }


	/** Read bypass info from RDB using stored procedure
	 *
	 *  Stored procedure looks for "*_mm" records in EPICS.sgnl_rec
	 *  It thus relies on the "crawler" to parse the records from MPS IOCs.
	 *
	 *  @param monitor Progress monitor
	 *  @param connection RDB connection
	 *  @param mode {@link MachineMode}
	 *  @return {@link Bypass} array
	 *  @throws Exception on error
	 */
	private Bypass[] readBypassInfoOld(final JobMonitor monitor, final Connection connection, final MachineMode mode) throws Exception
    {
		monitor.beginTask("Read bypass requests");
		final RequestLookup requestors = new RequestLookup(monitor, connection);

		String rdb_mode = mode.name();

		// if the machMode is Site, tell the RDB it's Tgt and don't add the 2nd input of "Y"
		if (mode == MachineMode.Site)
			rdb_mode = "Tgt";

		String sql = "{ ? = call epics.epics_mps_pkg.mps_signals_to_audit(?";
		if(mode != MachineMode.Site)
			sql=sql+",?) }";
		else
			sql=sql+") }";
		final CallableStatement procedure = connection.prepareCall(sql);

		// Request the array of the MPS Mode Mask Table, based on the input Machine Mode
		procedure.registerOutParameter(1, Types.ARRAY,"EPICS.MPS_MODE_MASK_TAB");
		procedure.setString(2, rdb_mode);
		if(mode != MachineMode.Site)
			procedure.setString(3, "Y");

		monitor.beginTask("Fetching bypass details from RDB...");
		procedure.execute();

		// Store the retrieved MPS Mode Mask Table array
		final List<Bypass> bypasses = new ArrayList<>();
		final Object[] result = (Object[]) procedure.getArray(1).getArray();
		// retrieve the signal id read from the RDB array for gathering bypass information
		for (int index = 0; index < result.length; index++)
		{
			if (index % 100 == 0)
				monitor.beginTask("Read details for " + index + " bypasses");

			final Struct element = (Struct) result[index];
			final Object[] attributes = element.getAttributes();
			if (attributes.length < 1)
				continue;

			final Object sig_id_obj = attributes[0];
			if (sig_id_obj == null)
				continue;

			// The 'signal ID' will be 'Ring_Vac:SGV_AB:FPL_Ring_mm'
			// Get device ID 'Ring_Vac:SGV_AB'
			final String signal_id = sig_id_obj.toString();
			final int sep = signal_id.lastIndexOf(':');
			final String device_id;
			if (sep > 0)
				device_id = signal_id.substring(0, sep);
			else
				device_id = signal_id;

			// Get request info
			final Request request = requestors.getRequestor(device_id);

			// For a signal ID 'Ring_Vac:SGV_AB:FPL_Ring_mm',
			// the base name of the bypass PVs is
			// 'Ring_Vac:SGV_AB:FPL_Ring',
			// resulting in Bypass PVs
			// 'Ring_Vac:SGV_AB:FPL_Ring_sw_jump_status' and
			// 'Ring_Vac:SGV_AB:FPL_Ring_swmask'
			final int mm = signal_id.lastIndexOf("_mm");
			if (mm <= 0)
				continue;
			final String pv_basename = signal_id.substring(0, mm);
			final Bypass bypass = new Bypass(pv_basename, request, this);
			bypasses.add(bypass);
			// System.out.println(bypass);
		}
		procedure.close();

		return bypasses.toArray(new Bypass[bypasses.size()]);
    }

	/** Update the counts for bypassed ... error */
	private void updateCounts()
    {
		// Fetch thread-safe copies
		final Bypass[] bypasses;
		synchronized (this)
        {
	        bypasses = mode_bypasses;
        }
        int bypassed = 0;
        int bypassable = 0;
        int not_bypassable = 0;
        int disconnected = 0;
        int error = 0;
    	for (Bypass bypass : bypasses)
    	{
    		switch (bypass.getState())
    		{
    		case Bypassed:
    			++bypassed;
    			break;
    		case Bypassable:
    			++bypassable;
    			break;
    		case NotBypassable:
    			++not_bypassable;
    			break;
    		case Disconnected:
    			++disconnected;
    			break;
    		default:
    			++error;
    		}
    	}
		// Update model
    	synchronized (this)
        {
	        this.bypassed = bypassed;
	        this.bypassable = bypassable;
	        this.not_bypassable = not_bypassable;
	        this.disconnected = disconnected;
	        this.error = error;
        }
    }

	/** @see BypassListener */
	@Override
    public void bypassChanged(final Bypass bypass)
    {
		updateCounts();
		if (state_filter == BypassState.All)
		{	// Update single bypass
			for (BypassModelListener listener : listeners)
				listener.bypassChanged(bypass);
		}
		else
		{	// We're not displaying all bypasses,
			// so need to filter
			filter();
			// .. and that might have changed what we see,
			// so trigger full update
			for (BypassModelListener listener : listeners)
				listener.bypassesChanged();
		}
    }

	@Override
	public synchronized String toString()
	{
	    final StringBuilder buf = new StringBuilder();

	    buf.append("BypassModel ").append(machine_mode).append(": ");
	    buf.append(mode_bypasses.length).append(" bypasses @ ").append(System.identityHashCode(mode_bypasses));
	    if (running)
	        buf.append(" (running)");

	    return buf.toString();
	}
}

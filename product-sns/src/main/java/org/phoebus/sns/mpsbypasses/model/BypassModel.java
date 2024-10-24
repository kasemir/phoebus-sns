/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.model;

import static org.phoebus.sns.mpsbypasses.MPSBypasses.logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.phoebus.framework.jobs.JobMonitor;
import org.phoebus.framework.persistence.XMLUtil;
import org.phoebus.framework.rdb.RDBConnectionPool;
import org.phoebus.sns.mpsbypasses.MPSBypasses;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
     *  @see BypassModelListener#modelLoaded(BypassModel)
     */
    public synchronized void selectMachineMode(final JobMonitor monitor)
    {
        logger.log(Level.FINE, "Read bypasses");
        monitor.beginTask("Clearing old information");
        stop();

        mode_bypasses = new Bypass[] { new Bypass("Reading Bypass Info", "") };
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
            final Bypass[] new_bypasses = readBypassInfo(monitor, rdb.getConnection());
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
     *  @return {@link Bypass} array
     *  @throws Exception on error
     */
    private Bypass[] readBypassInfo(final JobMonitor monitor, final Connection connection) throws Exception
    {
        monitor.beginTask("Read bypass requests");
        final RequestLookup requestors = new RequestLookup(monitor, connection);

        monitor.beginTask("Fetching bypass details from RDB...");

        final List<Bypass> bypasses = new ArrayList<>();
        try
        {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(MPSBypasses.mps_config_file);
            doc.getDocumentElement().normalize();
            final Element root_node = doc.getDocumentElement();

            if (! root_node.getNodeName().equals("mps"))
                throw new Exception("Expected <mps>, got <" + root_node.getNodeName());
            
            int count = 0;
            for (Element pe : XMLUtil.getChildElements(root_node, "processor"))
            {
                final int np = Integer.parseInt(pe.getAttribute("id"));
                for (Element ne : XMLUtil.getChildElements(pe, "node"))
                {
                    final int fn = Integer.parseInt(ne.getAttribute("id")) % 10;
                    for (Element ce : XMLUtil.getChildElements(ne, "channel"))
                    {
                        final int ch = Integer.parseInt(ce.getAttribute("id"));
                        final String name = ce.getAttribute("name");
                        if (name.isBlank())
                            continue;
                        if (count % 100 == 0)
                            monitor.beginTask("Read details for " + count + " bypasses");
                        ++count;

                        // Get request info
                        final Request request = requestors.getRequestor(name);
                        final Bypass bypass = new Bypass(np, fn, ch, name, request, this);
                        bypasses.add(bypass);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Error reading MPS config file '" + MPSBypasses.mps_config_file + "'", ex);
            return new Bypass[0];
        }

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
        {    // Update single bypass
            for (BypassModelListener listener : listeners)
                listener.bypassChanged(bypass);
        }
        else
        {    // We're not displaying all bypasses,
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

        buf.append("BypassModel: ");
        buf.append(mode_bypasses.length).append(" bypasses @ ").append(System.identityHashCode(mode_bypasses));
        if (running)
            buf.append(" (running)");

        return buf.toString();
    }
}

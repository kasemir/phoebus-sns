/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.model;

import static org.phoebus.sns.mpsbypasses.MPSBypasses.logger;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.epics.vtype.VEnum;
import org.epics.vtype.VNumber;
import org.epics.vtype.VType;
import org.phoebus.pv.PV;
import org.phoebus.pv.PVPool;
import org.phoebus.sns.mpsbypasses.MPSBypasses;

import io.reactivex.rxjava3.disposables.Disposable;

/** Info about one Bypass
 *
 *  <p>Combines the 'live' info from PVs with the 'static'
 *  info from the RDB
 *
 *  <p>Given a base PV name like 'Ring_Vac:SGV_AB',
 *  it will connect to the PVs 'Ring_Vac:SGV_AB:sw_jump_status'
 *  and 'Ring_Vac:SGV_AB:swmask'
 *  to determine if the bypass is possible (jumper)
 *  and actually masked (software mask),
 *  summarizing that as the live {@link BypassState}
 *
 *  @author Delphy Armstrong - Original MPSBypassInfo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Bypass
{
	final private String name;
	final private Request request;
	final private BypassListener listener;

	private PV jumper_pv, mask_pv;
	private Disposable jumper_pv_listener, mask_pv_listener;

    private volatile VType jumper, mask;
	private volatile BypassState state = BypassState.Disconnected;

	/** Initialize
	 *  @param name Name, e.g. "Ring_Vac:SGV_AB"
	 *  @param request Who requested the bypass? <code>null</code> if not requested.
	 *  @param listener {@link BypassListener}
	 *  @throws Exception on error
	 */
	public Bypass(final String name, final Request request,
			final BypassListener listener) throws Exception
	{
		this.name = name;
		this.request = request;
		this.listener = listener;
	}

	/** Create a pseudo-Bypass that is used to display
	 *  messages in the bypass table
	 *  @param message Message
	 *  @param detail Detail that will show in ()
	 */
	public Bypass(final String message, final String detail)
	{
		name = message + " (" + detail + ")";
		request = null;
		listener = null;
		jumper_pv = null;
		mask_pv = null;
	}

	/** @return Bypass name, for example "Ring_Vac:SGV_AB" */
	public String getName()
	{
		return name;
	}

    /** @return Name of the Jumper PV, for example "Ring_Vac:SGV_AB:sw_jump_status" */
    public String getJumperPVName()
    {
        return name + ":sw_jump_status";
    }

    /** @return Name of the Mask PV, for example "Ring_Vac:SGV_AB:swmask" */
    public String getMaskPVName()
    {
        return name + ":swmask";
    }

	/** @return Request for this bypass or <code>null</code> */
	public Request getRequest()
	{
		return request;
	}

	/** @return Bypass state */
	public BypassState getState()
	{
		return state;
	}

	/** Connect to PVs */
	public void start()
	{
		if (name == null)
			return;

		try
		{
    		jumper_pv = PVPool.getPV(getJumperPVName());
    		jumper_pv_listener = jumper_pv.onValueEvent()
    		                              .throttleLatest(MPSBypasses.update_throttle_ms, TimeUnit.MILLISECONDS)
    		                              .subscribe(value ->
            {
                if (PV.isDisconnected(value))
                {
                    logger.log(Level.WARNING, "Jumper PV Disconnected: " + jumper_pv.getName());
                    updateState(null, mask);
                }
                else
                    updateState(value, mask);
            });

    		mask_pv = PVPool.getPV(getMaskPVName());
    		mask_pv_listener = mask_pv.onValueEvent()
                        		      .throttleLatest(MPSBypasses.update_throttle_ms, TimeUnit.MILLISECONDS)
                                      .subscribe(value ->
            {
                if (PV.isDisconnected(value))
                {
                    logger.log(Level.WARNING, "Mask PV Disconnected: " + mask_pv.getName());
                    updateState(jumper, null);
                }
                else
                    updateState(jumper, value);
            });
		}
		catch (Exception ex)
		{
		    logger.log(Level.WARNING, "Cannot start " + this, ex);
		}
	}

	/** Disconnect PVs */
	public void stop()
	{
		if (name == null)
			return;

		mask_pv_listener.dispose();
	    jumper_pv_listener.dispose();
		PVPool.releasePV(jumper_pv);
		PVPool.releasePV(mask_pv);

		state = BypassState.Disconnected;
		// Does NOT notify listener
		// because the way this is used the listener
		// will soon see a different list of bypasses
		// or close down.
		// Either way, no update needed.
	}

	/** Update alarm state from current values of PVs
	 *  @param jumper Value of jumper PV
	 *  @param mask Value of mask PV
	 */
	private void updateState(final VType jumper, final VType mask)
    {
	    this.jumper = jumper;
	    this.mask = mask;
		// Anything unknown?
		if (mask == null  ||  jumper == null)
			state = BypassState.Disconnected;
		else
		{	// Determine state
			final boolean jumpered = getNumber(jumper) > 0;
			final boolean masked = getNumber(mask) > 0;

			if (jumpered)
			{
				if (masked)
					state = BypassState.Bypassed;
				else
					state = BypassState.Bypassable;
			}
			else
			{
				if (masked)
					state = BypassState.InError;
				else
					state = BypassState.NotBypassable;
			}
		}

	    // send update
		listener.bypassChanged(this);
    }

	/** @param value {@link VType}
	 *  @return Integer extracted from VType
	 */
	private int getNumber(final VType value)
    {
	    if (value instanceof VNumber)
	        return ((VNumber)value).getValue().intValue();
	    else if (value instanceof VEnum)
	        return ((VEnum)value).getIndex();
        return -1;
    }

    /** @return Debug representation */
	@Override
    public String toString()
    {
	    return "Bypass " + name + ", state " + state +
	        ", Jumper " + getJumperPVName() +
	        ", Mask " + getMaskPVName() +	        
	        ", requested by " + (request != null ? request : "nobody");
    }
}

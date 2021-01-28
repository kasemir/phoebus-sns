/*******************************************************************************
 * Copyright (c) 2019-2021 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.modes;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.vtype.VEnum;
import org.phoebus.pv.PV;
import org.phoebus.pv.PVPool;
import org.phoebus.sns.mpsbypasses.MPSBypasses;

import io.reactivex.rxjava3.disposables.Disposable;

/** Read beam mode from MPS PVs
 *
 *  <p>This is convoluted because instead of one PV to indicate the current mode,
 *  there are N PVs to reflect the on/off state of the possible modes,
 *  with only one PV supposed to be active at a given time.
 *
 *  <p>Additionally, the sense of 'active' differs for the RDTL vs. Switch mode PVs.
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamModeMonitor
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    final private BeamMode[] modes = BeamMode.values();
    final private BeamModeListener listener;

    private final PV[] rtdl_pv = new PV[modes.length];
    private final PV[] switch_pv = new PV[modes.length];

    private final Disposable[] rtdl_listener = new Disposable[modes.length];
    private final Disposable[] switch_listener = new Disposable[modes.length];

    private final VEnum[] rtdl_value = new VEnum[modes.length];
    private final VEnum[] switch_value = new VEnum[modes.length];

	private volatile BeamMode rtdl_mode = null;
	private volatile BeamMode switch_mode = null;

	/** Initialize
	 *  @param listener
	 */
	public BeamModeMonitor(final BeamModeListener listener)
	{
	    this.listener = listener;
	}

	/** Connect PVs
	 *  @throws Exception on error
	 */
	public void start() throws Exception
	{
        // Handle 'RTDL' PVs
        for (int i=0; i<modes.length; ++i)
        {
            final int pv_i = i;
            rtdl_pv[i] = PVPool.getPV("ICS_MPS:RTDL_BmMd:" + modes[i].getSignal());
            rtdl_listener[i] = rtdl_pv[i].onValueEvent()
                                         .throttleLatest(MPSBypasses.update_throttle_ms, TimeUnit.MILLISECONDS)
                                         .subscribe(value ->
            {
                if (PV.isDisconnected(value))
                {
                    logger.log(Level.WARNING, "Disconnected: ", rtdl_pv[pv_i].getName());
                    rtdl_value[pv_i] = null;
                    updateModes(null, switch_mode);
                }
                else if (value instanceof VEnum)
                {
                    rtdl_value[pv_i] = (VEnum) value;
                    final BeamMode mode = getSelectedMode(rtdl_value, 1);
                    logger.log(Level.FINE, "RTDL Mode: {0}", mode);
                    updateModes(mode, switch_mode);
                }
            });
        }

        // Handle 'Switch' PVs
        for (int i=0; i<modes.length; ++i)
        {
            final int pv_i = i;
            switch_pv[i] = PVPool.getPV("ICS_MPS:Switch_BmMd:" + modes[i].getSignal());
            switch_listener[i] = switch_pv[i].onValueEvent()
                                             .throttleLatest(MPSBypasses.update_throttle_ms, TimeUnit.MILLISECONDS)
                                             .subscribe(value ->
            {
                if (PV.isDisconnected(value))
                {
                    logger.log(Level.WARNING, "Disconnected: ", switch_pv[pv_i].getName());
                    switch_value[pv_i] = null;
                    updateModes(rtdl_mode, null);
                }
                else if (value instanceof VEnum)
                {
                    switch_value[pv_i] = (VEnum) value;
                    final BeamMode mode = getSelectedMode(switch_value, 0);
                    logger.log(Level.FINE, "Switch Mode: {0}", mode);
                    updateModes(rtdl_mode, mode);
                }
            });
        }
	}

	/** Determine which of the values indicates an active mode
	 *  @param values Values of the mode PVs
	 *  @param active_value Value that indicates the active mode
	 *  @return Selected {@link BeamMode} or <code>null</code>
	 */
	private BeamMode getSelectedMode(final VEnum[] values, final int active_value)
	{
	    if (values == null)
	        return null;

	    if (values.length != modes.length)
	        throw new IllegalStateException();

	    int active = -1;
	    for (int i=0; i<modes.length; ++i)
	    {
	        final VEnum value = values[i];
            // At least one disconnected PV -> state not known
            if (value == null)
                return null;
            if (value.getIndex() == active_value)
	        {
	            if (active >= 0)
	            {
	                Logger.getLogger(getClass().getName()).
	                    log(Level.WARNING,
	                        "Both {0} and {1} active at the same time",
	                        new Object[] { modes[active], modes[i] });
	                return null;
	            }
	            active = i;
	        }
	    }

	    if (active >= 0)
	        return modes[active];
	    return null;
	}

	/** Disconnect PVs */
	public void stop()
	{
        for (int i=0; i < switch_pv.length; ++i)
        {
            switch_listener[i].dispose();
            PVPool.releasePV(switch_pv[i]);

            rtdl_listener[i].dispose();
            PVPool.releasePV(rtdl_pv[i]);
        }
		updateModes(null, null);
	}

	/** Update modes and notify listeners on change
	 *  @param new_rtdl_mode
	 *  @param new_switch_mode
	 */
	private void updateModes(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.beamModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}

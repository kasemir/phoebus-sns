/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.modes;

/** Beam mode listener
 *  @author Kay Kasemir
 */
public interface BeamModeListener
{
	/** Invoked when the beam mode readbacks change
	 *  @param new_rtdl_mode Beam mode based on RTDL info
	 *  @param new_switch_mode Beam mode based on MPS switch readings
	 */
	public void beamModeUpdate(BeamMode new_rtdl_mode, BeamMode new_switch_mode);
}

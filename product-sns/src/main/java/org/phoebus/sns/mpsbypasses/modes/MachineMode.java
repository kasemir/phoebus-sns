/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.modes;

/** Enumerations for the different MPS Bypass Machine Modes
 *
 *  @author Delphy Armstrong - original
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum MachineMode
{
	MEBT_BS("MEBT Beam Stop"),
	CCL_BS("CCL Beam Stop"),
	LinDmp("Linac Dump"),
	InjDmp("Injection Dump"),
	Ring("Ring"),
	ExtDmp("Extraction Dump"),
	Tgt("Target");

	/** Human-readable representation */
	final private String label;

	/** Initialize
	 *  @param label Human-readable representation
	 */
	private MachineMode(final String label)
	{
		this.label = label;
	}

	/** @return Human-readable representation */
	@Override
    public String toString()
	{
		return label;
	}
}

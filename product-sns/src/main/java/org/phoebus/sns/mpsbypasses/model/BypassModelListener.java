/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.model;

import org.phoebus.sns.mpsbypasses.modes.MachineMode;

/** Listener to updates from {@link BypassModel}
 *  @author Kay Kasemir
 */
public interface BypassModelListener extends BypassListener
{
	/** The model loaded all the bypass info from the RDB,
	 *  it's ready to be started.
	 *
	 *  @param model Model that has loaded
	 *  @param error Exception that happened while loading model,
	 *               or <code>null</code> if OK
	 *  @see BypassModel#selectMachineMode(MachineMode)
	 */
	void modelLoaded(final BypassModel model, Exception error);

	/** Invoked when bypass counts have changed:
	 *  A previously filtered bypass because visible
	 *  or vice versa, so an overall refresh is needed.
	 */
	void bypassesChanged();
}

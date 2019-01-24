/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.model;

/** Listener to {@link Bypass} changes
 *  @author Kay Kasemir
 */
public interface BypassListener
{
	/** Invoked when a {@link Bypass} changes its state
	 *  @param bypass Bypass that changed its state
	 */
	public void bypassChanged(Bypass bypass);
}

/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.sns.mpsbypasses.model.Bypass;

import javafx.scene.control.TableCell;

/** Table cell that sets background color based on bypass and request state
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StateCell extends TableCell<BypassRow, String>
{
    @Override
    public void updateItem(final String item, final boolean empty)
    {
        super.updateItem(item, empty);
        if (empty  ||  item == null)
        {
            setText("");
            setBackground(null);
        }
        else
        {
            setText(item);
            if (getTableRow() == null   ||  getTableRow().getItem() == null)
                setBackground(null);
            else
            {
                final Bypass bypass = getTableRow().getItem().bypass;
                setBackground(BypassColors.getBypassColor(bypass.getState(), bypass.getRequest() != null));
            }
        }
    }
}

/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.ui;

import javafx.scene.control.TableCell;

/** Table cell indicates the table row
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RowIndexCell extends TableCell<BypassRow, String>
{
    @Override
    public void updateItem(final String item, final boolean empty)
    {
        super.updateItem(item, empty);

        if (empty  ||  getTableRow() == null)
            setText("");
        else
            setText(Integer.toString(getTableRow().getIndex() + 1));
    }
}

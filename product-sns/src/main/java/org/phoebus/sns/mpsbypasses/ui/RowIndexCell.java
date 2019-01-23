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

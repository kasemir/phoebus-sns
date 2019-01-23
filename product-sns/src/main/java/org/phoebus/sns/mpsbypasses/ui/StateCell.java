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

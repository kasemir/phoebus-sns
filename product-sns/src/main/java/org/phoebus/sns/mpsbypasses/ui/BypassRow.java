package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.sns.mpsbypasses.model.Request;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** One row in bypass table */
@SuppressWarnings("nls")
class BypassRow
{
    public final Bypass bypass;
    public final StringProperty name;
    public final StringProperty state = new SimpleStringProperty();
    public final StringProperty requestor = new SimpleStringProperty();
    public final StringProperty date = new SimpleStringProperty();

    public BypassRow(final Bypass bypass)
    {
        this.bypass = bypass;
        name = new SimpleStringProperty(bypass.getFullName());
        update();
    }

    public void update()
    {
        state.set(bypass.getState().toString());
        final Request request = bypass.getRequest();
        if (request == null)
        {
            requestor.set("-- none --");
            date.set("");
        }
        else
        {
            requestor.set(request.getRequestor());
            date.set(request.getDate().toString());
        }
    }
}

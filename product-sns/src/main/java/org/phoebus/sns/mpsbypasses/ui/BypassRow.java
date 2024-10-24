/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
        name = new SimpleStringProperty(bypass.getName());
        update();
    }

    public void update()
    {
        state.set(bypass.getState().toString());
        final Request request = bypass.getRequest();
        if (request == null)
        {
            requestor.set("");
            date.set("");
        }
        else
        {
            requestor.set(request.getRequestor());
            date.set(request.getDate().toString());
        }
    }
}

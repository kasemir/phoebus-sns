/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.ui.application.PhoebusApplication;
import org.phoebus.ui.javafx.ImageCache;

import javafx.scene.control.MenuItem;

@SuppressWarnings("nls")
public class OpenWeb extends MenuItem
{
    public OpenWeb(String title, String url)
    {
        super(title, ImageCache.getImageView(ImageCache.class, "/icons/web.png"));
        setOnAction(event -> PhoebusApplication.INSTANCE.getHostServices().showDocument(url));
    }
}

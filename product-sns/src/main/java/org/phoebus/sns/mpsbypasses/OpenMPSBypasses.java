/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses;

import org.phoebus.framework.workbench.ApplicationService;
import org.phoebus.ui.javafx.ImageCache;
import org.phoebus.ui.spi.MenuEntry;

import javafx.scene.image.Image;

@SuppressWarnings("nls")
public class OpenMPSBypasses implements MenuEntry
{
    @Override
    public String getName()
    {
        return MPSBypasses.DISPLAY_NAME;
    }

    @Override
    public Image getIcon()
    {
        return ImageCache.getImage(MPSBypasses.class, "/icons/mpsbypasses.png");
    }

    @Override
    public String getMenuPath()
    {
        return "Display";
    }

    @Override
    public Void call() throws Exception
    {
        ApplicationService.createInstance(MPSBypasses.NAME);
        return null;
    }
}
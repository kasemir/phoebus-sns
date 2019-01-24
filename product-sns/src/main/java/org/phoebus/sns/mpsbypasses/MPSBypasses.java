/*******************************************************************************
 * Copyright (c) 2019 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses;

import java.net.URL;
import java.util.logging.Logger;

import org.phoebus.framework.preferences.PreferencesReader;
import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;

@SuppressWarnings("nls")
public class MPSBypasses implements AppDescriptor
{
    public static final Logger logger = Logger.getLogger(MPSBypasses.class.getPackageName());

    public static final String NAME = "mps_bypasses";
    public static final String DISPLAY_NAME = "MPS Bypasses";

    public static final long update_throttle_ms = 500;
    public static final String url, user, password;
    public static final String url_enter_bypass, url_view_bypass;

    static
    {
        final PreferencesReader prefs = new PreferencesReader(MPSBypasses.class, "/mpsbypasses_preferences.properties");
        url = prefs.get("url");
        user = prefs.get("user");
        password = prefs.get("password");
        url_enter_bypass = prefs.get("url_enter_bypass");
        url_view_bypass = prefs.get("url_view_bypass");
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDisplayName()
    {
        return DISPLAY_NAME;
    }

    @Override
    public URL getIconURL()
    {
        return getClass().getResource("/icons/mpsbypasses.png");
    }

    @Override
    public AppInstance create()
    {
        // Create the singleton instance or show existing one
        if (MPSBypassesInstance.INSTANCE == null)
            MPSBypassesInstance.INSTANCE = new MPSBypassesInstance(this);
        else
            MPSBypassesInstance.INSTANCE.raise();
        return MPSBypassesInstance.INSTANCE;
    }
}

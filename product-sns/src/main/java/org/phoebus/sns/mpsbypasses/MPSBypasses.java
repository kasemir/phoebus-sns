package org.phoebus.sns.mpsbypasses;

import java.net.URL;

import org.phoebus.framework.preferences.PreferencesReader;
import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;

@SuppressWarnings("nls")
public class MPSBypasses implements AppDescriptor
{
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

package org.phoebus.sns.mpsbypasses;

import org.phoebus.framework.preferences.PreferencesReader;

@SuppressWarnings("nls")
public class MPSBypasses
{
    public static final long update_throttle_ms = 500;
    public static final String url, user, password;

    static
    {
        final PreferencesReader prefs = new PreferencesReader(MPSBypasses.class, "/mpsbypasses_preferences.properties");
        url = prefs.get("url");
        user = prefs.get("user");
        password = prefs.get("password");
    }
}

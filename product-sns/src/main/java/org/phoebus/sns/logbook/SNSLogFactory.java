package org.phoebus.sns.logbook;

import org.phoebus.framework.preferences.PreferencesReader;
import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogFactory;
import org.phoebus.security.tokens.SimpleAuthenticationToken;

public class SNSLogFactory implements LogFactory
{
    private static final PreferencesReader prefs = new PreferencesReader(SNSLogFactory.class, "/log_preferences.properties");
    private static final String rdb_url = prefs.get("rdb_url");
    private static final String read_only_username = prefs.get("read_only_username");
    private static final String read_only_password = prefs.get("read_only_password");
    
    private static final String ID = "org.phoebus.sns.logbook";
    
    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public LogClient getLogClient()
    {
        return new SNSLogbookClient(rdb_url, read_only_username, read_only_password);
    }

    @Override
    public LogClient getLogClient(Object authToken)
    {
        SimpleAuthenticationToken simpleAuth = (SimpleAuthenticationToken) authToken;
        return new SNSLogbookClient(rdb_url, simpleAuth.getUsername(), simpleAuth.getPassword());
    }
}

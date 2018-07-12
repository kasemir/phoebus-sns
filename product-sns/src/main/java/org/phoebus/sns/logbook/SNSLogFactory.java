package org.phoebus.sns.logbook;

import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogFactory;

public class SNSLogFactory implements LogFactory
{
    private static final String ID = "org.phoebus.sns.logbook";
    
    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public LogClient getLogClient()
    {
        // return getLogClient(prefs.get("log_list_user", "log_list_password"))
        return null;
    }
    
    public LogClient getLogClient(final String user, final String password)
    {
        // return new SNSLogbookClient(user, password);
        return null;
    }

    @Override
    public LogClient getLogClient(Object authToken)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

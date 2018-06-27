package org.phoebus.sns.logbook;

import org.phoebus.logging.Logbook;

public class SNSLogbook implements Logbook
{
    final private String name;
    
    public SNSLogbook(final String name)
    {
        this.name = name;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getOwner()
    {
        return "";
    }
    
    @Override
    public String toString()
    {
        return "Logbook '" + name + "'";
    }

}

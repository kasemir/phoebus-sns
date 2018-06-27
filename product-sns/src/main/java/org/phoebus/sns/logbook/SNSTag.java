package org.phoebus.sns.logbook;

import org.phoebus.logging.Tag;

public class SNSTag implements Tag
{
    final private String name;
    
    public SNSTag(final String name) 
    {
        this.name = name;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getState()
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        return "Tag '" + name + "'";
    }
}

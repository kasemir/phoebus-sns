package org.phoebus.applications.eliza;

import java.net.URL;

import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;

import Eliza.ElizaMain;

@SuppressWarnings("nls")
public class ElizaApp implements AppDescriptor
{
    static final String DISPLAY_NAME = "AI Assistant";
    static final String NAME = "eliza";

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
        return ElizaMain.class.getResource("/Eliza/eliza.png");
    }

    @Override
    public AppInstance create()
    {
        // Create the singleton instance or show existing one
        if (ElizaInstance.INSTANCE == null)
            ElizaInstance.INSTANCE = new ElizaInstance(this);
        else
            ElizaInstance.INSTANCE.raise();
        return ElizaInstance.INSTANCE;
    }
}

package org.phoebus.applications.eliza;

import org.phoebus.framework.workbench.ApplicationService;
import org.phoebus.ui.javafx.ImageCache;
import org.phoebus.ui.spi.MenuEntry;

import Eliza.ElizaMain;
import javafx.scene.image.Image;

@SuppressWarnings("nls")
public class OpenEliza implements MenuEntry
{
    @Override
    public String getName()
    {
        return ElizaApp.DISPLAY_NAME;
    }

    @Override
    public Image getIcon()
    {
        return ImageCache.getImage(ElizaMain.class, "/Eliza/eliza.png");
    }

    @Override
    public String getMenuPath()
    {
        return "Utility";
    }

    @Override
    public Void call() throws Exception
    {
        ApplicationService.createInstance(ElizaApp.NAME);
        return null;
    }
}

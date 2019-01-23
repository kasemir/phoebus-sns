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
        // TODO Auto-generated method stub
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
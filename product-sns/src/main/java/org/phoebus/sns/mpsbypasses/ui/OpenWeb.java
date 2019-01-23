package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.ui.application.PhoebusApplication;
import org.phoebus.ui.javafx.ImageCache;

import javafx.scene.control.MenuItem;

@SuppressWarnings("nls")
public class OpenWeb extends MenuItem
{
    public OpenWeb(String title, String url)
    {
        super(title, ImageCache.getImageView(ImageCache.class, "/icons/web.png"));
        setOnAction(event -> PhoebusApplication.INSTANCE.getHostServices().showDocument(url));
    }
}

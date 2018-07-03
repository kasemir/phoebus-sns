package org.phoebus.sns.logbook.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class LogEntryAttachmentsView extends TabPane
{
    private final LogEntryModel model;
    private final LogImagesTab  images;
    private final Tab           files, properties;
    
    public LogEntryAttachmentsView(final LogEntryModel model)
    {
        this.model = model;
        images     = new LogImagesTab(model);
        files      = new Tab("Files");
        properties = new Tab("Properties");
        
        formatView();
    }

    private void formatView()
    {
        getTabs().addAll(images, files, properties);
    }
}

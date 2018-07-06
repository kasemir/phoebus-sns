package org.phoebus.sns.logbook.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class LogEntryAttachmentsView extends TabPane
{
    @SuppressWarnings("unused")
    private final LogEntryModel model;
    private final LogImagesTab  images;
    private final Tab           files, properties;
    
    public LogEntryAttachmentsView(final LogEntryModel model)
    {
        super();
        this.model = model;
        images     = new LogImagesTab(model);
        files      = new LogFilesTab(model);
        properties = new Tab("Properties");
        
        formatView();
    }

    private void formatView()
    {
        // Anything else to do?
        getTabs().addAll(images, files, properties);
    }
}

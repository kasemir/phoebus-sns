package org.phoebus.sns.logbook.ui;

import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

public class LogEntryAttachmentsView extends Accordion
{
    private final TabPane       tabPane;
    @SuppressWarnings("unused")
    private final LogEntryModel model;
    private final LogImagesTab  images;
    private final Tab           files, properties;
    
    public LogEntryAttachmentsView(final LogEntryModel model)
    {
        super();
        this.model = model;
        tabPane    = new TabPane();
        images     = new LogImagesTab(model);
        files      = new LogFilesTab(model);
        properties = new LogPropertiesTab();
        
        formatView();
    }

    private void formatView()
    {
        tabPane.getTabs().addAll(images, files, properties);
        TitledPane tPane = new TitledPane("Attachments", tabPane);
        this.getPanes().add(tPane);
    }
}

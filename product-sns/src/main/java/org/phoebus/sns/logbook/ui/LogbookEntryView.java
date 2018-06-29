package org.phoebus.sns.logbook.ui;

import javafx.scene.layout.VBox;

public class LogbookEntryView extends VBox
{
    private final LogEntryModel          model;
    
    private final LogCredentialEntryView credentialEntry;
    private final LogDateLevelView       dateAndLevel;
    private final LogEntryFieldsView     logEntryFields;
    private final LogbooksTagsView       logbooksAndTags;
    
    public LogbookEntryView()
    {
        model = new LogEntryModel();
        
        // user name and password label and fields.
        credentialEntry = new LogCredentialEntryView(model);
        
        // date and level labels, fields, and selectors.
        dateAndLevel = new LogDateLevelView();
        
        // title and text labels and fields.
        logEntryFields = new LogEntryFieldsView();
        
        // log books and tags text field, selector, and addition view button
        logbooksAndTags =  new LogbooksTagsView(model);
        
        getChildren().addAll(credentialEntry, dateAndLevel, logEntryFields, logbooksAndTags);
    }
}
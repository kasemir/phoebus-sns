/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.Collection;

import org.phoebus.framework.preferences.PhoebusPreferenceService;
import org.phoebus.logging.LogEntry;
import org.phoebus.logging.LogFactory;
import org.phoebus.logging.LogService;
import org.phoebus.logging.Logbook;
import org.phoebus.logging.Tag;
import org.phoebus.ui.dialog.DialogHelper;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Dialog for making an entry into a log book.
 * @author Evan Smith
 */
public class LogbookEntryDialog extends Dialog<LogEntry>
{
    /** Width of labels on views leftmost column. */
    public static final int labelWidth = 80;
    
    private static final LogService logService = LogService.getInstance();
    
    /** Purveyor of log entry application state. */
    private final LogEntryModel           model;
    
    /** Dialog Content */
    private final VBox                    content;
    
    /** View handles user credential entry for access to log. */
    private final LogCredentialEntryView  credentialEntry;
    
    /** View handles displaying of date and log entry level selection. */
    private final LogDateLevelView        dateAndLevel;
    
    /** View handles the input for creation of the entry. */
    private final LogEntryFieldsView      logEntryFields;
        
    /** View handles addition of log entry attachments. */
    private final LogEntryAttachmentsView attachmentsView;
    
    /** Button type for submitting log entry. */
    private final ButtonType submit;

    public LogbookEntryDialog(final Node parent, LogEntry template)
    {   
        model = new LogEntryModel(parent);
        initModel(template);
        
        content = new VBox();
        
        // user name and password label and fields.
        credentialEntry = new LogCredentialEntryView(model);
        
        // date and level labels, fields, and selectors.
        dateAndLevel = new LogDateLevelView(model);
        
        // title and text labels and fields.
        logEntryFields = new LogEntryFieldsView(model);
                
        // Images, Files, Properties
        attachmentsView = new LogEntryAttachmentsView(model);        
        
        // Let the Text Area grow to the bottom.
        VBox.setVgrow(logEntryFields,  Priority.ALWAYS);

        VBox.setMargin(credentialEntry,       new Insets(10, 0,  0, 0));
        VBox.setMargin(logEntryFields,        new Insets( 0, 0, 10, 0));
        
        content.setSpacing(10);
        content.getChildren().addAll(credentialEntry, dateAndLevel, logEntryFields, attachmentsView);
        
        getDialogPane().setContent(content);
        
        submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        
        setResizable(true);
        
        DialogHelper.positionAndSize(this, parent,
                PhoebusPreferenceService.userNodeForClass(LogbookEntryDialog.class),
                800, 1000);

        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, submit);
        
        setResultConverter(button ->
        {
            return button == submit ? model.getEntry() : null;
        });
    }

    private void initModel(LogEntry template)
    {
        // model.setTitle(template.getTitle());
        model.setText(template.getDescription());
        
        Collection<Logbook> logbooks = template.getLogbooks();
        logbooks.forEach(logbook-> 
        {
            model.addSelectedLogbook(logbook.getName());
        });  
        
        Collection<Tag> tags = template.getTags();
        tags.forEach(tag-> 
        {
            model.addSelectedTag(tag.getName());
        });
    }
    
    public void submitEntry()
    {
        LogFactory logFactory = logService.getLogFactories().get("org.phoebus.sns.logbook");
        if (logFactory != null)
        {
            System.out.println("Factory successfully retrieved: " + logFactory.getId());
        }
        
        LogEntry logEntry = model.getEntry();
        
        //LogClient client = logFactory.getLogClient(model.getUsername(), model.getPassword());
        
        //client.set(logEntry);
    }
}
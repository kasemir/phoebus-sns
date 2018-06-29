/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for making an entry into a log book.
 * @author Evan Smith
 *
 */
public class LogbookEntryView extends VBox
{
    /** Purveyor of state in the log entry view. */
    private final LogEntryModel          model;
    
    /** View handles user credential entry for access to log books. */
    private final LogCredentialEntryView credentialEntry;
    
    /** View handles displaying of date and log entry level selection. */
    private final LogDateLevelView       dateAndLevel;
    
    /** View handles the input for creation of the entry. */
    private final LogEntryFieldsView     logEntryFields;
    
    public LogbookEntryView()
    {
        model = new LogEntryModel();
        
        // user name and password label and fields.
        credentialEntry = new LogCredentialEntryView(model);
        
        // date and level labels, fields, and selectors.
        dateAndLevel = new LogDateLevelView();
        
        // title and text labels and fields.
        logEntryFields = new LogEntryFieldsView(model);
        
        // Let the Text Area grow to the bottom.
        VBox.setVgrow(logEntryFields, Priority.ALWAYS);

        getChildren().addAll(credentialEntry, dateAndLevel, logEntryFields);
    }
}
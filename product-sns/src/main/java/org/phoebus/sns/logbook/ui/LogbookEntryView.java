/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * View for making an entry into a log book.
 * @author Evan Smith
 *
 */
public class LogbookEntryView extends VBox
{
    /** Width of labels on views leftmost column. */
    public static final int labelWidth = 80;
    
    /** Purveyor of log entry application state. */
    private final LogEntryModel           model;
    
    /** View handles user credential entry for access to log. */
    private final LogCredentialEntryView  credentialEntry;
    
    /** View handles displaying of date and log entry level selection. */
    private final LogDateLevelView        dateAndLevel;
    
    /** View handles the input for creation of the entry. */
    private final LogEntryFieldsView      logEntryFields;
    
    /** View handles addition of log entry attachments. */
    private final LogEntryAttachmentsView attachmentsView;
    
    private final HBox buttonBox;
    private final Button cancel, submit;
    
    public LogbookEntryView(final Node callingNode)
    {
        Scene callingScene = callingNode.getParent().getScene();
        Window owner = callingScene.getWindow();
        
        Stage stage = new Stage();     
        stage.initOwner(owner); // The stage should die if the main window dies.
        
        model = new LogEntryModel(callingNode);
       
        // user name and password label and fields.
        credentialEntry = new LogCredentialEntryView(model);
        
        // date and level labels, fields, and selectors.
        dateAndLevel = new LogDateLevelView(model);
        
        // title and text labels and fields.
        logEntryFields = new LogEntryFieldsView(model);
        
        // Images, Files, Properties
        attachmentsView = new LogEntryAttachmentsView(model);
        
        // Cancel and Submit buttons.
        buttonBox = new HBox();
        cancel = new Button("Cancel");
        submit = new Button("Submit");
        cancel.setPrefWidth(100);
        submit.setPrefWidth(100);
        buttonBox.getChildren().addAll(cancel, submit);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setSpacing(10);
        
        setButtonActions();
        
        // Let the Text Area grow to the bottom.
        VBox.setVgrow(logEntryFields, Priority.ALWAYS);

        VBox.setMargin(credentialEntry, new Insets(10, 10,  0, 10));
        VBox.setMargin(dateAndLevel,    new Insets( 0, 10,  0, 10));
        VBox.setMargin(logEntryFields,  new Insets( 0, 10,  0, 10));
        VBox.setMargin(logEntryFields,  new Insets( 0, 10,  0, 10));
        VBox.setMargin(buttonBox,       new Insets( 0, 10, 10, 10));
        
        setSpacing(10);
        getChildren().addAll(credentialEntry, dateAndLevel, logEntryFields, attachmentsView, buttonBox);
        
        Scene scene = new Scene(this, 700, 1000);
        stage.setTitle("Logbook Entry");
        stage.setScene(scene);
        
        stage.show();
    }

    private void setButtonActions()
    {
        cancel.setOnAction(event ->
        {
            close();
        });
        
        submit.setOnAction(event ->
        {
            model.submitEntry();
            close();
        });
    }
    
    private void close()
    {
        Scene scene = this.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }
}
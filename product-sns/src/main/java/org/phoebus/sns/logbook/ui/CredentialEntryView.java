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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * View to facilitate user credential entry.
 * @author Evan Smith
 */
public class CredentialEntryView extends HBox
{
    private final LogEntryModel model;
    private final Label         userFieldLabel, passwordFieldLabel;
    private final TextField     userField;
    private final PasswordField passwordField;
    
    public CredentialEntryView(LogEntryModel model)
    {
        this.model = model;
        userFieldLabel     = new Label("User Name:");       
        passwordFieldLabel = new Label("Password:");

        userField     = new TextField();
        passwordField = new PasswordField();
        
        formatView();
    }
    
    private void formatView()
    {
        setFieldActions();
        
        userFieldLabel.setPrefWidth(LogbookEntryDialog.labelWidth);

        // The preferred width is set to zero so that the labels don't minimize themselves to let the fields have their preferred widths.
        userField.setPrefWidth(0);
        passwordField.setPrefWidth(0);
        
        HBox.setMargin(passwordFieldLabel, new Insets(0, 0, 0, 5));
        HBox.setHgrow(userField, Priority.ALWAYS);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        
        setSpacing(5);
        setAlignment(Pos.CENTER);
        getChildren().addAll(userFieldLabel, userField, passwordFieldLabel, passwordField);
    }

    private void setFieldActions()
    {
        userField.setOnKeyReleased(event ->
        {
            model.setUser(userField.getText());
        });
        
        passwordField.setOnKeyReleased(event ->
        {
            model.setPassword(passwordField.getText());
        });
    }
}

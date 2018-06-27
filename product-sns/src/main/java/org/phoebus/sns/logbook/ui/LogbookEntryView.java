package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogbookEntryView extends VBox
{
    
    private final Label         userFieldLabel, passwordFieldLabel;
    private final TextField     userField;
    private final PasswordField passwordField;
    
    public LogbookEntryView()
    {
       userFieldLabel     = new Label("User Name:");       
       passwordFieldLabel = new Label("Password:");

       userField     = new TextField();
       passwordField = new PasswordField();

       HBox.setHgrow(userField, Priority.ALWAYS);
       HBox.setHgrow(passwordField, Priority.ALWAYS);
       
       HBox userCredentialEntry = new HBox();
       
       userCredentialEntry.setSpacing(15);
       userCredentialEntry.setAlignment(Pos.CENTER);
       userCredentialEntry.getChildren().addAll(userFieldLabel, userField, passwordFieldLabel, passwordField);
       
       VBox.setMargin(userCredentialEntry, new Insets(20, 5, 20, 5));
       
       getChildren().add(userCredentialEntry);
    }
}

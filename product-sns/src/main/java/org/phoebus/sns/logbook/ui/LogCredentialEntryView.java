package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogCredentialEntryView extends HBox
{
    private final LogEntryModel model;
    private final Label         userFieldLabel, passwordFieldLabel;
    private final TextField     userField;
    private final PasswordField passwordField;
    
    public LogCredentialEntryView(LogEntryModel model)
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
     // The preferred width is set to zero so that the labels don't minimize themselves to let the fields have their preferred widths.
        userField.setPrefWidth(0);
        passwordField.setPrefWidth(0);
        HBox.setHgrow(userField, Priority.ALWAYS);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        
        setSpacing(15);
        setAlignment(Pos.CENTER);
        getChildren().addAll(userFieldLabel, userField, passwordFieldLabel, passwordField);
        
        VBox.setMargin(this, new Insets(20, 5, 20, 5));
    }
}

package org.phoebus.sns.logbook.ui;

import java.time.Instant;

import org.phoebus.util.time.TimestampFormats;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
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
    
    private final Label     dateLabel, levelLabel;
    private final TextField dateField;
    private final ComboBox<String>  levelSelector;
    private final ObservableList<String> levels = FXCollections.observableArrayList(
                                                        "Urgent",
                                                        "High",
                                                        "Normal");
    
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

       dateLabel = new Label("Date:");
       dateField = new TextField(TimestampFormats.DATE_FORMAT.format(Instant.now()));
       
       dateField.setPrefWidth(100);

       levelLabel = new Label("Level:");
       
       levelSelector = new ComboBox<String>(levels);
       
       levelLabel.setAlignment(Pos.CENTER_RIGHT);
       
       // Put log level label and selector in HBox so that they can be right justified.
       HBox levelBox  = new HBox();
       levelBox.getChildren().addAll(levelLabel, levelSelector);
       levelBox.setSpacing(5);
       levelBox.setAlignment(Pos.CENTER_RIGHT);
       HBox.setHgrow(levelBox, Priority.ALWAYS);
       
       // date and level labels, fields, and selectors.
       HBox dateLevel = new HBox();
       
       dateLevel.setAlignment(Pos.CENTER);
       dateLevel.setSpacing(5);
       dateLevel.getChildren().addAll(dateLabel, dateField, levelBox);
       VBox.setMargin(dateLevel, new Insets(40, 5, 10, 5));
       getChildren().addAll(userCredentialEntry, dateLevel);
    }
}

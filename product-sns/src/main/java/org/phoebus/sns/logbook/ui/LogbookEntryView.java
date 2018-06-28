package org.phoebus.sns.logbook.ui;

import java.time.Instant;
import java.util.List;

import org.phoebus.util.time.TimestampFormats;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogbookEntryView extends VBox
{
    
    private final Label         userFieldLabel, passwordFieldLabel;
    private final TextField     userField;
    private final PasswordField passwordField;
    
    private final Label                  dateLabel, levelLabel;
    private final TextField              dateField;
    private final ComboBox<String>       levelSelector;
    private final ObservableList<String> levels = FXCollections.observableArrayList(
                                                        "Urgent",
                                                        "High",
                                                        "Normal");
    
    private final Label     titleLabel, textLabel;
    private final TextField titleField;
    private final TextArea  textArea;
    
    private final Label                   logbooksLabel, tagsLabel;
    private final TextField               logbooksField, tagsField;
    private final MenuButton              logbooksSelector, tagsSelector;
    private final ObservableList<String>  logbooks, tags;
    private final Button                  addLogbooksButton, addTagsButton;
    
    public LogbookEntryView()
    {
       userFieldLabel     = new Label("User Name:");       
       passwordFieldLabel = new Label("Password:");

       userField     = new TextField();
       passwordField = new PasswordField();

       // user name and password label and fields.
       HBox userCredentialEntry = formatUserCredentialEntry();

       dateLabel = new Label("Date:");
       dateField = new TextField(TimestampFormats.DATE_FORMAT.format(Instant.now()));
       dateField.setPrefWidth(100);

       levelLabel = new Label("Level:");
       levelSelector = new ComboBox<String>(levels);
       
       // date and level labels, fields, and selectors.
       HBox dateAndLevel = formatDateAndLevel();
       
       titleLabel = new Label("Title:");
       titleField = new TextField();
       textLabel  = new Label("Text:");
       textArea   = new TextArea();

       // title and text labels and fields.
       VBox logEntryFields = formatLogEntryFields();
       
       logbooksLabel     = new Label("Logbooks:");
       logbooksField     = new TextField();
       logbooksSelector  = new MenuButton();
       logbooks          = FXCollections.observableArrayList();
       addLogbooksButton = new Button();
       
       tagsLabel     = new Label("Tags:       ");
       tagsField     = new TextField();
       tagsSelector  = new MenuButton();
       tags          = FXCollections.observableArrayList();
       addTagsButton = new Button();

       VBox logbooksAndTags = formatLogbooksAndTagsView();
       
       getChildren().addAll(userCredentialEntry, dateAndLevel, logEntryFields, logbooksAndTags);
       
       initializeLogbooksAndTags();
    }
    
   
      private HBox formatUserCredentialEntry()
    {
        HBox userCredentialEntry = new HBox();
        
        // The preferred width is set to zero so that the labels don't minimize themselves to let the fields have their preferred widths.
        userField.setPrefWidth(0);
        passwordField.setPrefWidth(0);
        HBox.setHgrow(userField, Priority.ALWAYS);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        
        userCredentialEntry.setSpacing(15);
        userCredentialEntry.setAlignment(Pos.CENTER);
        userCredentialEntry.getChildren().addAll(userFieldLabel, userField, passwordFieldLabel, passwordField);
        
        VBox.setMargin(userCredentialEntry, new Insets(20, 5, 20, 5));
        
        return userCredentialEntry;
    }
    
    private HBox formatDateAndLevel()
    {
     // date and level labels, fields, and selectors.
        HBox dateAndLevel = new HBox();
        
        // TODO : Verify this.
        dateField.setEditable(false);
        levelLabel.setAlignment(Pos.CENTER_RIGHT);
        
        // Put log level label and selector in HBox so that they can be right justified.
        HBox levelBox  = new HBox();
        levelBox.getChildren().addAll(levelLabel, levelSelector);
        levelBox.setSpacing(5);
        levelBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(levelBox, Priority.ALWAYS);

        dateAndLevel.setAlignment(Pos.CENTER);
        dateAndLevel.setSpacing(5);
        dateAndLevel.getChildren().addAll(dateLabel, dateField, levelBox);
        VBox.setMargin(dateAndLevel, new Insets(40, 5, 10, 5));
                
        return dateAndLevel;
    }
    
    private VBox formatLogEntryFields()
    {
        VBox logEntryFields = new VBox();
        
        // title label and title field.
        HBox titleBox = new HBox();
        
        titleField.setPrefWidth(0);
        HBox.setHgrow(titleField, Priority.ALWAYS);

        titleBox.setSpacing(5);
        VBox.setMargin(titleBox, new Insets(5, 5, 5, 5));
        titleBox.getChildren().addAll(titleLabel, titleField);
        
        // text label and text area.
        HBox textBox = new HBox();
        
        textArea.setPrefWidth(0);
        HBox.setHgrow(textArea, Priority.ALWAYS);
        
        textBox.setSpacing(5);
        VBox.setMargin(textBox, new Insets(5, 5, 5, 5));

        textBox.getChildren().addAll(textLabel, textArea);

        logEntryFields.getChildren().addAll(titleBox, textBox);

        return logEntryFields;
    }
    
    /** Format the log books and tags selector view. 
     *  @return VBox containing the formatted items.
     */
    private VBox formatLogbooksAndTagsView()
    {

        logbooks.addListener(
                (ListChangeListener.Change<? extends String> listener) -> 
        {
            String selectedLogbooks = "";
            for (final String logbookName : logbooks)
            {
                selectedLogbooks += (selectedLogbooks.isEmpty() ? "" : ", ") +  logbookName;
            }
            logbooksField.setText(selectedLogbooks);
        });
        
        tags.addListener(
                (ListChangeListener.Change<? extends String> listener) -> 
        {
            String selectedTags = "";
            for (final String tagName : tags)
            {
                selectedTags += (selectedTags.isEmpty() ? "" : ", ") +  tagName;
            }
            tagsField.setText(selectedTags);
        });
        
        HBox logbooksBox = new HBox();
        HBox.setHgrow(logbooksField, Priority.ALWAYS);
        logbooksBox.getChildren().addAll(logbooksLabel, logbooksField, logbooksSelector, addLogbooksButton);
        logbooksBox.setSpacing(5);
        VBox.setMargin(logbooksBox, new Insets(5));

        HBox tagsBox = new HBox();
        HBox.setHgrow(tagsField, Priority.ALWAYS);
        tagsBox.getChildren().addAll(tagsLabel, tagsField, tagsSelector, addTagsButton);
        tagsBox.setSpacing(5);
        VBox.setMargin(tagsBox, new Insets(5));
        
        VBox logbooksAndTags = new VBox();
        logbooksAndTags.getChildren().addAll(logbooksBox, tagsBox);
        logbooksAndTags.setSpacing(5);
        VBox.setMargin(logbooksAndTags, new Insets(5));
        return logbooksAndTags;
    }
    
    private void addLogbookToSelector(final String logbook)
    {
        CheckMenuItem newLogbook = new CheckMenuItem(logbook);
        newLogbook.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                CheckMenuItem source = (CheckMenuItem) e.getSource();
                String text = source.getText();
                if (logbooks.contains(text))
                    logbooks.remove(text);
                else
                    logbooks.add(text);
            }
        });
        logbooksSelector.getItems().add(newLogbook);
    }
    
    private void addTagToSelector(final String tag)
    {
        CheckMenuItem newTag = new CheckMenuItem(tag);
        newTag.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                CheckMenuItem source = (CheckMenuItem) e.getSource();
                String text = source.getText();
                if (tags.contains(text))
                    tags.remove(text);
                else
                    tags.add(text);
            }
        });
        tagsSelector.getItems().add(newTag);
    }
    
    // TODO : This should use the model to retrieve the log books and tags.
    private void initializeLogbooksAndTags()
    {
        List<String> tags = List.of("Tag 1", "Tag 2", "Tag 3");
        List<String> logbooks = List.of("Logbook 1", "Logbook 2", "Logbook 3");
        
        for (final String tag : tags)
        {
            addTagToSelector(tag);
        }
        for (final String logbook : logbooks)
        {
            addLogbookToSelector(logbook);
        }
    }
}
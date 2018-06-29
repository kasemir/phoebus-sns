package org.phoebus.sns.logbook.ui;

import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogbooksTagsView extends VBox
{
    private final LogEntryModel model;
    private final Label         logbooksLabel, tagsLabel;
    private final TextField     logbooksField, tagsField;
    private final MenuButton    logbooksSelector, tagsSelector;
    private final Button        addLogbooksButton, addTagsButton;
    
    public LogbooksTagsView(LogEntryModel model)
    {
        this.model = model;
        logbooksLabel     = new Label("Logbooks:");
        logbooksField     = new TextField();
        logbooksSelector  = new MenuButton();
        addLogbooksButton = new Button();
        
        tagsLabel     = new Label("Tags:       ");
        tagsField     = new TextField();
        tagsSelector  = new MenuButton();
        addTagsButton = new Button();
        formatView();
        initializeSelectors();
    }
    
    private void formatView()
    {
    
        createLogbooksFieldEventHandler();
        createTagsFieldEventHandler();

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
        
        getChildren().addAll(logbooksBox, tagsBox);
        setSpacing(5);
        VBox.setMargin(this, new Insets(5));
    }

    private void createTagsFieldEventHandler()
    {
        // Listen to key released events. Listening to textProperty would not work as the 
        // log book selector also changes that. It would result in an infinite loop.
        logbooksField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event)
            {
                int caretPos = logbooksField.getCaretPosition();
                TextField source = (TextField) event.getSource();
                String[] tokens = source.getText().split(",(\\s)*");
                boolean tokenNotFound = false;
                for (String token : tokens)
                {
                    if (model.hasLogbook(token))
                    {
                        if (! model.hasSelectedLogbook(token))
                        {
                            for (MenuItem item : logbooksSelector.getItems())
                            {
                                 CheckMenuItem menuItem = (CheckMenuItem) item;
                                 if (menuItem.getText().equals(token))
                                 {
                                     if (! menuItem.isSelected())
                                     {
                                         menuItem.setSelected(true);
                                         menuItem.fire();
                                     }
                                 }
                            }
                        }
                    }
                    else
                    {
                        tokenNotFound |= true;
                        List<String> strings = Arrays.asList(tokens);
                        for (MenuItem item : logbooksSelector.getItems())
                        {
                             CheckMenuItem menuItem = (CheckMenuItem) item;
                             if (! strings.contains(menuItem.getText()))
                             {
                                 if (menuItem.isSelected())
                                 {
                                     menuItem.setSelected(false);
                                     menuItem.fire();
                                 }
                             }
                        }
                    }
                }
                
                if (tokenNotFound)
                {
                    logbooksField.setStyle("-fx-text-fill: red;"); // Setting the style sets the cursor to the beginning of the field.
                    tagsField.selectPositionCaret(caretPos);
                    logbooksField.deselect();  // Deselect the text.

                }
                else
                {
                    logbooksField.setStyle("-fx-text-fill: black;");
                    tagsField.selectPositionCaret(caretPos);
                    logbooksField.deselect();
                }
            }          
        });        
    }

    private void createLogbooksFieldEventHandler()
    {
        // Listen to key released events. Listening to textProperty would not work as the 
        // tag selector also changes that. It would result in an infinite loop.
        tagsField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event)
            {
                int caretPos = tagsField.getCaretPosition();
                TextField source = (TextField) event.getSource();
                String[] tokens = source.getText().split(",(\\s)*");
                boolean tokenNotFound = false;
                for (String token : tokens)
                {
                    if (model.hasTag(token))
                    {
                        if (! model.hasSelectedTag(token))
                        {
                            for (MenuItem item : tagsSelector.getItems())
                            {
                                 CheckMenuItem menuItem = (CheckMenuItem) item;
                                 if (menuItem.getText().equals(token))
                                 {
                                     if (! menuItem.isSelected())
                                     {
                                         menuItem.setSelected(true);
                                         menuItem.fire();
                                     }
                                 }
                            }
                        }
                    }
                    else
                    {
                        tokenNotFound |= true;
                        List<String> strings = Arrays.asList(tokens);
                        for (MenuItem item : tagsSelector.getItems())
                        {
                             CheckMenuItem menuItem = (CheckMenuItem) item;
                             if (! strings.contains(menuItem.getText()))
                             {
                                 //menuItem.setSelected(false);
                                 if (menuItem.isSelected())
                                 {
                                     menuItem.setSelected(false);
                                     menuItem.fire();
                                 }
                             }
                        }
                    }
                }
                
                if (tokenNotFound)
                {
                    tagsField.setStyle("-fx-text-fill: red;");
                    tagsField.selectPositionCaret(caretPos);
                    tagsField.deselect();
                }
                else
                {
                    tagsField.setStyle("-fx-text-fill: black;");
                    tagsField.selectPositionCaret(caretPos);
                    tagsField.deselect();
                }
            }          
        });
    }
    
    public void addLogbookToSelector(final String logbook)
    {
        CheckMenuItem newLogbook = new CheckMenuItem(logbook);
        newLogbook.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                CheckMenuItem source = (CheckMenuItem) e.getSource();
                String text = source.getText();
                if (model.hasSelectedLogbook(text))
                    model.removeSelectedLogbook(text);
                else
                {
                    model.addSelectedLogbook(text);
                }
            }
        });
        logbooksSelector.getItems().add(newLogbook);        
    }
    
    public void addTagToSelector(final String tag)
    {
        CheckMenuItem newTag = new CheckMenuItem(tag);
        newTag.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                CheckMenuItem source = (CheckMenuItem) e.getSource();
                String text = source.getText();
                if (model.hasSelectedTag(text))
                    model.removeSelectedTag(text);
                else
                {
                    model.addSelectedTag(text);
                }
            }
        });
        tagsSelector.getItems().add(newTag);
    }
    
    private void initializeSelectors()
    {
        for (final String logbook : model.getLogbooks())
        {
            addLogbookToSelector(logbook);
        }
        for (final String tag : model.getTags())
        {
            addTagToSelector(tag);
        }
    }
}

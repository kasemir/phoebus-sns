package org.phoebus.sns.logbook.ui;

import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogbooksTagsView extends VBox
{
    private final LogEntryModel model;
    private final Label         logbooksLabel, tagsLabel;
    private final TextField     logbooksField, tagsField;
    private final ContextMenu   logbooksContextMenu, tagsContextMenu;
    private final ToggleButton  logbooksSelector, tagsSelector;
    private final Button        addLogbooksButton, addTagsButton;
    
    public LogbooksTagsView(LogEntryModel model)
    {
        this.model = model;
        
        logbooksLabel       = new Label("Logbooks:");
        logbooksField       = new TextField();
        logbooksSelector    = new ToggleButton("V");
        addLogbooksButton   = new Button();
        logbooksContextMenu = new ContextMenu();
        
        tagsLabel       = new Label("Tags:       ");
        tagsField       = new TextField();
        tagsSelector    = new ToggleButton("V");
        addTagsButton   = new Button();
        tagsContextMenu = new ContextMenu();
        
        formatView();
        initializeSelectors();
    }
    
    private void formatView()
    {
        
        tagsSelector.setOnAction(actionEvent -> 
        {
            if (tagsSelector.isSelected())
                tagsContextMenu.show(tagsField, Side.BOTTOM, 0, 0);
            else
                tagsContextMenu.hide();
        });
        logbooksSelector.setOnAction(actionEvent -> 
        {
            if (logbooksSelector.isSelected())
                logbooksContextMenu.show(logbooksField, Side.BOTTOM, 0, 0);
            else
                logbooksContextMenu.hide();
        });
        
        logbooksContextMenu.focusedProperty().addListener((changeListener, oldVal, newVal) -> 
        {
            if (! newVal && ! logbooksSelector.focusedProperty().get())
                logbooksSelector.setSelected(newVal);
        });
        tagsContextMenu.focusedProperty().addListener((changeListener, oldVal, newVal) -> 
        {
            if (! newVal && ! tagsSelector.focusedProperty().get())
                tagsSelector.setSelected(newVal);
        });
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

    private void createLogbooksFieldEventHandler()
    {
        // Listen to key released events. Listening to textProperty would not work as the 
        // log book selector also changes that. It would result in an infinite loop.
        logbooksField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event)
            {
                int caretPos = logbooksField.getCaretPosition();
                TextField source = (TextField) event.getSource();
                String[] tokens = source.getText().split("(\\s)*,(\\s)*");
                boolean tokenNotFound = false;
                for (String token : tokens)
                {
                    token = token.trim();
                    System.out.print("\"" + token + "\", ");
                    // A known log book has been typed.
                    if (model.hasLogbook(token))
                    {
                        if (! model.hasSelectedLogbook(token))
                        {
                            for (MenuItem item : logbooksContextMenu.getItems())
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
                    // An unknown log book has been typed.
                    else
                    {
                        tokenNotFound |= true; // Signal to turn text red.
                        List<String> strings = Arrays.asList(tokens);
                        for (MenuItem item : logbooksContextMenu.getItems())
                        {
                            // Any logbook that is selected but no longer typed should be deselected.
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
                    logbooksField.setStyle("-fx-text-fill: red;"); // Setting the style sets the caret to the beginning of the field.
                    logbooksField.selectPositionCaret(caretPos);   // Reset the caret position.
                    logbooksField.deselect();                      // Deselect the text.

                }
                else
                {
                    logbooksField.setStyle("-fx-text-fill: black;");
                    logbooksField.selectPositionCaret(caretPos);
                    logbooksField.deselect();
                }
                System.out.println();
            }
        });        
    }

    private void createTagsFieldEventHandler()
    {
        // Listen to key released events. Listening to textProperty would not work as the 
        // tag selector also changes that. It would result in an infinite loop.
        tagsField.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event)
            {
                int caretPos = tagsField.getCaretPosition();
                TextField source = (TextField) event.getSource();
                String[] tokens = source.getText().split("(\\s)*,(\\s)*");
                boolean tokenNotFound = false;
                for (String token : tokens)
                {
                    token = token.trim();
                    // A known tag has been typed.
                    if (model.hasTag(token))
                    {
                        if (! model.hasSelectedTag(token))
                        {
                            for (MenuItem item : tagsContextMenu.getItems())
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
                    // An unknown tag has been typed.
                    else
                    {
                        tokenNotFound |= true; // Signal to turn text red.
                        
                        List<String> strings = Arrays.asList(tokens);
                        for (MenuItem item : tagsContextMenu.getItems())
                        {
                            // Any tag that is selected but no longer typed should be deselected.
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
                {
                    model.removeSelectedLogbook(text);
                    logbooksField.setText(handleInput(logbooksField.getText(), model.getSelectedLogbooks(), model.getLogbooks()));
                }
                else
                {
                    model.addSelectedLogbook(text);
                    logbooksField.setText(handleInput(logbooksField.getText(), model.getSelectedLogbooks(), model.getLogbooks()));
                }
                if (logbooksSelector.isSelected())
                    logbooksSelector.setSelected(false);
            }
        });
        logbooksContextMenu.getItems().add(newLogbook);        
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
                {
                    model.removeSelectedTag(text);
                    tagsField.setText(handleInput(tagsField.getText(), model.getSelectedTags(), model.getTags()));
                }
                else
                {
                    model.addSelectedTag(text);
                    tagsField.setText(handleInput(tagsField.getText(), model.getSelectedTags(), model.getTags()));
                }
                if (tagsSelector.isSelected())
                {
                    tagsSelector.setSelected(false);
                }
            }
        });
        tagsContextMenu.getItems().add(newTag);
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
    
    private String handleInput(String prev,List<String> selected, List<String> known)
    {
        String text = "";
        for (String item : known)
            prev = prev.replaceAll(item + "(,)*", "");
        
        for (String item : selected)
            text += (text.isEmpty() ? "" : ", ") + item;
        
        prev = prev.trim();
        //System.out.println("text: '" + text + "', " + "prev: '" + prev + "'");
        if (! prev.isEmpty())
        {
            if (! text.isEmpty())
                text += ", " + prev;
            else 
                text += prev;   
        }
        
        return text;
    }
}

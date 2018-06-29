/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

/**
 * View that handles user input in regards to passed lists of items.
 * <p> The view will check if the items the user has typed are contained in the 
 * lists accessible from the passed suppliers. Should they not be correct, the
 * input will be flagged. 
 * <p> Any correct items in the input string will be moved in front
 * of the incorrect items.
 * @author Evan Smith
 *
 */
public class LabelFieldSelectorView extends HBox
{

    private final Label         label;
    private final TextField     field;
    private final ContextMenu   dropDown;
    private final ToggleButton  selector;
    private final Button        addView;

    private final Supplier<List<String>>    selected, known;
    private final Predicate<String>         hasSelected, has;
    private final Function<String, Boolean> addSelected, removeSelected;
    
    public LabelFieldSelectorView(final String labelText, 
                                   Supplier<List<String>> selected, 
                                   Supplier<List<String>> known, 
                                   Predicate<String> hasSelected, 
                                   Predicate<String> has,
                                   Function<String, Boolean> addSelected,
                                   Function<String, Boolean> removeSelected)
    {
        this.selected       = selected;
        this.known          = known;
        this.hasSelected    = hasSelected;
        this.has            = has;
        this.addSelected    = addSelected;
        this.removeSelected = removeSelected;
        
        label    = new Label(labelText);
        field    = new TextField();
        selector = new ToggleButton("v"); // TODO: Get a down arrow icon for this.
        addView  = new Button();          // TODO: Implement add Log books/Tags view. Get a add icon for each.
        dropDown = new ContextMenu();
        
        formatView();
    }
    
     /** Format the view  */
    private void formatView()
    {
        label.setPrefWidth(85);
        selector.setOnAction(actionEvent -> 
        {
            if (selector.isSelected())
                dropDown.show(field, Side.BOTTOM, 0, 0);
            else
                dropDown.hide();
        });
        
        dropDown.focusedProperty().addListener((changeListener, oldVal, newVal) -> 
        {
            if (! newVal && ! selector.focusedProperty().get())
                selector.setSelected(newVal);
        });
        
        createFieldEventHandler();
        
        HBox.setHgrow(field, Priority.ALWAYS);
        getChildren().addAll(label, field, selector, addView);

        initializeSelector();
        
        setSpacing(5);
        VBox.setMargin(this, new Insets(5));
    }
    
    /** Initialize the drop down context menu. */
    private void initializeSelector()
    {
        for (final String item : known.get())
        {
            addToDropDown(item);
        }
    }
    
    /**
     * Create an event handler for parsing and then reacting to user input.
     */
    private void createFieldEventHandler()
    {
        // Listen to key released events. Listening to textProperty would not work as the 
        // item selector also changes that. It would result in an infinite loop.
        field.setOnKeyReleased(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event)
            {
                int caretPos = field.getCaretPosition();

                TextField source = (TextField) event.getSource();
                String[] tokens = source.getText().split("(\\s)*,(\\s)*");
                boolean tokenNotFound = false;
                for (String token : tokens)
                {
                    token = token.trim();
                    // A known item has been typed.
                    if (has.test(token))
                    {
                        if (! hasSelected.test(token))
                        {
                            // Check all the items in the drop down that are entered.
                            for (MenuItem item : dropDown.getItems())
                            {
                                 CheckMenuItem menuItem = (CheckMenuItem) item;
                                 // Only check the typed items if they aren't already.
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
                    // An unknown item has been typed.
                    else
                    {
                        tokenNotFound |= true; // Signal to turn text red.
                        
                        List<String> strings = Arrays.asList(tokens);
                        for (MenuItem item : dropDown.getItems())
                        {
                            // Any item that is selected but no longer typed should be deselected.
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
                
                // Indicate erroneous entry with red text.
                if (tokenNotFound)
                {
                    field.setStyle("-fx-text-fill: red;");
                    field.selectPositionCaret(caretPos);
                    field.deselect();
                }
                else
                {
                    field.setStyle("-fx-text-fill: black;");
                    field.selectPositionCaret(caretPos);
                    field.deselect();
                }
            }          
        });
    }
    
    /**
     * Add a new CheckMenuItem to the drop down ContextMenu.
     * @param item - Item to be added.
     */
    private void addToDropDown(String item)
    {
        CheckMenuItem newLogbook = new CheckMenuItem(item);
        newLogbook.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                CheckMenuItem source = (CheckMenuItem) e.getSource();
                String text = source.getText();
                if (hasSelected.test(text))
                {
                    removeSelected.apply(text);
                    field.setText(handleInput(field.getText(), selected.get(), known.get()));
                }
                else
                {
                    addSelected.apply(text);
                    field.setText(handleInput(field.getText(), selected.get(), known.get()));
                }
                if (selector.isSelected())
                    selector.setSelected(false);
            }
        });
        dropDown.getItems().add(newLogbook);        
    }
    
    /**
     * Parse the user's input. 
     * <ol>
     * <li> Isolate incorrect text.
     * <li> Build correct string of items.
     * <li> Flag incorrect input and put it at the end of the correct string.
     * </ol>
     * @param incorrect - String containing possibly incorrect text.
     * @param selectedItems - The items currently selected in the drop down.
     * @param knownItems - All known items.
     * @return String containing new text.
     */
    private String handleInput(String incorrect,List<String> selectedItems, List<String> knownItems)
    {
        String text = "";
        // Isolate the incorrect text by removing the correct item entries.
        for (String item : knownItems)
            incorrect = incorrect.replaceAll(item + "(,)*", "");
        
        // Build the correct text string.
        for (String item : selectedItems)
            text += (text.isEmpty() ? "" : ", ") + item;
        
        // If incorrect entry is all whitespace, then delete whitespace.
        if (incorrect.matches("(\\s)+"))
            incorrect = incorrect.trim();
        // Else, only delete the leading whitespace. Maintain trailing white space as to not mess with field caret position.
        else
            incorrect = incorrect.replaceAll("\\A\\s+", "");

        
        if (! incorrect.isEmpty())
        {
            // If the incorrect text is not empty and text is not empty, append the incorrect text onto the correct text.
            if (! text.isEmpty())
                text += ", " + incorrect;
            // Otherwise, incorrect is all there is, so add it to text.
            else 
                text += incorrect;   
        }
        
        return text;
    }
}

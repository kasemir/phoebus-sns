/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.List;
import java.util.concurrent.Callable;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View that handles user input in regards to passed lists of selectable items.
 * @author Evan Smith
 */
public class LabelFieldSelectorView extends HBox
{

    private final Label         label;
    private final TextField     field;
    private final ContextMenu   dropDown;
    private final ToggleButton  selector; // Opens context menu (dropDown).
    private final Button        addItem;  // Opens a yet to be implemented view.

    // Suppliers of selected and known item lists. Selected items is always either an empty set or subset of the known items.
    private final Supplier<List<String>> selected;
    private final Supplier<List<String>> known;             
    
    private final Predicate<String>         hasSelected;                 // Predicate to test if a specific item is selected.
    private final Function<String, Boolean> addSelected, removeSelected; // Functions to add or remove known items to/from the selected items list.
        
    public LabelFieldSelectorView(final String labelText, 
                                   Supplier<List<String>> selected, 
                                   Supplier<List<String>> known, 
                                   Predicate<String> hasSelected, 
                                   Function<String, Boolean> addSelected,
                                   Function<String, Boolean> removeSelected)
    {
        this.selected       = selected;
        this.known          = known;
        this.hasSelected    = hasSelected;
        this.addSelected    = addSelected;
        this.removeSelected = removeSelected;
        
        label    = new Label(labelText);
        field    = new TextField();
        selector = new ToggleButton("v"); // TODO: Get a down arrow icon for this.
        addItem  = new Button();          // TODO: Implement add Log books/Tags view. Get a add icon for each.
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
        
        field.setEditable(false);
        
        HBox.setHgrow(field, Priority.ALWAYS);
        
        addItem.setOnAction(event ->
        {
            new ListSelectionView(getScene().getWindow(), known, selected, addSelected, removeSelected, new Callable<Void> ()
            {
                @Override
                public Void call() throws Exception
                {
                    setFieldText();
                    return null;
                }
            }); 
        });
        
        getChildren().addAll(label, field, selector, addItem);
        
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
                    setFieldText();
                }
                else
                {
                    addSelected.apply(text);
                    setFieldText();
                }
                if (selector.isSelected())
                    selector.setSelected(false);
            }
        });
        dropDown.getItems().add(newLogbook);        
    }
    
    /** Sets the field's text based on the selected items list.*/
    private void setFieldText()
    {
        List<String> selectedItems = selected.get();
        
        String fieldText = "";
        for (String item : selectedItems)
        {
            fieldText += (fieldText.isEmpty() ? "" : ", ") + item;
        }
        
        field.setText(fieldText);
    }
}

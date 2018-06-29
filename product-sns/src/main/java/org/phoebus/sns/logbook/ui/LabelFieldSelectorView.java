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
        selector = new ToggleButton("V");
        addView  = new Button();
        dropDown = new ContextMenu();
        
        formatView();
    }
    
    private void formatView()
    {
        
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
    
    private void initializeSelector()
    {
        for (final String item : known.get())
        {
            addToDropDown(item);
        }
    }
    
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
                            for (MenuItem item : dropDown.getItems())
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
    
    private String handleInput(String prev,List<String> selectedItems, List<String> knownItems)
    {
        String text = "";
        for (String item : knownItems)
            prev = prev.replaceAll(item + "(,)*", "");
        
        for (String item : selectedItems)
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

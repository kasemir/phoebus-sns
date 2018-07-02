package org.phoebus.sns.logbook.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
/**
 * 
 * @author Evan Smith
 *
 */
public class ListSelectionView extends HBox
{
    private final Stage  stage;
    
    @SuppressWarnings("unused") // Used to initialize list views.
    private final Supplier<ObservableList<String>> available;
    @SuppressWarnings("unused") // Used to initialize list views.
    private final Supplier<ObservableList<String>> selected;
    
    private final Function<String, Boolean> addSelected, removeSelected;
    
    private final Font labelFont;
    private final VBox selectedBox, buttonsBox, availableBox;
    private final Label selectedLabel, itemsLabel;
    private final ListView<String> availableItems;
    private final ListView<String> selectedItems;
    
    private final HBox entryBox;
    private final Button add, remove, clear, apply, cancel;
    
    private final Callable<Void> toCall;

    public ListSelectionView(Window parent,
                              Supplier<ObservableList<String>>    available, 
                              Supplier<ObservableList<String>>    selected,
                              Function<String, Boolean> addSelected,
                              Function<String, Boolean> removeSelected,
                              Callable<Void>            toCall)
    {
        stage = new Stage();     
        stage.initOwner(parent);
        
        this.available      = available;
        this.selected       = selected;
        this.addSelected    = addSelected;
        this.removeSelected = removeSelected;
        this.toCall         = toCall;
        
        selectedBox  = new VBox();
        buttonsBox   = new VBox();
        availableBox = new VBox();
        entryBox     = new HBox();
        
        add    = new Button("Add");
        remove = new Button("Remove");
        clear  = new Button("Clear");
        apply  = new Button("Apply");
        cancel = new Button("Cancel");
        
        labelFont     = new Font(16);
        selectedLabel = new Label("Selected");
        itemsLabel    = new Label("Available");
        
        selectedItems  = new ListView<String>(selected.get());
        // We wan't to remove items from the available list as they're selected, and add them back as they are unselected. 
        // Due to this we need a copy as available.get() returns an immutable list.
        availableItems = new ListView<String>(
                FXCollections.observableArrayList(List.copyOf(available.get())));
        
        for (String item : selectedItems.getItems())
            availableItems.getItems().remove(item);
        
        formatView();
        
        Scene scene = new Scene(this, 600, 600);
        
        stage.setScene(scene);
        
        stage.show();
    }

    private void formatView()
    {
        selectedItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedItems.setStyle("-fx-control-inner-background-alt: white");
        availableItems.setStyle("-fx-control-inner-background-alt: white");

        add.setOnAction(event ->
        {
            // Can't modify list we're iterating over, so make a copy to iterate over.
            List<String> toAdd = new ArrayList<String>(availableItems.getSelectionModel().getSelectedItems());
            for (String item : toAdd)
            {
                addSelected.apply(item);
                availableItems.getItems().remove(item);
            }
            clearSelections();
        });
        
        remove.setOnAction(event ->
        {
            // Can't modify list we're iterating over, so make a copy to iterate over.
            List<String> toRemove = new ArrayList<String>(selectedItems.getSelectionModel().getSelectedItems());
            for (String item : toRemove)
            {
                removeSelected.apply(item);
                availableItems.getItems().add(item);
            }
            Collections.sort(availableItems.getItems());
            clearSelections();
        });
        
        clear.setOnAction(event ->
        {
            // Can't modify list we're iterating over, so make a copy to iterate over.
            List<String> toRemove = new ArrayList<String>(selectedItems.getItems());
            for (String item : toRemove)
            {
                removeSelected.apply(item);
                availableItems.getItems().add(item);
            }
            Collections.sort(availableItems.getItems());
            clearSelections();
        });
        
        apply.setOnAction(event ->
        {
            try
            {  toCall.call();  }
            catch (Exception ex)
            {  /* Ignore? */   }
            finally
            {  stage.close();  }
        });
        
        cancel.setOnAction(event -> 
        {
            stage.close();
        });
        
        setAlignment(Pos.CENTER);
        setSpacing(5);

        buttonsBox.setSpacing(5);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(add, remove, clear);
        
        HBox emptyBox = new HBox();
        
        Button hiddenButton = new Button();
        hiddenButton.setVisible(false);
        emptyBox.getChildren().add(hiddenButton);
        
        itemsLabel.setFont(labelFont);
        VBox.setVgrow(availableItems, Priority.ALWAYS);
        availableBox.setSpacing(5);
        availableBox.getChildren().addAll(itemsLabel, availableItems, emptyBox);
        
        selectedLabel.setFont(labelFont);
        VBox.setVgrow(selectedItems, Priority.ALWAYS);
        selectedBox.setSpacing(5);
        
        entryBox.setAlignment(Pos.CENTER_RIGHT);
        entryBox.setSpacing(5);
        entryBox.getChildren().addAll(cancel, apply);
        
        selectedBox.getChildren().addAll(selectedLabel, selectedItems, entryBox);

        HBox.setMargin(availableBox, new Insets(5, 0, 10, 0));
        HBox.setMargin(buttonsBox,   new Insets(5, 0, 10, 0));
        HBox.setMargin(selectedBox,  new Insets(5, 0, 10, 0));
        
        getChildren().addAll(availableBox, buttonsBox, selectedBox);
    }

    private void clearSelections()
    {
        selectedItems.getSelectionModel().clearSelection();
        availableItems.getSelectionModel().clearSelection();
    }
}
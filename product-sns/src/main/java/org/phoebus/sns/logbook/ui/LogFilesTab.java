package org.phoebus.sns.logbook.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class LogFilesTab extends Tab
{
    private class FileCell extends ListCell<File>
    {
        public FileCell()
        {
            super();
            this.setStyle("-fx-text-fill: blue;"
                         + "-fx-underline: true;");
            this.setOnMouseClicked(click -> 
            {
                if (click.getClickCount() == 2)
                {
                    try
                    {
                        Desktop.getDesktop().open(this.getItem());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        @Override
        public void updateItem(File file, boolean empty)
        {
            super.updateItem(file, empty);
            if (empty)
            {
                setGraphic(null);
                setText(null);
            }
            else
            {
                setText(file.getName());
            }   
        }
    }
    
    private final LogEntryModel  model;
    
    private final VBox           content;
    private final Label          label;
    private final ListView<File> listView;
    private final HBox           listBox, buttonBox;
    private final Button         attachContext, attachFile, removeSelected;
    
    private final FileChooser    fileChooser;  
    
    public LogFilesTab(final LogEntryModel model) 
    {
        super();
        this.model = model;
        
        content   = new VBox();
        label     = new Label("Attached Files");
        listView  = new ListView<File>(model.getFiles());
        listBox   = new HBox();
        buttonBox = new HBox();
        attachContext  = new Button("Attach Context");
        attachFile     = new Button("Attach File");
        removeSelected = new Button("Remove Selected");
        
        fileChooser = new FileChooser();
        
        formatTab();
    }

    private void formatTab()
    {
        setText("Files");
        setClosable(false);
        
        formatContent();
        setOnActions();

        setContent(content);
    }

    private void formatContent()
    {
        VBox.setMargin(label, new Insets(10, 0, 0, 10));
        
        formatListBox();
        formatButtonBox();
        
        content.setSpacing(10);
        content.getChildren().addAll(label, listBox, buttonBox);
    }

    
    private void formatListBox()
    {
        listView.setCellFactory(cell -> new FileCell());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setPrefHeight(50);
        listView.setStyle("-fx-control-inner-background-alt: #f4f4f4");
        listView.setStyle("-fx-control-inner-background: #f4f4f4");
        VBox.setMargin(listBox, new Insets(0, 10, 0, 10));
        VBox.setVgrow(listBox, Priority.ALWAYS);
        HBox.setHgrow(listView, Priority.ALWAYS);
        listBox.getChildren().add(listView);
    }

    private void formatButtonBox()
    {
        buttonBox.setSpacing(10);
        VBox.setMargin(buttonBox, new Insets(0, 10, 0, 10));
        attachContext.prefWidthProperty().bind(buttonBox.widthProperty().divide(3));
        attachFile.prefWidthProperty().bind(buttonBox.widthProperty().divide(3));
        removeSelected.prefWidthProperty().bind(buttonBox.widthProperty().divide(3));

        buttonBox.getChildren().addAll(attachContext, attachFile, removeSelected);
    }
    
    private void setOnActions()
    {
        // TODO : Implement attach context
        
        attachFile.setOnAction(event -> 
        {
            Window ownerWindow = this.getTabPane().getParent().getScene().getWindow();
            List<File> files = fileChooser.showOpenMultipleDialog(ownerWindow);
            if (null != files)
            {
                for (File file : files)
                {
                    model.addFile(file);
                }
            }
        });
        
        removeSelected.setOnAction(event ->
        {
            // We can't alter a list that we are iterating over, so we iterate over a copy of the selected files list.
            List<File> files = List.copyOf(listView.getSelectionModel().getSelectedItems());
            for (File file : files)
            {
                model.removeFile(file);
            }
        });
    }
}

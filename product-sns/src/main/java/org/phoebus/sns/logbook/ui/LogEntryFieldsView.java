package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogEntryFieldsView extends VBox
{
    private final Label     titleLabel, textLabel;
    private final TextField titleField;
    private final TextArea  textArea;
    
    public LogEntryFieldsView()
    {
        titleLabel = new Label("Title:");
        titleField = new TextField();
        textLabel  = new Label("Text:");
        textArea   = new TextArea();
        
        formatView();
    }

    private void formatView()
    {
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

        getChildren().addAll(titleBox, textBox);
    }
}

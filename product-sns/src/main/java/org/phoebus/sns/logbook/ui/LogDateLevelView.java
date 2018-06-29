/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.time.Instant;

import org.phoebus.util.time.TimestampFormats;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogDateLevelView extends HBox
{
    private final Label                  dateLabel, levelLabel;
    private final TextField              dateField;
    private final ComboBox<String>       levelSelector;
    private final ObservableList<String> levels = FXCollections.observableArrayList(
                                                        "Urgent",
                                                        "High",
                                                        "Normal");
    
    // private final String Date;
    // private final String Level;
    
    public LogDateLevelView()
    {
        dateLabel = new Label("Date:");
        dateField = new TextField(TimestampFormats.DATE_FORMAT.format(Instant.now()));
        dateField.setPrefWidth(100);

        levelLabel = new Label("Level:");
        levelSelector = new ComboBox<String>(levels);
        
        formatView();
    }

    private void formatView()
    {
        dateField.setEditable(false);
        dateLabel.setPrefWidth(85);
        levelLabel.setAlignment(Pos.CENTER_RIGHT);
        
        // Put log level label and selector in HBox so that they can be right justified.
        HBox levelBox  = new HBox();
        levelBox.getChildren().addAll(levelLabel, levelSelector);
        levelBox.setSpacing(5);
        levelBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(levelBox, Priority.ALWAYS);

        setAlignment(Pos.CENTER);
        setSpacing(5);
        getChildren().addAll(dateLabel, dateField, levelBox);
        VBox.setMargin(this, new Insets(40, 5, 10, 5));
    }
}

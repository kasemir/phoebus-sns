/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class LogbooksTagsView extends VBox
{
    @SuppressWarnings("unused")
    private final LogEntryModel model;
    
    public LogbooksTagsView(LogEntryModel model)
    {
        this.model = model;
        
        LabelFieldSelectorView logbooks = new LabelFieldSelectorView("Logbooks", 
                                                                      model::getLogbooks, 
                                                                      model::getSelectedLogbooks, 
                                                                      model::hasSelectedLogbook, 
                                                                      model::addSelectedLogbook, 
                                                                      model::removeSelectedLogbook);
        
        LabelFieldSelectorView tags = new LabelFieldSelectorView("Tags", 
                                                                  model::getTags,
                                                                  model::getSelectedTags, 
                                                                  model::hasSelectedTag, 
                                                                  model::addSelectedTag, 
                                                                  model::removeSelectedTag);
        
        setSpacing(10);
        
        logbooks.setAlignment(Pos.CENTER);
        tags.setAlignment(Pos.CENTER);
        getChildren().addAll(logbooks, tags);
    }
}
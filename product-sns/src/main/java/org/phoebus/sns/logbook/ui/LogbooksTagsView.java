/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import javafx.scene.layout.VBox;

public class LogbooksTagsView extends VBox
{
    @SuppressWarnings("unused")
    private final LogEntryModel model;
    
    public LogbooksTagsView(LogEntryModel model)
    {
        this.model = model;
        
        LabelFieldSelectorView logbooks = new LabelFieldSelectorView("Logbooks:", 
                                                                      model::getSelectedLogbooks, 
                                                                      model::getLogbooks, 
                                                                      model::hasSelectedLogbook, 
                                                                      model::hasLogbook, 
                                                                      model::addSelectedLogbook, 
                                                                      model::removeSelectedLogbook);
        
        LabelFieldSelectorView tags = new LabelFieldSelectorView("Tags:       ", 
                                                                  model::getSelectedTags, 
                                                                  model::getTags, 
                                                                  model::hasSelectedTag, 
                                                                  model::hasTag, 
                                                                  model::addSelectedTag, 
                                                                  model::removeSelectedTag);
        
        //VBox.setMargin(this, new Insets(5));

        getChildren().addAll(logbooks, tags);
    }
}
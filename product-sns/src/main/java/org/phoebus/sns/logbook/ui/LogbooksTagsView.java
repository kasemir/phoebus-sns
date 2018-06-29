package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
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
        
        VBox.setMargin(this, new Insets(5));

        getChildren().addAll(logbooks, tags);
    }
}
/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogEntryModel
{
    // SNSLogClient client; // Source of log and tag data.
    private final ObservableList<String>    logbooks, tags, selectedLogbooks, selectedTags;
    
    public LogEntryModel()
    {
        // TODO : Implement SNSLogClient and use it to retrieve data. Remove dummy data.
        tags     = FXCollections.observableArrayList(List.of("Tag 1", "Tag 2", "Tag 3"));
        logbooks = FXCollections.observableArrayList(List.of("Logbook 1", "Logbook 2", "Logbook 3"));
        
        selectedLogbooks = FXCollections.observableArrayList();
        selectedTags     = FXCollections.observableArrayList();
    }
    
    public ObservableList<String> getLogbooks()
    {
        return FXCollections.unmodifiableObservableList(logbooks);
    }
    
    public ObservableList<String> getSelectedLogbooks()
    {
        return FXCollections.unmodifiableObservableList(selectedLogbooks);
    }
    
    public boolean hasLogbook (final String logbook)
    {
        return logbooks.contains(logbook);
    }
    
    public boolean hasSelectedLogbook (final String logbook)
    {
        return selectedLogbooks.contains(logbook);
    }
    
    public boolean addSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.add(logbook);
        selectedLogbooks.sort(Comparator.naturalOrder());
        return result;
    }
    
    public boolean removeSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.remove(logbook);
        selectedLogbooks.sort(Comparator.naturalOrder());
        return result;    
    }
    
    public ObservableList<String> getTags()
    {
        return FXCollections.unmodifiableObservableList(tags);
    }
    
    public ObservableList<String> getSelectedTags()
    {
        return FXCollections.unmodifiableObservableList(selectedTags);
    }
    
    public boolean hasTag (final String tag) 
    {
        return tags.contains(tag);
    }
    
    public boolean hasSelectedTag (final String tag) 
    {
        return selectedTags.contains(tag);
    }
    
    public boolean addSelectedTag(final String tag)
    {
        boolean result = selectedTags.add(tag);
        selectedTags.sort(Comparator.naturalOrder());
        return result;    
    }
    
    public boolean removeSelectedTag(final String tag)
    {
        boolean result = selectedTags.remove(tag);
        selectedTags.sort(Comparator.naturalOrder());
        return result;        
    }
}

/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogEntryModel
{
    // SNSLogClient client; // Source of log and tag data.
    private final List<String>    logbooks, tags, selectedLogbooks, selectedTags;
    
    public LogEntryModel()
    {
        // TODO : Implement SNSLogClient and use it to retrieve data. Remove dummy data.
        tags     = List.of("Tag 1", "Tag 2", "Tag 3");
        logbooks = List.of("Logbook 1", "Logbook 2", "Logbook 3");
        
        selectedLogbooks = new ArrayList<String>();
        selectedTags     = new ArrayList<String>();
    }
    
    public List<String> getLogbooks()
    {
        return Collections.unmodifiableList(logbooks);
    }
    
    public List<String> getSelectedLogbooks()
    {
        return Collections.unmodifiableList(selectedLogbooks);
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
        return selectedLogbooks.add(logbook);
    }
    
    public boolean removeSelectedLogbook(final String logbook)
    {
        return selectedLogbooks.remove(logbook);
    }
    
    public List<String> getTags()
    {
        return Collections.unmodifiableList(tags);
    }
    
    public List<String> getSelectedTags()
    {
        return Collections.unmodifiableList(selectedTags);
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
        return selectedTags.add(tag);
    }
    
    public boolean removeSelectedTag(final String tag)
    {
        return selectedTags.remove(tag);
    }
}

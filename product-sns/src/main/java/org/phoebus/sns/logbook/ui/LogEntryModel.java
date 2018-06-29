package org.phoebus.sns.logbook.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogEntryModel
{
    // SNSLogClient client;
    private final List<String> logbooks, selectedLogbooks, tags, selectedTags;
    
    public LogEntryModel()
    {
        tags     = List.of("Tag 1", "Tag 2", "Tag 3");
        logbooks = List.of("Logbook 1", "Logbook 2", "Logbook 3");
        
        selectedLogbooks = new ArrayList<String>();
        selectedTags     = new ArrayList<String>();
    }
    
    public List<String> getLogbooks()
    {
        return Collections.unmodifiableList(logbooks);
    }
    
    public boolean hasLogbook (final String logbook)
    {
        return logbooks.contains(logbook);
    }
    
    public boolean hasSelectedLogbook (final String logbook)
    {
        return selectedLogbooks.contains(logbook);
    }
    
    public void addSelectedLogbook(final String logbook)
    {
        selectedLogbooks.add(logbook);
    }
    
    public boolean removeSelectedLogbook(final String logbook)
    {
        return selectedLogbooks.remove(logbook);
    }
    
    public List<String> getTags()
    {
        return Collections.unmodifiableList(tags);
    }
    
    public boolean hasTag (final String tag) 
    {
        return tags.contains(tag);
    }
    
    public boolean hasSelectedTag (final String tag) 
    {
        return selectedTags.contains(tag);
    }
    
    public void addSelectedTag(final String tag)
    {
        selectedTags.add(tag);
    }
    
    public boolean removeSelectedTag(final String tag)
    {
        return selectedTags.remove(tag);
    }
}

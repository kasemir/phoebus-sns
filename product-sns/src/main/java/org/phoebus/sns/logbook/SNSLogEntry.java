package org.phoebus.sns.logbook;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.phoebus.logging.Attachment;
import org.phoebus.logging.LogEntry;
import org.phoebus.logging.Logbook;
import org.phoebus.logging.Property;
import org.phoebus.logging.Tag;
import org.phoebus.sns.logbook.elog.ELogEntry;

public class SNSLogEntry implements LogEntry
{
    final private ELogEntry entry;

    public SNSLogEntry(final ELogEntry entry)
    {
        this.entry = entry;
    }
    
    @Override
    public Long getId()
    {
       return entry.getId();
    }

    @Override
    public String getOwner()
    {
        return entry.getUser();
    }

    @Override
    public String getDescription()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLevel()
    {
        return entry.getPriority().getName();
    }

    @Override
    public Instant getCreatedDate()
    {
        return entry.getDate().toInstant();
    }

    @Override
    public Instant getModifiedDate()
    {
        return entry.getDate().toInstant();
    }

    @Override
    public int getVersion()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Collection<Tag> getTags()
    {
        return Converter.convertCategories(entry.getCategories());
    }

    @Override
    public Tag getTag(String tagName)
    {
        Map<String, Object> tags = getTags().stream().collect(Collectors.toMap(Tag::getName, tag -> tag));
        return (Tag) tags.get(tagName);
    }

    @Override
    public Collection<Logbook> getLogbooks()
    {
        return Converter.convertLogbooks(entry.getLogbooks());
    }

    @Override
    public Collection<Attachment> getAttachments()
    {
        return Converter.convertAttachments(entry.getImages(), entry.getAttachments());
    }

    @Override
    public Collection<Property> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public Property getProperty(String propertyName)
    {
        // TODO Auto-generated method stub 
        return null;
    }

}

package org.phoebus.sns.logbook;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.phoebus.logging.Attachment;
import org.phoebus.logging.LogClient;
import org.phoebus.logging.LogEntry;
import org.phoebus.logging.Logbook;
import org.phoebus.logging.Property;
import org.phoebus.logging.Tag;

public class SNSLogbookClient implements LogClient
{
    final private String url;
    final private String user;
    final private String password;
    
    public SNSLogbookClient(final String url, final String user, final String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    @Override
    public Collection<Logbook> listLogbooks()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Tag> listTags()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property> listProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> listAttributes(String propertyName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> listLogs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry getLog(Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Attachment> listAttachments(Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAttachment(Long logId, Attachment attachment)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAttachment(Long logId, String attachmentName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Property getProperty(String property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry set(LogEntry log)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> set(Collection<LogEntry> logs)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag set(Tag tag)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag set(Tag tag, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Logbook set(Logbook Logbook)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Logbook set(Logbook logbook, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Property set(Property property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry update(LogEntry log)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> update(Collection<LogEntry> logs)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Property update(Property property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag update(Tag tag, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag update(Tag tag, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Logbook update(Logbook logbook, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Logbook update(Logbook logbook, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry update(Property property, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Attachment add(File local, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LogEntry findLogById(Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogsBySearch(String pattern)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogsByTag(String pattern)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogsByLogbook(String logbook)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogsByProperty(String propertyName, String attributeName, String attributeValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogsByProperty(String propertyName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<LogEntry> findLogs(Map<String, String> map)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteTag(String tag)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteLogbook(String logbook)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteProperty(String property)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(LogEntry log)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Long logId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Collection<LogEntry> logs)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Tag tag, Long logId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Tag tag, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Logbook logbook, Long logId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Logbook logbook, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Property property, Long logId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Property property, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(String fileName, Long logId)
    {
        // TODO Auto-generated method stub
        
    }
}
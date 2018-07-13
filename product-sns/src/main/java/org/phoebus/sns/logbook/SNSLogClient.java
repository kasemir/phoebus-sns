/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.phoebus.logbook.Attachment;
import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogEntry;
import org.phoebus.logbook.Logbook;
import org.phoebus.logbook.Property;
import org.phoebus.logbook.Tag;
import org.phoebus.sns.logbook.elog.ELog;
import org.phoebus.sns.logbook.elog.ELogAttachment;
import org.phoebus.sns.logbook.elog.ELogEntry;

/**
 * SNS implementation of org.phoebus.logbook.LogClient
 * @author Evan Smith
 */
public class SNSLogClient implements LogClient
{
    final private String url;
    final private String user;
    final private String password;

    /**
     * LogClient that uses ELog
     * @param url URL to RDB
     * @param user
     * @param password
     */
    public SNSLogClient(final String url, final String user, final String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    @Override
    /** @{inheritDoc} */
    public Collection<Logbook> listLogbooks()
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            return Converter.convertLogbooks(elog.getLogbooks());
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<Tag> listTags()
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            return Converter.convertCategories(elog.getCategories());
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<Property> listProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<String> listAttributes(String propertyName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<LogEntry> listLogs()
    {
        
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            List<ELogEntry> elogEntries = elog.getEntries(Date.from(Instant.ofEpochSecond(0, 0)), Date.from(Instant.now()));
            // Create a list of SNSLogEntries that each wrap an ELogEntry
            Collection<LogEntry> entries = new ArrayList<LogEntry>();
            for (ELogEntry entry : elogEntries)
            {
                entries.add(new SNSLogEntry(entry));
            }
            
            return entries;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry getLog(Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            return new SNSLogEntry(elog.getEntry(logId));
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<Attachment> listAttachments(Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            Collection<ELogAttachment> elogImageAttachments = elog.getImageAttachments(logId);
            Collection<ELogAttachment> elogFileAttachments = elog.getOtherAttachments(logId);
            
            Collection<Attachment> attachments = new ArrayList<Attachment>();
            
            for (ELogAttachment attachment : elogImageAttachments)
                attachments.add(new SNSAttachment(attachment));
            
            for (ELogAttachment attachment : elogFileAttachments)
                    attachments.add(new SNSAttachment(attachment));
            
            return attachments;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public InputStream getAttachment(Long logId, Attachment attachment)
    {
        // TODO Work on this.
        Collection<Attachment> attachments = listAttachments(logId);
        Optional<Attachment> result = attachments.stream().filter(a -> attachment.equals(a)).findFirst();
        if (result.isPresent())
            return ((SNSAttachment) result.get()).getInputStream();
        else
            return null;
    }

    @Override
    /** @{inheritDoc} */
    public InputStream getAttachment(Long logId, String attachmentName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property getProperty(String property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry set(LogEntry log)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<LogEntry> set(Collection<LogEntry> logs)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag set(Tag tag)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag set(Tag tag, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook set(Logbook Logbook)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook set(Logbook logbook, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property set(Property property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry update(LogEntry log)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<LogEntry> update(Collection<LogEntry> logs)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property update(Property property)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag update(Tag tag, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag update(Tag tag, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook update(Logbook logbook, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook update(Logbook logbook, Collection<Long> logIds)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry update(Property property, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Attachment add(File local, Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry findLogById(Long logId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsBySearch(String pattern)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByTag(String pattern)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByLogbook(String logbook)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByProperty(String propertyName, String attributeName, String attributeValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByProperty(String propertyName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogs(Map<String, String> map)
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

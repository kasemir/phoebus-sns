/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook;

import static org.phoebus.ui.application.PhoebusApplication.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.phoebus.logbook.Attachment;
import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogEntry;
import org.phoebus.logbook.Logbook;
import org.phoebus.logbook.Property;
import org.phoebus.logbook.Tag;
import org.phoebus.sns.logbook.elog.ELog;
import org.phoebus.sns.logbook.elog.ELogAttachment;
import org.phoebus.sns.logbook.elog.ELogEntry;
import org.phoebus.sns.logbook.elog.ELogPriority;
import org.phoebus.util.time.TimestampFormats;


/**
 * SNS implementation of org.phoebus.logbook.LogClient
 * @author Evan Smith
 */
public class SNSLogClient implements LogClient
{
    /** Number of seconds in 48 hours. */
    final private static Long seconds24Hours = (long) (60 * 60 * 24);
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
        logger.log(Level.WARNING, "listProperties method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<String> listAttributes(String propertyName)
    {
        logger.log(Level.WARNING, "listAttributes method not supported by SNSLogClient.");
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
            Instant now = Instant.now();
            Instant yesterday = Instant.ofEpochSecond(now.getEpochSecond() - seconds24Hours);
            
           
            
            /*
             * TODO
             * 
             * The elog.getEntries(...) call causes "exceeded simultaneous SESSIONS_PER_USER limit" SQL exceptions.
             * 
             * Should investigate into Elog.getEntries to determine if this can be avoided.
             * 
             * Possible, but not preferred, solution is to break the retrieval up into smaller pieces.
             * For example: retrieve 6 hours of log entries at a time 8 times.
             * 
             */
            
            // Get every log entry from the last 24 hours.
            List<ELogEntry> elogEntries = elog.getEntries( Date.from(yesterday), Date.from(Instant.now()));
            
            // Create a list of SNSLogEntries
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
        // TODO Is this how this is supposed to work??
        Collection<Attachment> attachments = listAttachments(logId);
        Optional<Attachment> result = attachments.stream().filter(a -> attachment.equals(a)).findFirst();
        if (result.isPresent())
        {
            try
            {
                return new FileInputStream(result.get().getFile());
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
            return null;
    }

    @Override
    /** @{inheritDoc} */
    public InputStream getAttachment(Long logId, String attachmentName)
    {
        // TODO Work on this. Is the attachmentName the name of the attachment file? Attachments don't have a name field ...
        Collection<Attachment> attachments = listAttachments(logId);
        Optional<Attachment> result = attachments.stream().filter(a -> attachmentName.equals(a.getFile().getName())).findFirst();
        if (result.isPresent())
        {
            try
            {
                return new FileInputStream(result.get().getFile());
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
            return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property getProperty(String property)
    {
        logger.log(Level.WARNING, "getProperty method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry set(LogEntry log)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            
            Collection<Logbook> logbooks = log.getLogbooks();
            Iterator<Logbook> logIter = logbooks.iterator();
            
            Collection<Tag> tags = log.getTags();
            Iterator<Tag> tagIter = tags.iterator();
            
            Collection<Attachment> attachments = log.getAttachments();
            Iterator<Attachment> attachIter = attachments.iterator();
            
            // Create the entry
            String level = log.getLevel();
            
            ELogPriority priority = (level.isEmpty()) ? ELogPriority.Normal : ELogPriority.forName(level);
            long id = elog.createEntry(logIter.hasNext() ? logIter.next().getName() : "", log.getTitle(), log.getDescription(), priority);
            
            // Add all attached files to the entry
            while (attachIter.hasNext())
            {
                Attachment a = attachIter.next();
                
                // Get the file extension.
                int extIndex = a.getFile().getName().lastIndexOf(".");
                String extension = a.getFile().getName().substring(extIndex);
                
                // Get the last modified date.
                Instant date = Instant.ofEpochMilli(a.getFile().lastModified());
                
                // Build the attachment caption.
                String caption = TimestampFormats.SECONDS_FORMAT.format(date) + extension;
                
                elog.addAttachment(id, a.getFile().getName(), caption, new FileInputStream(a.getFile()));
            }
            
            // Add the log books to the entry
            while(logIter.hasNext())
            {
                Logbook l = logIter.next();
                elog.addLogbook(id, l.getName());
            }
            
            // Add the categories (tags) to the entry
            while(tagIter.hasNext())
            {
                Tag t = tagIter.next();
                elog.addCategory(id, t.getName());
            }
            
            return log;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<LogEntry> set(Collection<LogEntry> logEntries)
    {
        logger.log(Level.WARNING, "set(Collection<LogEntry>) not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag set(Tag tag)
    {
        logger.log(Level.WARNING, "set(Tag) not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag set(Tag tag, Collection<Long> logIds)
    {
        logger.log(Level.WARNING, "set(Tag, Collection<Long>) not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook set(Logbook logbook)
    {
        logger.log(Level.WARNING, "set(Logbook) not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook set(Logbook logbook, Collection<Long> logIds)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            for (Long logId : logIds)
            {
                elog.addLogbook(logId, logbook.getName());
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property set(Property property)
    {
        logger.log(Level.WARNING, "set(Property) method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry update(LogEntry log)
    {
        logger.log(Level.WARNING, "update(LogEntry) method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Collection<LogEntry> update(Collection<LogEntry> logs)
    {
        logger.log(Level.WARNING, "update(Collection<LogEntry>) method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Property update(Property property)
    {
        logger.log(Level.WARNING, "update(Property) method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag update(Tag tag, Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            elog.addLogbook(logId, tag.getName());
            return tag;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Tag update(Tag tag, Collection<Long> logIds)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            for (Long logId : logIds)
                elog.addLogbook(logId, tag.getName());
            return tag;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook update(Logbook logbook, Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            elog.addLogbook(logId, logbook.getName());
            return logbook;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Logbook update(Logbook logbook, Collection<Long> logIds)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            for (Long logId : logIds)
                elog.addLogbook(logId, logbook.getName());
            return logbook;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry update(Property property, Long logId)
    {
        logger.log(Level.WARNING, "update(Property, Long) not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public Attachment add(File local, Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            elog.addAttachment(logId, local.getName(), "", new FileInputStream(local));
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public LogEntry findLogById(Long logId)
    {
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            ELogEntry elogEntry = elog.getEntry(logId);
            return new SNSLogEntry(elogEntry);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsBySearch(String pattern)
    {
        /* 
         *  Thus far there is no way to perform such searches in ELog without creating the code first.
         *  
         *  So just ignore the search string and return the last days worth of logs regardless of what the pattern is. 
         *  This will allow the code that uses this method to at least work. 
         */
        
        try
        (
            final ELog elog = new ELog(url, user, password);
        )
        {
            Instant now = Instant.now();
            Instant yesterday = Instant.ofEpochSecond(now.getEpochSecond() - seconds24Hours);
            
            // Get every log entry from the last 48 hours.
            List<ELogEntry> elogEntries = elog.getEntries( Date.from(yesterday), Date.from(Instant.now()));
            
            // Create a list of SNSLogEntries
            List<LogEntry> entries = new ArrayList<LogEntry>();
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
    public List<LogEntry> findLogsByTag(String pattern)
    {
        // TODO Implement in ELog then implement method.
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByLogbook(String logbook)
    {
        // TODO Implement in ELog then implement method.
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByProperty(String propertyName, String attributeName, String attributeValue)
    {
        logger.log(Level.WARNING, "findLogsByProperty method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogsByProperty(String propertyName)
    {
        logger.log(Level.WARNING, "findLogsByProperty method not supported by SNSLogClient.");
        return null;
    }

    @Override
    /** @{inheritDoc} */
    public List<LogEntry> findLogs(Map<String, String> map)
    {
        // TODO What's supposed to be in the map?
        return null;
    }

    @Override
    public void deleteTag(String tag)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void deleteLogbook(String logbook)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void deleteProperty(String property)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient."); 
    }

    @Override
    public void delete(LogEntry log)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Long logId)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Collection<LogEntry> logs)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Tag tag, Long logId)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Tag tag, Collection<Long> logIds)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Logbook logbook, Long logId)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Logbook logbook, Collection<Long> logIds)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Property property, Long logId)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(Property property, Collection<Long> logIds)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }

    @Override
    public void delete(String fileName, Long logId)
    {
        logger.log(Level.WARNING, "delete operations not supported by SNSLogClient.");
    }
}

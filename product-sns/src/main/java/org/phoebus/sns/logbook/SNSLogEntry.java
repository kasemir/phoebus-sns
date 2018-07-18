/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.phoebus.logbook.Attachment;
import org.phoebus.logbook.LogEntry;
import org.phoebus.logbook.Logbook;
import org.phoebus.logbook.Property;
import org.phoebus.logbook.Tag;
import org.phoebus.sns.logbook.elog.ELogEntry;

/**
 * SNS implementation of org.phoebus.logbook.LogEntry
 * @author Evan Smith
 */
public class SNSLogEntry implements LogEntry
{
    final private ELogEntry entry;

    public SNSLogEntry(final ELogEntry entry)
    {
        this.entry = entry;
    }
    
    @Override
    /** {@inheritDoc} */
    public Long getId()
    {
       return entry.getId();
    }

    @Override
    /** {@inheritDoc} */
    public String getOwner()
    {
        return entry.getUser();
    }

    @Override
    /** {@inheritDoc} */
    public String getTitle()
    {
       return entry.getTitle();
    }
    
    @Override
    /** {@inheritDoc} */
    public String getDescription()
    {
        return entry.getText();
    }

    @Override
    /** {@inheritDoc} */
    public String getLevel()
    {
        return entry.getPriority().getName();
    }

    @Override
    /** {@inheritDoc} */
    public Instant getCreatedDate()
    {
        return entry.getDate().toInstant();
    }

    @Override
    /** {@inheritDoc} */
    public Instant getModifiedDate()
    {
        return entry.getDate().toInstant();
    }

    @Override
    /** {@inheritDoc} */
    public int getVersion()
    {
        return 0;
    }

    @Override
    /** {@inheritDoc} */
    public Collection<Tag> getTags()
    {
        return Converter.convertCategories(entry.getCategories());
    }

    @Override
    /** {@inheritDoc} */
    public Tag getTag(String tagName)
    {
        Map<String, Object> tags = getTags().stream().collect(Collectors.toMap(Tag::getName, tag -> tag));
        return (Tag) tags.get(tagName);
    }

    @Override
    /** {@inheritDoc} */
    public Collection<Logbook> getLogbooks()
    {
        return Converter.convertLogbooks(entry.getLogbooks());
    }

    @Override
    /** {@inheritDoc} */
    public Collection<Attachment> getAttachments()
    {
        return Converter.convertAttachments(entry.getImages(), entry.getAttachments());
    }

    @Override
    /** {@inheritDoc} */
    public Collection<Property> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    /** {@inheritDoc} */
    public Property getProperty(String propertyName)
    {
        // TODO Auto-generated method stub 
        return null;
    }
    
    @Override
    /** Return the log entry in a string format. */
    public String toString()
    {
        return entry.toString();
    }

}

/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.phoebus.logbook.Attachment;
import org.phoebus.sns.logbook.elog.ELogAttachment;

/**
 * SNS implementation of org.phoebus.logbook.Attachment
 * @author Evan Smith
 */
public class SNSAttachment implements Attachment
{
    final private ELogAttachment attachment;
    private File file;

    public SNSAttachment(final ELogAttachment attachment)
    {
        this.attachment = attachment;       
    }
    
    /** Get a stream of the attachments data. */
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(attachment.getData());
    }

    @Override
    /** {@inheritDoc} */
    public String getContentType()
    {
        return attachment.getType();
    }

    @Override
    /** {@inheritDoc} */
    public Boolean getThumbnail()
    {
        return attachment.isImage();
    }

    @Override
    /** {@inheritDoc} */
    public File getFile()
    {
        try
        {
            // TODO This is in /tmp so will get cleaned by OS, is that OK?
            file = File.createTempFile("attachment", null);
        } 
        catch (IOException ex)
        {
            // TODO How to handle this? Gracefully or loudly? Can't throw without changing interface ...
            file = null;
            ex.printStackTrace();
        }
        try 
        (
            FileOutputStream stream = new FileOutputStream(file);
        )
        {
            stream.write(attachment.getData());
        } 
        catch (IOException ex1)
        {
            // TODO How to handle this? Gracefully or loudly?
            ex1.printStackTrace();
        }
        
        return file;
    }

}

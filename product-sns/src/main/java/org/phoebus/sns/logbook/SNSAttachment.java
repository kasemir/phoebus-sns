package org.phoebus.sns.logbook;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.phoebus.logbook.Attachment;
import org.phoebus.sns.logbook.elog.ELogAttachment;

public class SNSAttachment implements Attachment
{
    final private ELogAttachment attachment;
    
    public SNSAttachment(final ELogAttachment attachment)
    {
        this.attachment = attachment;
    }
    
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream(attachment.getData());
    }

    @Override
    public String getContentType()
    {
        return attachment.getType();
    }

    @Override
    public Boolean getThumbnail()
    {
        return attachment.isImage();
    }

    @Override
    public File getFile()
    {
        // TODO Auto-generated method stub
        return null;
    }

}

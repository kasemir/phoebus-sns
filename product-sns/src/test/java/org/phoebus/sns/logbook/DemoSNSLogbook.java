package org.phoebus.sns.logbook;

import java.util.Collection;

import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogEntry;
import org.phoebus.logbook.LogEntryImpl.LogEntryBuilder;
import org.phoebus.logbook.LogFactory;
import org.phoebus.logbook.LogService;
import org.phoebus.logbook.Logbook;
import org.phoebus.logbook.Tag;

public class DemoSNSLogbook
{
    private static final LogService logSerivce = LogService.getInstance();                                      // Get the instance of the log service.
    private static final LogFactory logFactory = logSerivce.getLogFactories().get("org.phoebus.sns.logbook");  // Get the sns implementation of the log factory.
    private static final LogClient  logClient  = logFactory.getLogClient();                                    // Get the read only log book client.
    
    private DemoSNSLogbook()
    {
        testRetrieval();
    }
    
    /** Test the ability of the LogClient to retrieve log books and tags */
    private void testRetrieval()
    {
        Collection<Logbook> logbooks = logClient.listLogbooks();
        Collection<Tag>     tags     = logClient.listTags();
        
        System.out.println("Logbooks:");
        for (Logbook logbook : logbooks)
        {
            System.out.println("\t" + logbook.getName());
        }
        System.out.println("Tags:");
        for (Tag tag : tags)
        {
            System.out.println("\t" + tag.getName());
        }
    }
    
    @SuppressWarnings("unused")
    private void testEntry()
    {
        LogEntryBuilder logEntryBuilder = new LogEntryBuilder();
        LogEntry logEntry = logEntryBuilder.build();
        logClient.set(logEntry);
    }
    
    public static void main(String[] args)
    {
        new DemoSNSLogbook();
    }
    
}

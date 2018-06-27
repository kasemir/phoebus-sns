package org.phoebus.sns.logbook.elog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class DemoELog
{
    
    private String url = "???", user = "???", password = "???";
    
    /** Demonstrate ELog.getLogbooks() by listing all the current log books. */
    private void DemoELogListLogbooks() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            System.out.println("Current Logbooks:");
            for (String logbook : elog.getLogbooks())
            {
                System.out.println("\t" + logbook);
            }
            System.out.println();
        }
    }
    
    /** Demonstrate ELog.getCategories by printing all the current categories. */
    private void DemoELogGetCategories() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            System.out.println("Current Log Categories:");
            for (ELogCategory category : elog.getCategories())
            {
                System.out.println("\t" + category.toString());
            }
            System.out.println();
        }
    }
    
    /** Demonstrate ELog.getEntries() by getting the last hour's log entries. */
    private void DemoELogGetEntries() throws Exception
    {

        try
        (
            ELog elog = new ELog(url, user, password);
        )
        {
            // Get dates for now and an hour ago.
            Long hourAgoSeconds = (long) 3600;
            
            Instant now     = Instant.now();
            Instant hourAgo = Instant.ofEpochSecond(now.getEpochSecond() - hourAgoSeconds);
   
            Date start = Date.from(hourAgo);
            Date end   = Date.from(now);
            
            // Get all log entries from the last hour.
            List<ELogEntry> entries = elog.getEntries(start, end);
            
            System.out.println("The last hours log entries: ");
            for (ELogEntry entry : entries)
            {
                System.out.println("\t" + entry.toString());
            }
            System.out.println();
        }
    }

    /** Demonstrate ELog.createEntry() by creating an entry in the "Scratch Pad" log book. */
    private void DemoELogCreateEntry() throws Exception
    {
        try
        (
            ELog elog = new ELog(url, user, password);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        )
        {
            System.out.print("Please enter message to send as a test to the Scratch Pad Logbook: ");
            final String testMessage = reader.readLine();
            
            System.out.println("\nSending message \"" + testMessage +"\" to Scratch Pad.\n");
            
            System.out.print("Type \"send\" to confirm log entry creation: ");
            final String confirm = reader.readLine();
            System.out.println();
            if (confirm.equalsIgnoreCase("send"))
            {
                    elog.createEntry("Scratch Pad", "Test", testMessage, ELogPriority.Normal);
                    System.out.println("Entry created.");
            }
            else
                System.out.println("Entry creation canceled.");
        }
    }
    
    private DemoELog()
    {
        try
        {
            /**
             * Credential File "test_cred" is a file containing the following.
             * 
             * # URL of relational database.
             * url=url_to_rdb
             * 
             * # User credentials
             * user=user_name
             * password=user_password
             * 
             * Any lines beginning with # will be ignored.
             * The terms after the "=" should be replaced with the user's specific information.
             */
            File credentialFile = new File("test_cred");

            try
            (
                FileReader fReader = new FileReader(credentialFile);
                BufferedReader reader = new BufferedReader(fReader);
            )
            {
                String line = null;
                while ( null != (line = reader.readLine()) )
                {
                    if (line.startsWith("#") || line.isEmpty())
                        continue;
                    
                    if (line.startsWith("url="))
                    {
                        url = line.substring(4);
                    }
                    else if (line.startsWith("user="))
                    {
                        user = line.substring(5);
                    }
                    else if (line.startsWith("password="))
                    {
                        password = line.substring(9);
                    }
                }
            }
            
            DemoELogListLogbooks();
            DemoELogGetCategories();
            DemoELogGetEntries();
            DemoELogCreateEntry();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        new DemoELog();
    }
}

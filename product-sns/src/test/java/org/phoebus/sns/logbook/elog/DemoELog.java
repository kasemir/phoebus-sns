package org.phoebus.sns.logbook.elog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class DemoELog
{
    
    private DemoELog()
    {
        String url = "???", user = "???", password = "???";
        try
        {
            File credentialFile = new File("./product-sns/src/test/java/org/phoebus/sns/logbook/elog/test_cred");
            
            
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

            try
            (
                ELog elog = new ELog(url, user, password);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            )
            {
                System.out.println("Current Logbooks:");
                for (String logbook : elog.getLogbooks())
                {
                    System.out.println("\t" + logbook);
                }
                System.out.println();
                
                System.out.print("Please enter message to send as a test to the Scratch Pad Logbook: ");
                final String testMessage = reader.readLine();
                
                System.out.println(testMessage);
                //elog.createEntry("Scratch Pad", "Test", testMessage, ELogPriority.Normal);
            }
            
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

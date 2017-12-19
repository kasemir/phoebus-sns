package org.phoebus.archive.reader.rdb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.phoebus.archive.reader.ArchiveReader;
import org.phoebus.archive.reader.ValueIterator;
import org.phoebus.vtype.VType;


@SuppressWarnings("nls")
public class RDBArchiveReaderDemo
{
    @Before
    public void setup()
    {
        final Preferences prefs = Preferences.userNodeForPackage(RDBArchiveReader.class);
        prefs.put(RDBArchiveReader.USER, "sns_reports");
        prefs.put(RDBArchiveReader.PASSWORD, "sns");
        prefs.put(RDBArchiveReader.PREFIX, "chan_arch.");
        prefs.put(RDBArchiveReader.STORED_PROCEDURE, "chan_arch.archive_reader_pkg.get_browser_data");
        prefs.put(RDBArchiveReader.STARTTIME_FUNCTION, "SELECT chan_arch.archive_reader_pkg.get_actual_start_time (?, ?, ?)  FROM DUAL");
    }

    @Test
    public void testReader() throws Exception
    {
        ArchiveReader reader = new RDBArchiveReader("Instruments", "jdbc:oracle:thin:@snsoroda-scan.sns.gov:1521/scprod_controls");

        List<String> names = reader.getNamesByPattern("");
        System.out.println(names);
        assertThat(names.size(), equalTo(0));

        names = reader.getNamesByPattern("BL7:Mot:*X");
        System.out.println(names);

        final Instant end = Instant.now();
        final Instant start = end.minus(Duration.ofHours(1));
        System.out.println("Raw samples");
        int count = 0;
        ValueIterator iter = reader.getRawValues("BL7:Chop:Skf1:MotorTempF", start, end);
        while (iter.hasNext())
        {
            final VType sample = iter.next();
            System.out.println(sample);
            ++count;
        }
        iter.close();
        System.out.println(count + " raw samples\n");

        System.out.println("Optimized samples");
        count = 0;
        iter = reader.getOptimizedValues("BL7:Chop:Skf1:MotorTempF", start, end, 50);
        while (iter.hasNext())
        {
            final VType sample = iter.next();
            System.out.println(sample);
            ++count;
        }
        iter.close();
        System.out.println(count + " optimized samples\n");


        reader.close();
    }
}

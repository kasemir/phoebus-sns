/*******************************************************************************
 * Copyright (c) 2017-2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.archive.reader.rdb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.prefs.Preferences;

import org.epics.vtype.VType;
import org.junit.Before;
import org.junit.Test;
import org.phoebus.archive.reader.ArchiveReader;
import org.phoebus.archive.reader.ArchiveReaders;
import org.phoebus.archive.reader.ValueIterator;

@SuppressWarnings("nls")
public class RDBArchiveReaderDemo
{
    @Before
    public void setup()
    {
        final Preferences prefs = Preferences.userNodeForPackage(RDBArchiveReader.class);
        prefs.put("user", "sns_reports");
        prefs.put("password", "sns");
        prefs.put("prefix", "chan_arch.");
        prefs.put("stored_procedure", "chan_arch.archive_reader_pkg.get_browser_data");
        prefs.put("starttime_function", "SELECT chan_arch.archive_reader_pkg.get_actual_start_time (?, ?, ?)  FROM DUAL");
    }

    @Test
    public void testReader() throws Exception
    {
        ArchiveReader reader = ArchiveReaders.createReader("jdbc:oracle:thin:@snsoroda-scan.sns.gov:1521/scprod_controls");

        Collection<String> names = reader.getNamesByPattern("");
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

/*******************************************************************************
 * Copyright (c) 2017-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.archive.reader.channelarchiver;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import org.epics.vtype.VType;
import org.junit.jupiter.api.Test;
import org.phoebus.archive.reader.ArchiveReader;
import org.phoebus.archive.reader.ArchiveReaders;
import org.phoebus.archive.reader.ValueIterator;

/** Test/Demo of the Channel Archiver file reader
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelArchiverReaderTest
{
    @Test
    public void testReadFile() throws Exception
    {
        File index = new File(System.getProperty("user.home"), "git/DemoData/index");
        String channel = "DTL_HPRF:Tnk1:T";

        // index = new File(System.getProperty("user.home"), "git/01_05/index");
        // channel = "DTL_HPRF:Cath2:I";

        System.out.println(index);
        if (! index.canRead())
            return;

        final ArchiveReader reader = ArchiveReaders.createReader("cadf:" + index.getAbsolutePath());
        for (String name : reader.getNamesByPattern("DTL_HPRF:*"))
            System.out.println(name);


        final Instant end = Instant.now();
        final Instant start = end.minus(Duration.ofDays(365*20));
        final ValueIterator iter = reader.getRawValues(channel, start, end);
        while (iter.hasNext())
        {
            final VType sample = iter.next();
            System.out.println(sample);
        }
        iter.close();
        reader.close();
    }
}

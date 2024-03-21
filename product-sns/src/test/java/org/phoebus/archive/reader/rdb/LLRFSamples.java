/*******************************************************************************
 * Copyright (c) 2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.archive.reader.rdb;

import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.epics.vtype.VNumber;
import org.epics.vtype.VType;
import org.phoebus.archive.reader.ArchiveReader;
import org.phoebus.archive.reader.ValueIterator;
import org.phoebus.framework.preferences.PropertyPreferenceLoader;

/** Example for a custom archive report:
 *  LLRF power averages for some time range,
 *  only considering samples where RF is "on",
 *  using a threshold to determine on/off state.
 *
 *  @author Kay Kasemir
 */
public class LLRFSamples
{
    public static void main(String[] args) throws Exception
    {
//        final LocalDateTime local_start =  LocalDateTime.of(2024, 3, 20, 0, 0);
        final LocalDateTime local_start =  LocalDateTime.of(2023, 6, 3, 0, 0);
        final LocalDateTime local_end =  local_start.plus(Duration.ofDays(3));
        final Instant start = local_start.atZone(ZoneId.systemDefault()).toInstant();
        final Instant end =   local_end.atZone(ZoneId.systemDefault()).toInstant();

        PropertyPreferenceLoader.load("product-sns/settings.ini");
        final String URL = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=snsappa.sns.ornl.gov)(PORT=1610))(ADDRESS=(PROTOCOL=TCP)(HOST=snsappb.sns.ornl.gov)(PORT=1610))(CONNECT_DATA=(SERVICE_NAME=prod_controls)))";

        final List<String> sys = new ArrayList<>(), inst = new ArrayList<>();
//        sys.add("RFQ"); inst.add("");
//        for (int i=1; i<=4; ++i)
//        {  sys.add("MEBT"); inst.add(Integer.toString(i)); }
//        for (int i=1; i<=6; ++i)
//        {  sys.add("DTL"); inst.add(Integer.toString(i)); }
//        for (int i=1; i<=4; ++i)
//        {  sys.add("CCL"); inst.add(Integer.toString(i)); }

        for (int i=1; i<=11; ++i)
            for (char c='a'; c<='c'; ++c)
            {  sys.add("SCL"); inst.add(String.format("%02d%c", i, c)); }
        for (int i=12; i<=23; ++i)
            for (char c='a'; c<='d'; ++c)
            {  sys.add("SCL"); inst.add(String.format("%02d%c", i, c)); }
        for (int i=29; i<=32; ++i)
            for (char c='a'; c<='d'; ++c)
            {  sys.add("SCL"); inst.add(String.format("%02d%c", i, c)); }

        try (ArchiveReader reader = new RDBArchiveReader(URL);
             PrintStream out = new PrintStream(String.format("LLRF_%04d_%02d_%02d.csv", local_start.getYear(), local_start.getMonth().getValue(), local_start.getDayOfMonth()));
            )
        {
            out.format("%04d-%02d-%02d to %04d-%02d-%02d",
                       local_start.getYear(), local_start.getMonth().getValue(), local_start.getDayOfMonth(),
                       local_end.getYear(), local_end.getMonth().getValue(), local_end.getDayOfMonth());

            out.println("Cavity\tFCM cavV\tFCM cavAmpAvg\tHPM ADCSnap1_Pwr");

            for (int i=0; i<sys.size(); ++i)
            {
                final String system = sys.get(i) + inst.get(i);
                out.print(system);
                System.out.println(system);
                for (int pv=0; pv<3; ++pv)
                {
                    final String name;
                    final double threshold;
                    switch (pv)
                    {
                    case 0: name = String.format("%s_LLRF:FCM%s:cavV", sys.get(i), inst.get(i));
                            threshold = 5.0; // HPM Cavity Field
                            break;
                    case 1: name = String.format("%s_LLRF:FCM%s:cavAmpAvg", sys.get(i), inst.get(i));
                            threshold = 5.0; // FCM Field
                            break;
                    default:name = String.format("%s_LLRF:HPM%s:ADCSnap1_Pwr", sys.get(i), inst.get(i));
                            threshold = 20.0; // Forward
                    }

                    try (ValueIterator values = reader.getRawValues(name, start, end))
                    {
                        int N = 0;
                        double avg = 0.0;
                        while (values.hasNext())
                        {
                            final VType value = values.next();
                            if (value instanceof VNumber)
                            {
                                double number = ((VNumber) value).getValue().doubleValue();
                                if (number >= threshold)
                                {
                                    ++N;
                                    avg += number;
                                }
                            }
                        }
                        if (N > 0)
                            avg /= N;
                        out.format("\t%.2f", avg);
                    }
                }

                out.println();
            }
        }
    }
}

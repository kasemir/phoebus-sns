/*******************************************************************************
 * Copyright (c) 2020-2025 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.channelfinder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.phoebus.channelfinder.Channel;
import org.phoebus.channelfinder.ChannelFinderClient;
import org.phoebus.channelfinder.ChannelFinderService;
import org.phoebus.channelfinder.Property;
import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.Preference;
import org.phoebus.framework.rdb.RDBInfo;
import org.phoebus.sns.completion.SNSPVProposals;
import org.phoebus.util.time.TimestampFormats;

/** 'Main' for channel finder maintenance
 *
 *  <p>Invoke via "phoebus.sh -main org.phoebus.sns.channelfinder.CmdTool -help"
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CmdTool
{
    private final ChannelFinderClient cf;

    /** URL, user, pass, URL, user, pass, ... */
    @Preference(name="sources") private static String[] infos;

    private final String owner = "admin"; // System.getProperty("user.name") ?

    public CmdTool(final ChannelFinderClient cf) throws Exception
    {
        this.cf = cf;
        AnnotatedPreferences.initialize(SNSPVProposals.class, CmdTool.class, "/pv_proposals_preferences.properties");
    }

    private void list(final String pattern) throws Exception
    {
        System.out.println("Channels matching pattern '" + pattern + "'");
        int i = 0;
        for (Channel channel : cf.findByName(pattern))
        {
            ++i;
            System.out.format("%4d : %s\n", i, channel.getName());
        }
    }

    /*
        SELECT fld_id, fld_val, rec_type_id, ioc_nm, file_nm, boot_dte
          FROM epics.sgnl_fld_v
         WHERE sgnl_id=?

        CREATE VIEW EPICS.SGNL_FLD_V
        AS SELECT b.rec_nm, c.rec_type, e.fld_type, e.dbd_type, d.fld_val, h.ioc_nm, a.ioc_boot_date, g.uri
          FROM irmisbase.ioc_boot a,
               irmisbase.rec b,
               irmisbase.rec_type c,
               irmisbase.fld d,
               irmisbase.fld_type e,
               irmisbase.ioc_resource f,
               irmisbase.uri g,
               irmisbase.ioc h
         WHERE a.current_load = 1
           AND a.ioc_id = h.ioc_id
           AND a.ioc_boot_id = b.ioc_boot_id
           AND a.ioc_boot_id = c.ioc_boot_id
           AND a.ioc_boot_id = f.ioc_boot_id
           AND f.ioc_resource_id = d.ioc_resource_id
           AND f.uri_id = g.uri_id
           AND b.rec_type_id = c.rec_type_id
           AND b.rec_id = d.rec_id
           AND d.fld_type_id = e.fld_type_id

          SELECT r.rec_nm, i.ioc_nm, b.ioc_boot_date
           FROM irmisbase.rec r
           JOIN irmisbase.ioc_boot b ON b.ioc_boot_id = r.ioc_boot_id
           JOIN irmisbase.ioc i      ON b.ioc_id = i.ioc_id
           WHERE b.current_load = 1
             AND r.rec_nm LIKE '%_LLRF:IOC%:Load';
   */
    private void importChannels(final String pattern) throws Exception
    {
        System.out.println("Reading IRMIS channels from " + infos[0] + " as " + infos[1] + "/" + infos[2]);

        final boolean all = pattern.equals("%");

        try
        (
            Connection connection = new RDBInfo(infos[0], infos[1], infos[2]).connect();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT r.rec_nm, i.ioc_nm, b.ioc_boot_date" +
                "  FROM irmisbase.rec r" +
                "  JOIN irmisbase.ioc_boot b ON b.ioc_boot_id = r.ioc_boot_id" +
                "  JOIN irmisbase.ioc i      ON b.ioc_id = i.ioc_id" +
                "  WHERE b.current_load = 1" +
                (all ? "" : " AND r.rec_nm LIKE ?"));
        )
        {
            if (! all)
                statement.setString(1, pattern);
            statement.setFetchDirection(ResultSet.FETCH_FORWARD);
            statement.setFetchSize(10000);

            int i = 0;
            try
            (
                ResultSet result = statement.executeQuery()
            )
            {
                // Properties must exist before they're assigned
                cf.set(Property.Builder
                        .property("iocName")
                        .owner(owner));
                cf.set(Property.Builder
                        .property("time")
                        .owner(owner));
                while (result.next())
                {
                    ++i;
                    final String channel = result.getString(1);
                    final String ioc = result.getString(2);
                    final Instant boot_time = result.getTimestamp(3).toInstant();
                    final String boot_stamp =  TimestampFormats.SECONDS_FORMAT.format(boot_time);
                    System.out.println(i + ": " + channel + " on " + ioc + " at " + boot_stamp);

                    cf.set(Channel.Builder
                                  .channel(channel)
                                  .with(Property.Builder.property("iocName", ioc))
                                  .with(Property.Builder.property("time", boot_stamp))
                                  .owner(owner));
                }
            }
        }
    }

    private void delete(final String name) throws Exception
    {
        cf.deleteChannel(name);
    }

    private void deleteByPattern(final String pattern) throws Exception
    {
        int i = 0;
        for (Channel channel : cf.findByName(pattern))
        {
            ++i;
            final String name = channel.getName();
            System.out.println("Deleting " + i + ": " + name);
            delete(name);
        }
    }

    private static void help()
    {
        System.out.println("Usage: -main " + CmdTool.class.getName() + " [options]");
        System.out.println();
        System.out.println("Channel Finder command line tool");
        System.out.println();
        System.out.println("-help                           -  This text");
        System.out.println("-list pattern                   -  List channel names for pattern");
        System.out.println("-delete pattern                 -  Delete channel names for pattern");
        System.out.println("-import rdb_pattern             -  Import IRMIS channels for RDB pattern");
        System.out.println();
        System.out.println("Patterns:     '*', 'DTL_LLRF:*:Load, ...");
        System.out.println("RDB Patterns: '%', 'DTL_LLRF:%:Load, ...");
    }

    public static void main(String[] original_args) throws Exception
    {
        final List<String> args = new ArrayList<>(List.of(original_args));
        if (args.isEmpty())
        {
            help();
            return;
        }

        try (ChannelFinderClient cf = ChannelFinderService.getInstance().getClient())
        {
        	final CmdTool imp = new CmdTool(cf);
            final Iterator<String> iter = args.iterator();
            while (iter.hasNext())
            {
                final String cmd = iter.next();
                if (cmd.startsWith("-h"))
                {
                    help();
                    return;
                }
                else if (cmd.startsWith("-list"))
                {
                    if (! iter.hasNext())
                        throw new Exception("Missing -list pattern");
                    iter.remove();
                    final String pattern = iter.next();
                    iter.remove();
                    imp.list(pattern);
                }
                else if (cmd.startsWith("-delete"))
                {
                    if (! iter.hasNext())
                        throw new Exception("Missing -delete pattern");
                    iter.remove();
                    final String pattern = iter.next();
                    iter.remove();
                    imp.deleteByPattern(pattern);
                }
                else if (cmd.startsWith("-import"))
                {
                    if (! iter.hasNext())
                        throw new Exception("Missing -import pattern");
                    iter.remove();
                    final String pattern = iter.next();
                    iter.remove();
                    imp.importChannels(pattern);
                }
                else
                {
                    System.out.println("Unknown parameters " + args);
                    help();
                    return;
                }
            }
        }
    }
}

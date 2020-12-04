package org.phoebus.sns.channelfinder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

import org.phoebus.channelfinder.Channel;
import org.phoebus.channelfinder.ChannelFinderClient;
import org.phoebus.channelfinder.ChannelFinderService;
import org.phoebus.channelfinder.Property;
import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.Preference;
import org.phoebus.framework.rdb.RDBInfo;
import org.phoebus.sns.completion.SNSPVProposals;
import org.phoebus.util.time.TimestampFormats;

@SuppressWarnings("nls")
public class IrmisImport
{
    private final ChannelFinderClient cf;

    /** URL, user, pass, URL, user, pass, ... */
    @Preference(name="sources") private static String[] infos;

    private final String owner = "admin"; // System.getProperty("user.name") ?

    public IrmisImport() throws Exception
    {
        cf = ChannelFinderService.getInstance().getClient();

        AnnotatedPreferences.initialize(SNSPVProposals.class, IrmisImport.class, "/pv_proposals_preferences.properties");
    }

    private void test() throws Exception
    {
        for (Channel channel : cf.findByName("BR:C001-PS:1{HC}O*"))
            System.out.println(channel.getName());
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
    private void importChannels() throws Exception
    {
        System.out.println("Reading IRMIS channels from " + infos[0] + " as " + infos[1] + "/" + infos[2]);
        try
        (
            Connection connection = new RDBInfo(infos[0], infos[1], infos[2]).connect();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT r.rec_nm, i.ioc_nm, b.ioc_boot_date" +
                "  FROM irmisbase.rec r" +
                "  JOIN irmisbase.ioc_boot b ON b.ioc_boot_id = r.ioc_boot_id" +
                "  JOIN irmisbase.ioc i      ON b.ioc_id = i.ioc_id" +
                "  WHERE b.current_load = 1" +
                " AND r.rec_nm LIKE ?");
        )
        {
            statement.setString(1, "%_LLRF:IOC%:Load");

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
                    final String boot_stamp =  TimestampFormats.FULL_FORMAT.format(boot_time);
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

    private void add(final String name) throws Exception
    {
        // Properties must exist before they're assigned
        cf.set(Property.Builder
                       .property("iocName")
                       .owner(owner));
        cf.set(Channel.Builder
                      .channel(name)
                      .with(Property.Builder.property("family", "42"))
                      .with(Property.Builder.property("iocName", "example"))
                      .owner(owner));
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
            cf.deleteChannel(name);
        }
    }

    private void close()
    {
        cf.close();
    }

    public static void main(String[] args) throws Exception
    {
        final IrmisImport imp = new IrmisImport();
        imp.test();
        imp.add("DemoChannel");
        imp.delete("DemoChannel");
        imp.deleteByPattern("BR:*");

        imp.importChannels();
        imp.close();
    }
}

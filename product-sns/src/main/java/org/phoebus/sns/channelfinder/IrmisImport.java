package org.phoebus.sns.channelfinder;

import org.phoebus.channelfinder.Channel;
import org.phoebus.channelfinder.ChannelFinderClient;
import org.phoebus.channelfinder.ChannelFinderService;
import org.phoebus.channelfinder.Property;

@SuppressWarnings("nls")
public class IrmisImport
{
    private final ChannelFinderClient cf;

    public IrmisImport() throws Exception
    {
        cf = ChannelFinderService.getInstance().getClient();
    }

    private void test() throws Exception
    {
        for (Channel channel : cf.findByName("BR:C001-PS:1{HC}O*"))
            System.out.println(channel.getName());
    }

    private void add(final String name) throws Exception
    {
        // Properties must exist before they're assigned
        cf.set(Property.Builder
                       .property("iocName")
                       .owner(System.getProperty("user.name")));
        cf.set(Channel.Builder
                      .channel(name)
                      .with(Property.Builder.property("family", "42"))
                      .with(Property.Builder.property("iocName", "example"))
                      .owner(System.getProperty("user.name")));
    }

    private void delete(final String name) throws Exception
    {
        cf.deleteChannel(name);
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
        imp.close();
    }
}

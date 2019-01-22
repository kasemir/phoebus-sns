package org.phoebus.sns.mpsbypasses;

import org.junit.Test;
import org.phoebus.framework.jobs.BasicJobMonitor;
import org.phoebus.framework.rdb.RDBConnectionPool;
import org.phoebus.sns.mpsbypasses.model.RequestLookup;

/** [Headless] JUnit Plug-in test of the {@link RequestLookup}
 *  @author Kay Kasemir
 */
public class RequestLookupTest
{
	@Test
	public void testBypassModel() throws Exception
	{
		final RDBConnectionPool rdb = new RDBConnectionPool(MPSBypasses.url,  MPSBypasses.user, MPSBypasses.password);
		RequestLookup requests = new RequestLookup(new BasicJobMonitor(), rdb.getConnection());
		requests.dump(System.out);
		rdb.clear();
	}
}

package org.phoebus.sns.mpsbypasses;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.phoebus.framework.jobs.BasicJobMonitor;
import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.sns.mpsbypasses.model.BypassModel;
import org.phoebus.sns.mpsbypasses.model.BypassModelListener;
import org.phoebus.sns.mpsbypasses.model.BypassState;
import org.phoebus.sns.mpsbypasses.model.RequestState;
import org.phoebus.sns.mpsbypasses.modes.MachineMode;

/** [Headless] JUnit Plug-in test of the {@link BypassModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassModelTest implements BypassModelListener
{
	final private AtomicInteger updates = new AtomicInteger(0);

	@Test
	public void testBypassModel() throws Exception
	{
		final BypassModel model = new BypassModel();

		model.setFilter(BypassState.All, RequestState.All);

		model.addListener(this);

		// This will take some time, then call modelLoaded:
		System.out.println("Loading RDB data...");
		model.selectMachineMode(new BasicJobMonitor()
		{
            @Override
            public void beginTask(String task_name)
            {
                System.out.println(task_name);
            }
		}, MachineMode.Site);

		// Allow model to 'run' for a while, connect to PVs and send updates
		Thread.sleep(5000);

		model.stop();
		// Should have had some updates
		final int received = updates.get();
		System.out.println("Got " + received + " updates");
		assertTrue(received > 0);
	}

	@Override
    public void modelLoaded(final BypassModel model, final Exception error)
    {
		if (error != null)
			error.printStackTrace();
		else
		{
		    for (Bypass bypass : model.getBypasses())
		        System.out.println(bypass);

			System.out.println("RDB data arrived, starting model...");
		}
    }

	@Override
    public void bypassesChanged()
    {
	    // Ignore
    }

	@Override
    public void bypassChanged(final Bypass bypass)
    {
		updates.incrementAndGet();
		if (bypass.getState() != BypassState.Disconnected)
			System.out.println(bypass);
    }
}

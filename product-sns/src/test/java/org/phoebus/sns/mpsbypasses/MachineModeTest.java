package org.phoebus.sns.mpsbypasses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.phoebus.sns.mpsbypasses.modes.MachineMode;
import org.phoebus.sns.mpsbypasses.modes.MachineModeListener;
import org.phoebus.sns.mpsbypasses.modes.MachineModeMonitor;

/** [Headless] JUnit test of the {@link MachineModeMonitor}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MachineModeTest implements MachineModeListener
{
    final private CountDownLatch done = new CountDownLatch(1);

	@Override
    public void machineModeUpdate(final MachineMode rtdl_mode, final MachineMode switch_mode)
    {
        System.out.println("Mode is: RTDL " + rtdl_mode + ", MPS switches " + switch_mode);
        if (rtdl_mode != null  &&  switch_mode != null)
            done.countDown();
    }

	@Test
	public void testMachineModeMonitor() throws Exception
	{
		final MachineModeMonitor mm = new MachineModeMonitor(this);
		System.out.println("MachineModeMonitor...");
		mm.start();
		done.await(5, TimeUnit.SECONDS);
        System.out.println("stop.");
		mm.stop();
        assertEquals(0, done.getCount());
	}
}

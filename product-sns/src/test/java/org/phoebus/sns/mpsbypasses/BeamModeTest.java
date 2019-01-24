package org.phoebus.sns.mpsbypasses;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.phoebus.pv.PVPool;
import org.phoebus.sns.mpsbypasses.modes.BeamMode;
import org.phoebus.sns.mpsbypasses.modes.BeamModeListener;
import org.phoebus.sns.mpsbypasses.modes.BeamModeMonitor;

/** [Headless] JUnit test of the {@link BeamModeMonitor}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamModeTest implements BeamModeListener
{
    final private CountDownLatch done = new CountDownLatch(1);

	@Override
    public void beamModeUpdate(final BeamMode rtdl_mode, final BeamMode switch_mode)
    {
	    System.out.println("Mode is: RTDL " + rtdl_mode + ", MPS switches " + switch_mode);
	    if (rtdl_mode != null  &&  switch_mode != null)
	        done.countDown();
    }

	@Test
	public void testBeamModeMonitor() throws Exception
	{
		final BeamModeMonitor mm = new BeamModeMonitor(this);
		System.out.println("BeamModeMonitor...");
		mm.start();
		done.await(5, TimeUnit.SECONDS);
        System.out.println("stop.");
		mm.stop();
		assertEquals(0, done.getCount());

		System.out.println("Unreleased PVs:");
		PVPool.getPVReferences().forEach(ref -> System.out.println(ref.getEntry()));
		assertEquals(0, PVPool.getPVReferences().size());
	}
}

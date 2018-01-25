package org.phoebus.sns.completion;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.phoebus.framework.autocomplete.PVProposalService;
import org.phoebus.framework.autocomplete.ProposalService.Handler;

@SuppressWarnings("nls")
public class SNSPVProposalsDemo
{
    @Test
    public void testLookup() throws Exception
    {
        final AtomicBoolean got_SNS_response = new AtomicBoolean();

        final Handler response_handler = (name, priority, proposals) ->
        {
            synchronized (SNSPVProposalsDemo.class)
            {
                if (name.equals(SNSPVProposals.NAME))
                    got_SNS_response.set(true);
                System.out.println("Result from '" + name + "':");
                proposals.forEach(System.out::println);
            }
        };

        got_SNS_response.set(false);
        PVProposalService.INSTANCE.lookup("", response_handler);
        PVProposalService.INSTANCE.waitForCompletion();
        assertThat(got_SNS_response.get(), equalTo(false));

        got_SNS_response.set(false);
        PVProposalService.INSTANCE.lookup("DTL_LLRF", response_handler);
        PVProposalService.INSTANCE.waitForCompletion();
        assertThat(got_SNS_response.get(), equalTo(true));
    }
}

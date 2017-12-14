/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.completion;

import java.util.List;

import org.phoebus.framework.autocomplete.Proposal;
import org.phoebus.framework.spi.PVProposalProvider;

/** PV name completion for SNS
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVProposals implements PVProposalProvider
{
    public static final String NAME = "SNS PVs";

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public List<Proposal> lookup(final String text)
    {
        if (text.isEmpty())
            return List.of();

        return List.of(
            new Proposal("DTL_LLRF:IOC1:Load"),
            new Proposal("DTL_LLRF:IOC2:Load"),
            new Proposal("DTL_LLRF:IOC3:Load"),
            new Proposal("DTL_LLRF:IOC4:Load"),
            new Proposal("DTL_LLRF:IOC5:Load"),
            new Proposal("DTL_LLRF:IOC6:Load"),
            new Proposal("CCL_LLRF:IOC1:Load"),
            new Proposal("CCL_LLRF:IOC2:Load"),
            new Proposal("CCL_LLRF:IOC3:Load"),
            new Proposal("CCL_LLRF:IOC4:Load"),
            new Proposal("SCL_LLRF:IOC01a:Load")
                );
    }
}

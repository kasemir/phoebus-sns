/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.completion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.phoebus.framework.autocomplete.Proposal;
import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.Preference;
import org.phoebus.framework.rdb.RDBConnectionPool;
import org.phoebus.framework.spi.PVProposalProvider;

/** PV name completion for SNS
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVProposals implements PVProposalProvider
{
    public static final String NAME = "SNS PVs";

    /** URL, user, pass, URL, user, pass, ... */
    @Preference(name="sources") private static String[] infos;

    /** Limit the number of results to return */
    @Preference private static int limit;

    static
    {
    	AnnotatedPreferences.initialize(SNSPVProposals.class, "/pv_proposals_preferences.properties");
    }

    private final List<RDBConnectionPool> pools;

    public SNSPVProposals()
    {
        pools = new ArrayList<>(infos.length / 3);
        for (int i=0; i<infos.length; i+=3)
            try
            {
                final RDBConnectionPool pool = new RDBConnectionPool(infos[i], infos[i+1], infos[i+2]);
                pool.setTimeout(10);
                pools.add(pool);
            }
            catch (Exception ex)
            {
                Logger.getLogger(getClass().getPackageName())
                      .log(Level.WARNING, "Cannot create connection pool", ex);
            }
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public List<Proposal> lookup(final String text)
    {
        // Need some minimum character count to limit size of result
        if (text.length() < 3)
            return List.of();

        final List<Proposal> result = new ArrayList<>();
        for (RDBConnectionPool pool : pools)
            try
            {
                final Connection connection = pool.getConnection();
                try
                {
                    final PreparedStatement stmt = connection.prepareStatement("SELECT name FROM chan_arch.channel WHERE name LIKE ? FETCH FIRST " + limit + " ROWS ONLY");
                    // Allow user to enter '*' as wildcard.
                    // Pad with '%' to look for text within a name
                    stmt.setString(1, "%" + text.replace('*', '%') + "%");
                    final ResultSet rs = stmt.executeQuery();
                    while (rs.next()  &&  result.size() < limit)
                        result.add(new Proposal(rs.getString(1)));
                    rs.close();
                    stmt.close();
                }
                finally
                {
                    pool.releaseConnection(connection);
                }
            }
            catch (InterruptedException ex)
            {
                // System.out.println("Ignoring interruption...");
            }
            catch (Exception ex)
            {   // Ignore interruptions (new lookup replaced this one)
                if (! (Thread.currentThread().isInterrupted() ||
                       ex.getMessage().contains("interrupt")))
                    Logger.getLogger(getClass().getPackageName())
                          .log(Level.WARNING, "Cannot search for PV names", ex);
                // else System.out.println("Ignoring " + ex);
            }

        return result;
    }
}

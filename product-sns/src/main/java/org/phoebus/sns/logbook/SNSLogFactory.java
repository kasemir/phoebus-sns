/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.logbook;

import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.Preference;
import org.phoebus.logbook.LogClient;
import org.phoebus.logbook.LogFactory;
import org.phoebus.security.tokens.SimpleAuthenticationToken;

public class SNSLogFactory implements LogFactory
{
    @Preference private static String rdb_url;
    @Preference private static String read_only_username;
    @Preference private static String read_only_password;
    
    private static final String ID = "SNS";

    static
    {
    	AnnotatedPreferences.initialize(SNSLogFactory.class, "/log_preferences.properties");
    }
    
    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public LogClient getLogClient()
    {
        return new SNSLogClient(rdb_url, read_only_username, read_only_password);
    }

    @Override
    public LogClient getLogClient(Object authToken)
    {
        SimpleAuthenticationToken simpleAuth = (SimpleAuthenticationToken) authToken;
        return new SNSLogClient(rdb_url, simpleAuth.getUsername(), simpleAuth.getPassword());
    }
}

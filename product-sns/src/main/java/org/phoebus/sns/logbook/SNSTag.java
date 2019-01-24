/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.logbook;

import org.phoebus.logbook.Tag;

public class SNSTag implements Tag
{
    final private String name;

    public SNSTag(final String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getState()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "Tag '" + name + "'";
    }
}

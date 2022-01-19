/*******************************************************************************
 * Copyright (c) 2012-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.logbook.elog;

/** ELog categories
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogCategory
{
    final private String id;
    final private String logbook;
    final private String category;

    /** @param id Category ID like "ELEC"
     *  @param logbook Logbook name that uses the tag like "SNS BL-09-CORELLI"
     *  @param category Tag/category like "Electrical Systems"
     */
    public ELogCategory(final String id, final String logbook, final String category)
    {
        this.id = id;
        this.logbook = logbook;
        this.category = category;
    }

    public String getID()
    {
        return id;
    }

    public String getLogbook()
    {
        return logbook;
    }

    public String getCategory()
    {
        return category;
    }

    @Override
    public String toString()
    {
        return logbook + " : " + category;
    }
}

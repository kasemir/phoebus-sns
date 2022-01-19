/*******************************************************************************
 * Copyright (c) 2010-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.logbook.elog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Unit tests for ELog.getCategoryFromCategoryLogbookString()
 *
 * @author Evan Smith
 */
@SuppressWarnings("nls")
public class TestCategoryExtraction
{
    @Test
    public void CorrectInput() throws Exception
    {
        final String[] result = ELog.getCategoryFromLogbookCategoryString("  Test Logbook :   Test Category");
        assertEquals(2, result.length);
        assertEquals("Test Logbook", result[0]);
        assertEquals("Test Category", result[1]);
    }
}

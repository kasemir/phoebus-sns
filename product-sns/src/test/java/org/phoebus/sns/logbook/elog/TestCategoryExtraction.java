/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
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
public class TestCategoryExtraction
{
    String expected = "";
    String actual = "";
    String logbook_and_category = "";
    
    @Test 
    public void CorrectInput()
    {
        logbook_and_category = "Test Logbook : Test Category";
        expected = "Test Category";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        assertEquals(expected, actual);
    }
    
    @Test 
    public void CorrectInputWithExtraneousWhitespace()
    {
        logbook_and_category = "\r\r\n\t\nTest Logbook \t\t\n: \t\n\tTest Category\t\t\t\n\n";
        expected = "Test Category";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoCategory()
    {
        expected = "Category is not in logbook and category string.";
        
        logbook_and_category = "Test Logbook : ";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {            
            actual = ex.getMessage();
        }
        
        assertEquals(expected, actual);

    }
    
    @Test
    public void WhenThereIsNoCategoryWithExtraneousWhitespace()
    {
        expected = "Category is not in logbook and category string.";
        
        logbook_and_category = "\n\t\nTest Logbook \t\r\t\n: \t\n\t\t\r\t\t\n\n";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {            
            actual = ex.getMessage();
        }
        
        assertEquals(expected, actual);

    }
    
    @Test
    public void WhenThereIsNoLogbook()
    {
        expected = "Logbook is not in logbook and category string.";
        
        logbook_and_category = ": Test Category";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoLogbookWithExtraneousWhitespace()
    {
        expected = "Logbook is not in logbook and category string.";
        
        logbook_and_category = "\n\t\n\t\t\r\n: \t\r\n\tTest Category\t\t\r\t\n\n";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoLogbookOrDelimiter()
    {
        expected = "Logbook and Category string missing element or delimiter.";

        logbook_and_category = "Test Category";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoLogbookOrDelimiterWithExtraneousWhitespace()
    {
        expected = "Logbook and Category string missing element or delimiter.";

        logbook_and_category = "\n\t\n\t\t\r\n \t\r\n\tTest Category\t\t\r\t\n\n";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoDelimiter()
    {
        expected = "Logbook and Category string missing element or delimiter.";

        logbook_and_category = "Test Logbook Test Category";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
    
    @Test
    public void WhenThereIsNoDelimiterWithExtraneousWhitespace()
    {
        expected = "Logbook and Category string missing element or delimiter.";

        logbook_and_category = "Test Logbook\n\t\n\t\t\r\n \t\r\n\tTest Category\t\t\r\t\n\n";
        
        try
        {
            actual = ELog.getCategoryFromLogbookCategoryString(logbook_and_category);
        } 
        catch (Exception ex)
        {
            actual = ex.getMessage();
        }

        assertEquals(expected, actual);
    }
}

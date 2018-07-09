package org.phoebus.sns.logbook.ui;

import javafx.scene.control.Tab;


/**
 * Placeholder for properties tab.
 * @author 1es
 *
 */
public class LogPropertiesTab extends Tab
{
    public LogPropertiesTab()
    {
        formatTab();
    }
    
    private void formatTab()
    {
        setClosable(false);
        setText("Properties");
    }
}

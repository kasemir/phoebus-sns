/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

/**
 * Collapsible tab pane view that facilitates adding images and files as attachments to log book entries.
 * @author Evan Smith
 *
 */
public class LogEntryAttachmentsView extends Accordion
{
    private final TabPane       tabPane;
    @SuppressWarnings("unused")
    private final LogEntryModel model;
    private final LogImagesTab  images;
    private final Tab           files, properties;
    
    public LogEntryAttachmentsView(final LogEntryModel model)
    {
        super();
        this.model = model;
        tabPane    = new TabPane();
        images     = new LogImagesTab(model);
        files      = new LogFilesTab(model);
        properties = new LogPropertiesTab();
        
        tabPane.getTabs().addAll(images, files, properties);
        
        TitledPane tPane = new TitledPane("Attachments", tabPane);
        
        getPanes().add(tPane);
    }
}

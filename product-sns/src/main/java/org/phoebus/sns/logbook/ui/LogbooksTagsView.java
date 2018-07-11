/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import static org.phoebus.ui.application.PhoebusApplication.logger;

import java.util.logging.Level;

import org.phoebus.ui.javafx.ImageCache;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * View for selecting log books and tags for a log entry.
 * @author Evan Smith
 */
public class LogbooksTagsView extends VBox
{
    private static final Image tag_icon = ImageCache.getImage(LabelFieldSelectorView.class, "/icons/add_tag.png");
    private static final Image logbook_icon = ImageCache.getImage(LabelFieldSelectorView.class, "/icons/logbook-16.png");

    public LogbooksTagsView(LogEntryModel model)
    {
        LabelFieldSelectorView logbooks = new LabelFieldSelectorView("Logbooks",
                                                                      logbook_icon,
                                                                      model::getLogbooks, 
                                                                      model::getSelectedLogbooks, 
                                                                      model::hasSelectedLogbook, 
                                                                      // Function throws Exception on internal error so use lambda to catch.
                                                                      logbook ->
                                                                      {
                                                                          try
                                                                          {
                                                                              return model.addSelectedLogbook(logbook);
                                                                          } 
                                                                          catch (Exception ex)
                                                                          {
                                                                              logger.log(Level.SEVERE, "Internal log selection failed.", ex);
                                                                          }
                                                                          return false;
                                                                      }, 
                                                                      // Function throws Exception on internal error so use lambda to catch.
                                                                      logbook ->
                                                                      {
                                                                          try
                                                                          {
                                                                              return model.removeSelectedLogbook(logbook);
                                                                          } 
                                                                          catch (Exception ex)
                                                                          {
                                                                              logger.log(Level.SEVERE, "Internal log selection failed.", ex);
                                                                          }
                                                                          return false;
                                                                      });
        
        LabelFieldSelectorView tags = new LabelFieldSelectorView("Tags",
                                                                  tag_icon,
                                                                  model::getTags,
                                                                  model::getSelectedTags, 
                                                                  model::hasSelectedTag, 
                                                                  // Function throws Exception on internal error so use lambda to catch.
                                                                  tag ->
                                                                  {
                                                                      try
                                                                      {
                                                                          return model.addSelectedTag(tag);
                                                                      } 
                                                                      catch (Exception ex)
                                                                      {
                                                                          logger.log(Level.SEVERE, "Internal tag selection failed.", ex);
                                                                      }
                                                                      return false;
                                                                  }, 
                                                                  // Function throws Exception on internal error so use lambda to catch.
                                                                  tag ->
                                                                  {
                                                                      try
                                                                      {
                                                                          return model.removeSelectedTag(tag);
                                                                      } 
                                                                      catch (Exception ex)
                                                                      {
                                                                          logger.log(Level.SEVERE, "Internal tag selection failed.", ex);
                                                                      }
                                                                      return false;
                                                                  });
        
        setSpacing(10);
        
        logbooks.setAlignment(Pos.CENTER);
        tags.setAlignment(Pos.CENTER);
        getChildren().addAll(logbooks, tags);
    }
}
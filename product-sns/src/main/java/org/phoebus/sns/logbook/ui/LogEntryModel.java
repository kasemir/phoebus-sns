/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Purveyor of log entry application state.
 * <p> Provides methods to set log entry data and to submit log entries.
 * @author Evan Smith
 */
public class LogEntryModel
{
    private Node   node;
    private String username, password;
    private String date, level;
    private String title, text;
    
    // private final SNSLogClient client; // Source of log and tag data.
    private final ObservableList<String>    logbooks, tags, selectedLogbooks, selectedTags;
    private final ObservableList<Image> images;
    
    public LogEntryModel(final Node callingNode)
    { 
        // TODO : Implement SNSLogClient and use it to retrieve data. Remove dummy data.
        tags     = FXCollections.observableArrayList(List.of("Tag 1", "Tag 2", "Tag 3"));
        logbooks = FXCollections.observableArrayList(List.of("Logbook 1", "Logbook 2", "Logbook 3"));
        
        selectedLogbooks = FXCollections.observableArrayList();
        selectedTags     = FXCollections.observableArrayList();
        
        images = FXCollections.observableArrayList();
        
        node = callingNode;
    }

    public Node getNode()
    {
        return node;
    }
    
    public Scene getScene()
    {
        return node.getScene();
    }

    public void setUser(final String username)
    {
        this.username = username;
    }
    
    public void setPassword(final String password)
    {
        this.password = password;
    }
    
    public void setDate(final String date)
    {
        this.date = date;
    }
    
    public void setLevel(final String level)
    {
        this.level = level;
    }
    
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    public void setText(final String text)
    {
        this.text = text;
    }
    
    public ObservableList<String> getLogbooks()
    {
        return FXCollections.unmodifiableObservableList(logbooks);
    }
    
    public ObservableList<String> getSelectedLogbooks()
    {
        return FXCollections.unmodifiableObservableList(selectedLogbooks);
    }
    
    public boolean hasLogbook (final String logbook)
    {
        return logbooks.contains(logbook);
    }
    
    public boolean hasSelectedLogbook (final String logbook)
    {
        return selectedLogbooks.contains(logbook);
    }
    
    public boolean addSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.add(logbook);
        selectedLogbooks.sort(Comparator.naturalOrder());
        return result;
    }
    
    public boolean removeSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.remove(logbook);
        return result;    
    }
    
    public ObservableList<String> getTags()
    {
        return FXCollections.unmodifiableObservableList(tags);
    }
    
    public ObservableList<String> getSelectedTags()
    {
        return FXCollections.unmodifiableObservableList(selectedTags);
    }
    
    public boolean hasTag (final String tag) 
    {
        return tags.contains(tag);
    }
    
    public boolean hasSelectedTag (final String tag) 
    {
        return selectedTags.contains(tag);
    }
    
    public boolean addSelectedTag(final String tag)
    {
        boolean result = selectedTags.add(tag);
        selectedTags.sort(Comparator.naturalOrder());
        return result;    
    }
    
    public boolean removeSelectedTag(final String tag)
    {
        boolean result = selectedTags.remove(tag);
        return result;        
    }
    
    public ObservableList<Image> getImages()
    {
        return FXCollections.unmodifiableObservableList(images);
    }
    
    public boolean addImage(final Image img)
    {
        return images.add(img);
    }
    
    public boolean removeImage(final Image image)
    {
        return images.remove(image);
    }
    
    public void submitEntry()
    {
        // TODO : Submit entry though SNSClient
        System.out.println("You pressed submit.");
        System.out.println("user: " + username);
        System.out.println("password: " + password);
        System.out.println("date: " + date);
        System.out.println("level: " + level);
        System.out.println("title: " + title);
        System.out.println("logbooks: ");
        for (String logbook : selectedLogbooks)
            System.out.println("\t" + logbook);
        System.out.println("tags: ");
        for (String tag : selectedTags)
            System.out.println("\t" + tag);
        System.out.println("text: " + text);
    }
}

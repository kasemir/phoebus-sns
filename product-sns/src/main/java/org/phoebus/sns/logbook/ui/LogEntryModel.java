/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.sns.logbook.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import org.phoebus.logging.LogFactory;
import org.phoebus.logging.LogService;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
    
    private static final LogService logService = LogService.getInstance();
    
    // private final SNSLogClient client; // Source of log and tag data.
    private final ObservableList<String>    logbooks, tags, selectedLogbooks, selectedTags;
    private final ObservableList<Image> images;
    private final ObservableList<File>  files;
    
    public LogEntryModel(final Node callingNode)
    { 
        // TODO : Implement SNSLogClient and use it to retrieve data. Remove dummy data.
        tags     = FXCollections.observableArrayList(List.of("Tag 1", "Tag 2", "Tag 3"));
        logbooks = FXCollections.observableArrayList(List.of("Logbook 1", "Logbook 2", "Logbook 3"));
        selectedLogbooks = FXCollections.observableArrayList();
        selectedTags     = FXCollections.observableArrayList();

        images = FXCollections.observableArrayList();
        files  = FXCollections.observableArrayList();
        
        node = callingNode;
    }

    /**
     * Gets the JavaFX Scene graph.
     * @return Scene
     */
    public Scene getScene()
    {
        return node.getScene();
    }

    /**
     * Set the user name.
     * @param username
     */
    public void setUser(final String username)
    {
        this.username = username;
    }
    
    /**
     * Set the password.
     * @param password
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }
    
    /**
     * Set the date.
     * @param date
     */
    public void setDate(final String date)
    {
        this.date = date;
    }
    
    /**
     * Set the level.
     * @param level
     */
    public void setLevel(final String level)
    {
        this.level = level;
    }
    
    /**
     * Set the title.
     * @param title
     */
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    /**
     * Set the text.
     * @param text
     */
    public void setText(final String text)
    {
        this.text = text;
    }
    
    /**
     * Get an unmodifiable list of the log books.
     * @return
     */
    public ObservableList<String> getLogbooks()
    {
        return FXCollections.unmodifiableObservableList(logbooks);
    }
    
    /**
     * Get an unmodifiable list of the selected log books.
     * @return
     */
    public ObservableList<String> getSelectedLogbooks()
    {
        return FXCollections.unmodifiableObservableList(selectedLogbooks);
    }
    
    /**
     * Tests whether the model's log book list contains the passed log book name.
     * @param logbook
     * @return
     */
    public boolean hasLogbook (final String logbook)
    {
        return logbooks.contains(logbook);
    }
    
    /**
     * Tests whether the model's selected log book list contains the passed log book name.
     * @param logbook
     * @return
     */
    public boolean hasSelectedLogbook (final String logbook)
    {
        return selectedLogbooks.contains(logbook);
    }
    
    /**
     * Add a log book to the model's selected log books list.
     * @param logbook
     * @return
     */
    public boolean addSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.add(logbook);
        selectedLogbooks.sort(Comparator.naturalOrder());
        return result;
    }
    
    /**
     * Remove a log book from the model's selected log book list.
     * @param logbook
     * @return
     */
    public boolean removeSelectedLogbook(final String logbook)
    {
        boolean result = selectedLogbooks.remove(logbook);
        return result;    
    }
    
    /**
     * Get an unmodifiable list of the tags.
     * @return
     */
    public ObservableList<String> getTags()
    {
        return FXCollections.unmodifiableObservableList(tags);
    }
    
    /**
     * Get an unmodifiable list of the selected tags.
     * @return
     */
    public ObservableList<String> getSelectedTags()
    {
        return FXCollections.unmodifiableObservableList(selectedTags);
    }
    
    /**
     * Tests whether the model's tag list contains the passed tag name.
     * @param tag
     * @return
     */
    public boolean hasTag (final String tag) 
    {
        return tags.contains(tag);
    }
    
    /**
     * Tests whether the model's selected tag list contains the passed tag name.
     * @param tag
     * @return
     */
    public boolean hasSelectedTag (final String tag) 
    {
        return selectedTags.contains(tag);
    }
    
    /**
     * Adds the passed tag name to the model's selected tag list.
     * @param tag
     * @return
     */
    public boolean addSelectedTag(final String tag)
    {
        boolean result = selectedTags.add(tag);
        selectedTags.sort(Comparator.naturalOrder());
        return result;    
    }
    
    /**
     * Removes the passed tag name from the model's selected tag list.
     * @param tag
     * @return
     */
    public boolean removeSelectedTag(final String tag)
    {
        boolean result = selectedTags.remove(tag);
        return result;        
    }
    
    /**
     * Return an unmodifiable list of the model's images.
     * @return
     */
    public ObservableList<Image> getImages()
    {
        return FXCollections.unmodifiableObservableList(images);
    }
    
    /**
     * Add an image to the model's list of images.
     * @param image
     * @return
     */
    public boolean addImage(final Image image)
    {
        if (null != image)
            return images.add(image);
        return false;    
    }
    
    /**
     * Remove an image from the model's list of images.
     * @param image
     * @return
     */
    public boolean removeImage(final Image image)
    {
        if (null != image)
            return images.remove(image);
        return false;
    }
    
    /**
     * Add a listener to the images list.
     * @param listChangeListener
     */
    public void addImagesListener(ListChangeListener<Image> listChangeListener)
    {
        images.addListener(listChangeListener);
    }
    
    /**
     * Return an unmodifiable list of the model's files.
     * @return
     */
    public ObservableList<File> getFiles()
    {
        return FXCollections.unmodifiableObservableList(files);
    }
    
    /**
     * Add a file to the model's list of files.
     * @param file
     * @return
     */
    public boolean addFile(final File file)
    {
        return files.add(file);
    }
    
    /** 
     * Remove a file form the model's list of files.
     * @param file
     * @return
     */
    public boolean removeFile(final File file)
    {
        return files.remove(file);
    }
    
    /**
     * Create and submit a log entry with the current data in the log entry form.
     */
    public void submitEntry()
    {
        // TODO How to set site specific log factory ID? Get from preferences loader?
        LogFactory logFactory = logService.getLogFactories().get("org.phoebus.sns.logbook");
        if (logFactory != null)
        {
            System.out.println("Factory successfully retrieved: " + logFactory.getId());
        }
        /*
        LogEntryBuilder logEntryBuilder = new LogEntryBuilder();
        
        for (File file : files)
            logEntryBuilder.attach(AttachmentImpl.of(file));
        // for (Image image : images) 
        // {
        //     logEntryBuilder.attach(AttachmentImpl.of(imageToStream(image));
        // }
        */
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
    
    @SuppressWarnings("unused")
    private ByteArrayInputStream imageToStream(final Image image) throws IOException
    {
        BufferedImage bufImg = SwingFXUtils.fromFXImage(image, null);
        try
        (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
        )
        {   
            ImageIO.write(bufImg, "png", out);
        
            byte[] data = out.toByteArray();
            out.close();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            return in;
        }
    }
}

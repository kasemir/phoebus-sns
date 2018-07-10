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
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Helper class for converting JavaFX images to streams.
 * @author Evan Smith
 */
public class ImageStreamHelper
{
    /** Convert JavaFX Image to ByteArrayInputStream */
    public static ByteArrayInputStream imageToStream(final Image image) throws IOException
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

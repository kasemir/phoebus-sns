/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.ui.dialog.DialogHelper;
import org.phoebus.ui.javafx.ImageCache;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/** Dialog with info on selected bypasses
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShowInfo extends MenuItem
{
    public ShowInfo(final TableView<BypassRow> bypasses)
    {
        super("Bypass Info", ImageCache.getImageView(ImageCache.class, "/icons/info.png"));
        setOnAction(event ->
        {
            final StringBuilder buf = new StringBuilder();

            for (BypassRow row : bypasses.getSelectionModel().getSelectedItems())
            {
                final Bypass bypass = row.bypass;
                if (buf.length() > 0)
                    buf.append("\n");
                buf.append(bypass.getName()).append("\n");
                buf.append("-----------------------------------\n");
                buf.append("Jumper PV: ").append(bypass.getJumperPVName()).append("\n");
                buf.append("Mask PV: ").append(bypass.getMaskPVName()).append("\n");
            }

            final TextArea text = new TextArea(buf.toString());
            text.setEditable(false);

            final Alert dialog = new Alert(AlertType.INFORMATION);
            dialog.setTitle("MPS Bypass Info");
            dialog.setHeaderText("Detail for selected Bypasses");
            dialog.getDialogPane().setContent(text);
            dialog.setResizable(true);
            DialogHelper.positionDialog(dialog, bypasses, -400, -300);
            dialog.show();
        });
    }
}

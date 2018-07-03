package org.phoebus.sns.logbook.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author Evan Smith
 */
public class LogImagesTab extends Tab
{
    private class ImageCell extends ListCell<WritableImage>
    {
        private ImageView imageView = new ImageView();
         
        public ImageCell()
        {
            super();
            this.setAlignment(Pos.CENTER);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            
            this.setOnMouseClicked(click ->
            {
                if (click.getClickCount() == 2)
                {
                    image.setImage(imageView.getImage());
                }
            });
        }
        
        @Override
        public void updateItem(WritableImage image, boolean empty)
        {
            super.updateItem(image, empty);
            if (empty)
                setGraphic(null);
            else
            {
                imageView.setImage(image);
                setGraphic(imageView);
            }
        }
    }
    
    private final LogEntryModel model;
    
    private final VBox      content, removeBox;
    private final HBox      imageBox, listBox;
    private final ImageView image;
    private final ListView<WritableImage>  imageList;
    private final HBox      buttonBox;
    private final Button    addImage, addScreenshot, cssWindow, clipboard, removeImage;
    
    public LogImagesTab(final LogEntryModel model)
    {
        this.model = model;
        
        content       = new VBox();
        imageBox      = new HBox();
        listBox       = new HBox();
        image         = new ImageView();
        imageList     = new ListView<WritableImage>(model.getImages());
        buttonBox     = new HBox();
        addImage      = new Button("Add Image");
        addScreenshot = new Button("Add Screenshot");
        cssWindow     = new Button("CSS Window");
        clipboard     = new Button("Clipboard");
        removeBox     = new VBox();
        removeImage   = new Button("Remove");
        
        formatTab();
    }

    private void formatTab()
    {
        setText("Images");
        setClosable(false);
        
        VBox.setVgrow(imageBox, Priority.ALWAYS);
        imageBox.setPrefSize(1000, 300);
        image.fitHeightProperty().bind(imageBox.heightProperty());
        image.setPreserveRatio(true);
        imageList.setCellFactory(param -> new ImageCell());
        imageList.setMinWidth(250);
        listBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(imageBox, Priority.ALWAYS);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().addAll(image);
        removeBox.setMinWidth(100);
        removeBox.getChildren().add(removeImage);
        listBox.getChildren().addAll(imageBox, removeBox, imageList);
        
        buttonBox.setSpacing(10);
        HBox.setMargin(addImage,  new Insets(0,  0, 0, 10));
        HBox.setMargin(clipboard, new Insets(0, 10, 0,  0));
        buttonBox.setAlignment(Pos.CENTER);
        
        // Give each button 1/4 of the room.
        addImage.prefWidthProperty().bind(buttonBox.widthProperty().divide(4));
        addScreenshot.prefWidthProperty().bind(buttonBox.widthProperty().divide(4));
        cssWindow.prefWidthProperty().bind(buttonBox.widthProperty().divide(4));
        clipboard.prefWidthProperty().bind(buttonBox.widthProperty().divide(4));
        
        buttonBox.getChildren().addAll(addImage, addScreenshot, cssWindow, clipboard);
        
        content.getChildren().addAll(listBox, buttonBox);
        
        this.setContent(content);
        
        addScreenshot.setOnAction(event -> 
        {
            model.addImage(takeScreenshot());
        });
        
        removeImage.setOnAction(event ->
        {
            int index = model.getImages().indexOf((WritableImage) image.getImage());
            
            model.removeImage((WritableImage) image.getImage());
            if (index >= 1)
                image.setImage(model.getImages().get(index - 1));
            else
                image.setImage(null);
        });
    }
    
    private WritableImage takeScreenshot()
    {
        Scene scene = model.getScene();
        WritableImage i = scene.snapshot(null);
        return i;
    }
}

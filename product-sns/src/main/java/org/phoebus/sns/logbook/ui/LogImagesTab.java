package org.phoebus.sns.logbook.ui;

import java.io.File;
import java.util.List;

import org.phoebus.ui.javafx.ImageCache;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Evan Smith
 */
public class LogImagesTab extends Tab
{
    private class ImageCell extends ListCell<Image>
    {
        private ImageView cellImageView = new ImageView();
         
        public ImageCell(final ImageView imageView)
        {
            super();
            setAlignment(Pos.CENTER);
            cellImageView.setFitHeight(100);
            cellImageView.setPreserveRatio(true);

            setOnMouseClicked(click ->
            {
                imageView.setImage(cellImageView.getImage());
            });
        }
        
        @Override
        public void updateItem(Image image, boolean empty)
        {
            super.updateItem(image, empty);
            if (empty)
                setGraphic(null);
            else
            {
                cellImageView.setImage(image);
                setGraphic(cellImageView);
            }
        }
    }
    
    private static final ImageView removeIcon = ImageCache.getImageView(ImageCache.class, "/icons/delete.png");
    
    private final LogEntryModel model;
    
    private final VBox        content, removeBox, listBox;
    private final HBox        imageBox, imageViewBox;
    private final ImageView   imageView;
    private final HBox        buttonBox;
    private final Button      addImage, addScreenshot, cssWindow, clipboard, removeImage;
    private final FileChooser addImageDialog;
    private final ListView<Image>  imageList;

    public LogImagesTab(final LogEntryModel model)
    {
        super();
        this.model = model;
        
        content       = new VBox();
        imageBox      = new HBox();
        imageViewBox  = new HBox();
        imageView     = new ImageView();
        removeBox     = new VBox();
        removeImage   = new Button("Remove", removeIcon);
        listBox       = new VBox();
        imageList     = new ListView<Image>(model.getImages());        
        buttonBox     = new HBox();
        addImage      = new Button("Add Image");
        addScreenshot = new Button("Add Screenshot");
        cssWindow     = new Button("CSS Window");
        clipboard     = new Button("Clipboard");
        
        addImageDialog = new FileChooser();
        
        formatTab();
    }

    private void formatTab()
    {
        setText("Images");
        setClosable(false);
        
        addImageDialog.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.ppm" , "*.pgm"));

        formatContent();
        setOnActions(); 
    }
    
    /** Format the tab content */
    private void formatContent()
    {
        formatImageBox();
        formatButtonBox();
        
        content.setSpacing(10);
        VBox.setMargin(imageBox, new Insets(10, 0, 0, 0));
        content.getChildren().addAll(imageBox, buttonBox);
        
        this.setContent(content);
    }

    private void formatImageBox()
    {
        imageBox.setPrefSize(1000, 200);
        
        imageBox.setSpacing(10);
        imageBox.setAlignment(Pos.CENTER_RIGHT);
        imageBox.getChildren().addAll(imageViewBox, removeBox, listBox);
        
        VBox.setMargin(imageBox, new Insets(0, 0, 0, 10));
        
        formatImageViewBox();
        formatRemoveBox();
        formatListBox();
    }
    
    private void formatImageViewBox()
    {
        imageView.fitHeightProperty().bind(imageBox.heightProperty());
        imageView.setPreserveRatio(true);
        imageViewBox.setAlignment(Pos.CENTER);
        imageViewBox.getChildren().add(imageView);
        
        HBox.setHgrow(imageViewBox, Priority.ALWAYS);
    }
    
    private void formatRemoveBox()
    {
        removeBox.setMinWidth(110);
        removeImage.setPrefSize(100, 30);
        VBox.setMargin(removeImage, new Insets(30, 0, 0, 0));
        removeBox.getChildren().add(removeImage);
    }
    
    private void formatListBox()
    {
        imageList.setStyle("-fx-control-inner-background-alt: #f4f4f4");
        imageList.setStyle("-fx-control-inner-background: #f4f4f4");

        imageList.setCellFactory(param -> new ImageCell(imageView));
        imageList.setMinWidth(150);
        listBox.setSpacing(5);
        listBox.setMaxWidth(150);
        HBox.setMargin(listBox, new Insets(0, 10, 0, 0));
        listBox.setAlignment(Pos.CENTER_LEFT);
        listBox.getChildren().addAll(new Label("Images: "), imageList);
    }
    
    private void formatButtonBox()
    {
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
    }
    
    private void setOnActions()
    {
        addImage.setOnAction(event ->
        {
            List<File> imageFiles = addImageDialog.showOpenMultipleDialog(this.getTabPane().getScene().getWindow());
            if (null != imageFiles)
            {
                for (File imageFile : imageFiles)
                {
                    Image image = new Image(imageFile.toURI().toString());
                    model.addImage(image);
                }
            }
        });
        
        addScreenshot.setOnAction(event -> 
        {
            model.addImage(captureScene());
        });
        
        removeImage.setOnAction(event ->
        {
            Image image = imageView.getImage();
            if (image != null)
            {
                model.removeImage(imageView.getImage());
                image = imageList.getSelectionModel().getSelectedItem();
                imageView.setImage(image);
            }
        });
        
        cssWindow.setOnAction(event ->
        {
            model.addImage(captureNode());
        });
        
        // TODO Implement clip board button
    }
    
    /**
     * Capture an image of the calling JavaFX node.
     * @return Image
     */
    private WritableImage captureNode()
    {
        Node node = model.getNode();
        WritableImage image = node.snapshot(null, null);
        return image;
    }
    
    /**
     * Capture an image of the scene that the calling JavaFX node belongs to.
     * @return Image
     */
    private WritableImage captureScene()
    {
        Scene scene = model.getScene();
        WritableImage image = scene.getRoot().snapshot(null, null);
        return image;
    }
}

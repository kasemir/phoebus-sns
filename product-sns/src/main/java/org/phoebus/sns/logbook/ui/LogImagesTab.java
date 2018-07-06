package org.phoebus.sns.logbook.ui;

import java.io.File;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
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
        private ImageView imageView = new ImageView();
         
        public ImageCell()
        {
            super();
            this.setAlignment(Pos.CENTER);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            
            this.setOnMouseClicked(click ->
            {
                imageView.setImage(imageView.getImage());
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
                imageView.setImage(image);
                setGraphic(imageView);
            }
        }
    }
    
    private final LogEntryModel model;
    
    private final VBox        content, removeBox;
    private final HBox        imageBox, listBox;
    private final ImageView   imageView;
    private final HBox        buttonBox;
    private final Button      addImage, addScreenshot, cssWindow, clipboard, removeImage;
    private final FileChooser addImageDialog;
    private final ListView<Image>  imageList;

    public LogImagesTab(final LogEntryModel model)
    {
        this.model = model;
        
        content        = new VBox();
        imageBox       = new HBox();
        listBox        = new HBox();
        imageView      = new ImageView();
        imageList      = new ListView<Image>(model.getImages());
        buttonBox      = new HBox();
        addImage       = new Button("Add Image");
        addScreenshot  = new Button("Add Screenshot");
        cssWindow      = new Button("CSS Window");
        clipboard      = new Button("Clipboard");
        removeBox      = new VBox();
        removeImage    = new Button("Remove");
        addImageDialog = new FileChooser();
        
        formatTab();
    }

    private void formatTab()
    {
        setText("Images");
        setClosable(false);
        
        addImageDialog.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.ppm" , "*.pgm"));
        VBox.setVgrow(imageBox, Priority.ALWAYS);
        imageBox.setPrefSize(1000, 300);
        imageView.fitHeightProperty().bind(imageBox.heightProperty());
        imageView.setPreserveRatio(true);
        imageList.setCellFactory(param -> new ImageCell());
        imageList.setMinWidth(250);
        listBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(imageBox, Priority.ALWAYS);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().addAll(imageView);
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
        
        addImage.setOnAction(event ->
        {
            List<File> imageFiles = addImageDialog.showOpenMultipleDialog(this.getTabPane().getScene().getWindow());
            if (null != imageFiles)
            {
                for (File imageFile : imageFiles)
                {
                    Image img = new Image(imageFile.toURI().toString());
                    model.addImage(img);
                }
            }
        });
        
        addScreenshot.setOnAction(event -> 
        {
            model.addImage(captureScene());
        });
        
        removeImage.setOnAction(event ->
        {
            Image img = imageView.getImage();
            if (img != null)
            {
                model.removeImage(imageView.getImage());
                img = imageList.getSelectionModel().getSelectedItem();
                imageView.setImage(img);
            }
        });
        
        cssWindow.setOnAction(event ->
        {
            model.addImage(captureNode());
        });
    }
    
    /**
     * Capture an image of the calling JavaFX node.
     * @return Image
     */
    private WritableImage captureNode()
    {
        Node node = model.getNode();
        WritableImage img = node.snapshot(new SnapshotParameters(), null);
        return img;
    }
    
    /**
     * Capture an image of the scene the calling JavaFX node belongs to.
     * @return Image
     */
    private WritableImage captureScene()
    {
        Scene scene = model.getScene();
        WritableImage img = scene.snapshot(null);
        return img;
    }
    
}

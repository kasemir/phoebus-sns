package org.phoebus.sns.logbook.ui;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class LogbookEntryViewDemo extends Application
{
    private class MyButton extends Button
    {
        public MyButton(final String text)
        {
            super(text);  
            initContextMenu();
        }
        
        private void initContextMenu()
        {
            ContextMenu cm = new ContextMenu();
            MenuItem mi = new MenuItem("Create Log Entry");
            mi.setOnAction(value -> 
            {
                Node node = (Node) this;
                new LogbookEntryView(node);
            });
            cm.getItems().add(mi);
            setContextMenu(cm);
        }
    }
    private class DemoView extends GridPane
    {
        public DemoView()
        {
            super();
            MyButton b1 = new MyButton("Button 1");
            MyButton b2 = new MyButton("Button 2");
            MyButton b3 = new MyButton("Button 3");
            MyButton b4 = new MyButton("Button 4");
            
            b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            b4.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            setConstraints(b1, 0, 0);
            setConstraints(b2, 1, 0);
            setConstraints(b3, 0, 1);
            setConstraints(b4, 1, 1);
            
            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.ALWAYS);
            c1.setFillWidth(true);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setHgrow(Priority.ALWAYS);
            c2.setFillWidth(true);
            
            getColumnConstraints().addAll(c1, c2);
            
            RowConstraints r1 = new RowConstraints();
            r1.setVgrow(Priority.ALWAYS); 
            r1.setFillHeight(true);
            RowConstraints r2 = new RowConstraints();
            r2.setVgrow(Priority.ALWAYS);
            r2.setFillHeight(true);
            
            getRowConstraints().addAll(r1, r2);
            
            setHgap(10);
            setVgap(10);

            getChildren().addAll(b1, b2, b3, b4);
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception
    {
        final Scene scene = new Scene(new DemoView(), 1000, 1000);
        
        stage.setTitle("Logbook Entry Demo");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
}

package org.phoebus.sns.logbook.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LogbookEntryViewDemo extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        LogbookEntryView view = new LogbookEntryView();
        
        final Scene scene = new Scene(view, 600, 800);
        stage.setTitle("Logbook Entry Demo");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args)
    {
        launch(args);
    }
}

package org.phoebus.applications.eliza;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ElizaUIDemo extends Application
{
    @Override
    public void start(final Stage stage) throws Exception
    {
        final ElizaUI eliza = new ElizaUI();
        stage.setScene(new Scene(eliza, 600, 800));
        stage.setTitle(ElizaApp.DISPLAY_NAME);
        stage.show();
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}

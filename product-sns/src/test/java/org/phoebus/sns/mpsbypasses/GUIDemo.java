package org.phoebus.sns.mpsbypasses;

import org.phoebus.pv.PVPool;
import org.phoebus.sns.mpsbypasses.model.BypassModel;
import org.phoebus.sns.mpsbypasses.model.BypassState;
import org.phoebus.sns.mpsbypasses.model.RequestState;
import org.phoebus.sns.mpsbypasses.ui.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("nls")
public class GUIDemo extends Application
{
    @Override
    public void start(final Stage stage) throws Exception
    {
        final BypassModel model = new BypassModel();
        model.setFilter(BypassState.All, RequestState.All);

        final GUI gui = new GUI(model);

        final Scene scene = new Scene(gui, 1100, 600);
        stage.setTitle("MPS Bypasses");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event ->
        {
            gui.dispose();

            System.out.println(PVPool.getPVReferences());

            System.exit(0);
        });
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}

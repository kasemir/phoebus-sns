package org.phoebus.sns.mpsbypasses.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.phoebus.framework.jobs.JobManager;
import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.sns.mpsbypasses.model.BypassModel;
import org.phoebus.sns.mpsbypasses.model.BypassModelListener;
import org.phoebus.sns.mpsbypasses.model.BypassState;
import org.phoebus.sns.mpsbypasses.model.RequestState;
import org.phoebus.sns.mpsbypasses.modes.BeamMode;
import org.phoebus.sns.mpsbypasses.modes.BeamModeMonitor;
import org.phoebus.sns.mpsbypasses.modes.MachineMode;
import org.phoebus.sns.mpsbypasses.modes.MachineModeMonitor;
import org.phoebus.ui.dialog.ExceptionDetailsErrorDialog;
import org.phoebus.ui.javafx.UpdateThrottle;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

@SuppressWarnings("nls")
public class GUI extends VBox implements BypassModelListener
{
    private final BypassModel model;

    private final ComboBox<MachineMode> sel_mode = new ComboBox<>();
    private final ComboBox<BypassState> sel_state = new ComboBox<>();
    private final ComboBox<RequestState> sel_req = new ComboBox<>();
    private final Button reload = new Button("Reload");

    private final TextField cnt_total = new TextField();
    private final TextField cnt_bypassed = new TextField();
    private final TextField cnt_bypassable = new TextField();
    private final TextField cnt_not_bypassable = new TextField();
    private final TextField cnt_disconnected = new TextField();
    private final TextField cnt_error = new TextField();

    private final TextField beam_rtdl = new TextField();
    private final TextField beam_switch = new TextField();
    private final TextField machine_rtdl = new TextField();
    private final TextField machine_switch = new TextField();

    private final TableView<BypassRow> bypasses = new TableView<>();

    private final BeamModeMonitor beam_monitor;
    private final MachineModeMonitor machine_monitor;

    private final UpdateThrottle full_table_update = new UpdateThrottle(500, TimeUnit.MILLISECONDS, this::updateAllTableRows);


    public GUI(final BypassModel model) throws Exception
    {
        this.model = model;

        setSpacing(5);
        createTable();
        HBox.setHgrow(bypasses, Priority.ALWAYS);
        bypasses.setMaxHeight(Double.MAX_VALUE);
        getChildren().addAll(createSelector(), createCounts(), createOpState(), bypasses);

        beam_monitor = new BeamModeMonitor(this::updateBeamMode);
        beam_monitor.start();
        machine_monitor = new MachineModeMonitor(this::updateMachineMode);
        machine_monitor.start();

        model.addListener(this);
    }

    private Node createSelector()
    {
        sel_mode.getItems().addAll(MachineMode.values());
        sel_state.getItems().addAll(BypassState.values());
        sel_req.getItems().addAll(RequestState.values());

        final HBox row = new HBox(5,
                new Label("Machine Mode:"), sel_mode,
                new Label("State:"),        sel_state,
                new Label("Requested:"),    sel_req,
                reload
                );
        for (Node n : row.getChildren())
            if (n instanceof Label)
                ((Label)n).setMaxHeight(Double.MAX_VALUE);

        sel_mode.setOnAction(event -> reload());
        reload.setOnAction(event -> reload());

        return row;
    }

    private Node createCounts()
    {
        final HBox row = new HBox(5,
                new Label("Total:"),  cnt_total,
                new Label("Bypassed:"), cnt_bypassed,
                new Label("Bypassable:"), cnt_bypassable,
                new Label("Not Bypassable:"), cnt_not_bypassable,
                new Label("Disconnected:"), cnt_disconnected,
                new Label("Error:"), cnt_error);
        for (Node n : row.getChildren())
            if (n instanceof Label)
                ((Label)n).setMaxHeight(Double.MAX_VALUE);
            else
            {
                ((TextField)n).setEditable(false);
                ((TextField)n).setPrefWidth(80);
            }
        return row;
    }


    private Node createOpState()
    {
        final GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        grid.add(new Label("RTDL"), 1, 0);
        grid.add(new Label("Switch"), 2, 0);

        beam_rtdl.setEditable(false);
        beam_switch.setEditable(false);
        grid.add(new Label("Beam Mode"), 0, 1);
        grid.add(beam_rtdl, 1, 1);
        grid.add(beam_switch, 2, 1);

        machine_rtdl.setEditable(false);
        machine_switch.setEditable(false);
        grid.add(new Label("Machine Mode"), 0, 2);
        grid.add(machine_rtdl, 1, 2);
        grid.add(machine_switch, 2, 2);

        return grid;
    }

    private Node createTable()
    {
        bypasses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BypassRow, String> col = new TableColumn<>("Bypass");
        col.setCellValueFactory(cell -> cell.getValue().name);
        bypasses.getColumns().add(col);

        col = new TableColumn<>("State");
        col.setCellValueFactory(cell -> cell.getValue().state);
        bypasses.getColumns().add(col);

        col = new TableColumn<>("Requestor");
        col.setCellValueFactory(cell -> cell.getValue().requestor);
        bypasses.getColumns().add(col);

        col = new TableColumn<>("Request Date");
        col.setCellValueFactory(cell -> cell.getValue().date);
        bypasses.getColumns().add(col);

        return bypasses;
    }


    private void updateBeamMode(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
        Platform.runLater(() ->
        {
            beam_rtdl.setText(handleNull(new_rtdl_mode));
            beam_switch.setText(handleNull(new_switch_mode));
        });
    }

    private void updateMachineMode(final MachineMode new_rtdl_mode, final MachineMode new_switch_mode)
    {
        Platform.runLater(() ->
        {
            machine_rtdl.setText(handleNull(new_rtdl_mode));
            machine_switch.setText(handleNull(new_switch_mode));
        });
    }

    private String handleNull(final Object obj)
    {
        if (obj == null)
            return "?";
        return obj.toString();
    }

    /** Display the model's counts
     *  (except for 'total' which doesn't change)
     */
    protected void displayCounts()
    {
        displayCount(cnt_bypassed, model.getBypassed());
        displayCount(cnt_bypassable, model.getBypassable());
        displayCount(cnt_not_bypassable, model.getNotBypassable());
        displayCount(cnt_disconnected, model.getDisconnected());
        displayCount(cnt_error, model.getInError());
    }

    /** @param txt {@link Text} field
     *  @param count Number to display in field
     */
    private static void displayCount(final TextField txt, int count)
    {
        txt.setText(Integer.toString(count));
    }

    private void reload()
    {
        System.out.println("Selected " + sel_mode.getValue());
        JobManager.schedule("MPS Bypasses", monitor ->
        {
            model.stop();
            model.selectMachineMode(monitor, sel_mode.getValue());
        });
    }

    @Override
    public void modelLoaded(final BypassModel model, final Exception error)
    {
        if (error != null)
        {
            ExceptionDetailsErrorDialog.openError(this, "Error", "Cannot load MPS Bypasses", error);
            return;
        }

        System.out.println("Model loaded, got " + model.getBypasses().length + " bypasses");
        full_table_update.trigger();

        Platform.runLater(() ->
        {
            displayCount(cnt_total, model.getTotal());
            displayCounts();
        });

        // Start model updates
        try
        {
            model.start();
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(this, "Error", "Cannot start MPS Bypass updates", ex);
        }
    }

    @Override
    public void bypassesChanged()
    {
        System.out.println("Bypasses changed");
        full_table_update.trigger();
    }

    @Override
    public void bypassChanged(final Bypass bypass)
    {
        // TODO Update only affected BypassRow
        // System.out.println(bypass);
        full_table_update.trigger();
    }

    private void updateAllTableRows()
    {
        final List<BypassRow> rows = new ArrayList<>();
        for (Bypass bypass : model.getBypasses())
            rows.add(new BypassRow(bypass));

        System.out.println("Got " + rows.size() + " bypasses");

        Platform.runLater(() ->
        {
            bypasses.getItems().setAll(rows);
        });
    }

    public void dispose()
    {
        full_table_update.dispose();
        beam_monitor.stop();
        machine_monitor.stop();
        model.stop();
    }
}

package org.phoebus.sns.mpsbypasses.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.phoebus.core.types.ProcessVariable;
import org.phoebus.framework.jobs.JobManager;
import org.phoebus.framework.selection.SelectionService;
import org.phoebus.sns.mpsbypasses.MPSBypasses;
import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.sns.mpsbypasses.model.BypassModel;
import org.phoebus.sns.mpsbypasses.model.BypassModelListener;
import org.phoebus.sns.mpsbypasses.model.BypassState;
import org.phoebus.sns.mpsbypasses.model.RequestState;
import org.phoebus.sns.mpsbypasses.modes.BeamMode;
import org.phoebus.sns.mpsbypasses.modes.BeamModeMonitor;
import org.phoebus.sns.mpsbypasses.modes.MachineMode;
import org.phoebus.sns.mpsbypasses.modes.MachineModeMonitor;
import org.phoebus.ui.application.ContextMenuHelper;
import org.phoebus.ui.dialog.ExceptionDetailsErrorDialog;
import org.phoebus.ui.javafx.UpdateThrottle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/** GUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUI extends GridPane implements BypassModelListener
{
    private static final Border BORDER = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT, new Insets(5)));
    private static final Insets BORDER_INSETS = new Insets(10);
    private static final Font BOLD = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize());

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
    private final ConcurrentHashMap<Bypass, BypassRow> model2gui = new ConcurrentHashMap<>();

    private final BeamModeMonitor beam_monitor;
    private final MachineModeMonitor machine_monitor;

    private final UpdateThrottle full_table_update = new UpdateThrottle(500, TimeUnit.MILLISECONDS, this::updateAllTableRows);


    public GUI(final BypassModel model) throws Exception
    {
        this.model = model;

        add(createSelector(), 0, 0);
        add(createCounts(), 0, 1);
        add(new HBox(createOpState(), createLegend()), 0, 2);
        add(createTable(), 0, 3);

        createContextMenu();

        beam_monitor = new BeamModeMonitor(this::updateBeamMode);
        beam_monitor.start();
        machine_monitor = new MachineModeMonitor(this::updateMachineMode);
        machine_monitor.start();

        model.addListener(this);
    }

    private Node createSelector()
    {
        sel_mode.getItems().addAll(MachineMode.values());
        sel_mode.setValue(MachineMode.Site);
        sel_state.getItems().addAll(BypassState.values());
        sel_state.setValue(BypassState.All);
        sel_req.getItems().addAll(RequestState.values());
        sel_req.setValue(RequestState.All);

        reload.setTooltip(new Tooltip("Re-load bypass information from Relational Database"));

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

        row.setPadding(new Insets(5, 0, 0, 15));

        final EventHandler<ActionEvent> filter_handler = event ->
        {
            model.setFilter(sel_state.getValue(), sel_req.getValue());
        };
        sel_state.setOnAction(filter_handler);
        sel_req.setOnAction(filter_handler);
        // Initial setting
        filter_handler.handle(null);
        reload();

        return row;
    }

    private Node createCounts()
    {
        final Label l = new Label("Counts");
        l.setFont(BOLD);

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

        final VBox box = new VBox(5, l, row);
        box.setBorder(BORDER);
        box.setPadding(BORDER_INSETS);

        return box;
    }

    private Node createOpState()
    {
        final GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        final Label l = new Label("Operating State");
        l.setFont(BOLD);
        grid.add(l, 0, 0);

        grid.add(new Label("RTDL"), 1, 1);
        grid.add(new Label("Switch"), 2, 1);

        beam_rtdl.setEditable(false);
        beam_switch.setEditable(false);
        grid.add(new Label("Beam Mode"), 0, 2);
        grid.add(beam_rtdl, 1, 2);
        grid.add(beam_switch, 2, 2);

        machine_rtdl.setEditable(false);
        machine_switch.setEditable(false);
        grid.add(new Label("Machine Mode"), 0, 3);
        grid.add(machine_rtdl, 1, 3);
        grid.add(machine_switch, 2, 3);

        grid.setBorder(BORDER);
        grid.setPadding(BORDER_INSETS);

        return grid;
    }

    private Node createLegend()
    {
        final GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        Label l = new Label("Legend");
        l.setFont(BOLD);
        grid.add(l, 0, 0);

        int col = 0;
        for (BypassState state : BypassState.values())
        {
            if (state == BypassState.All)
                continue;

            l = new Label(state.name());
            grid.add(l, col, 1);

            l = new Label("Requested");
            l.setBackground(BypassColors.getBypassColor(state, true));
            grid.add(l, col, 2);

            l = new Label("Not Requested");
            l.setBackground(BypassColors.getBypassColor(state, false));
            grid.add(l, col, 3);

            ++col;
        }

        grid.setBorder(BORDER);
        grid.setPadding(BORDER_INSETS);

        grid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(grid, Priority.ALWAYS);

        return grid;
    }

    private Node createTable()
    {
        bypasses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bypasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<BypassRow, String> col = new TableColumn<>("Bypass");
        col.setCellValueFactory(cell -> cell.getValue().name);
        bypasses.getColumns().add(col);

        col = new TableColumn<>("State");
        col.setCellValueFactory(cell -> cell.getValue().state);
        col.setCellFactory(c -> new StateCell());
        bypasses.getColumns().add(col);

        col = new TableColumn<>("Requestor");
        col.setCellValueFactory(cell -> cell.getValue().requestor);
        bypasses.getColumns().add(col);

        col = new TableColumn<>("Request Date");
        col.setCellValueFactory(cell -> cell.getValue().date);
        bypasses.getColumns().add(col);

        bypasses.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(bypasses, Priority.ALWAYS);
        GridPane.setVgrow(bypasses, Priority.ALWAYS);

        return bypasses;
    }

    private void createContextMenu()
    {
        final MenuItem info = new ShowInfo(bypasses);
        final MenuItem enter_request = new OpenWeb("Enter Bypass Request", MPSBypasses.url_enter_bypass);
        final MenuItem display_request = new OpenWeb("Bypass Display", MPSBypasses.url_view_bypass);

        final ContextMenu menu = new ContextMenu(info, enter_request, display_request);
        bypasses.setOnContextMenuRequested(event ->
        {
            menu.getItems().setAll(info, enter_request, display_request);
            // Publish PVs of selected rows
            final List<ProcessVariable> pvs = new ArrayList<>();
            for (BypassRow row : bypasses.getSelectionModel().getSelectedItems())
            {
                pvs.add(new ProcessVariable(row.bypass.getJumperPVName()));
                pvs.add(new ProcessVariable(row.bypass.getMaskPVName()));
            }
            SelectionService.getInstance().setSelection(bypasses, pvs);

            // Add PV entries
            if (ContextMenuHelper.addSupportedEntries(bypasses, menu))
                menu.getItems().add(3, new SeparatorMenuItem());


            menu.show(bypasses.getScene().getWindow(), event.getScreenX(), event.getScreenY());
        });
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
        // Update affected row
        final BypassRow row = model2gui.get(bypass);
        if (row != null)
            Platform.runLater(() ->
            {
                row.update();
                displayCounts();
            });
    }

    private void updateAllTableRows()
    {
        model2gui.clear();
        final List<BypassRow> rows = new ArrayList<>();
        for (Bypass bypass : model.getBypasses())
        {
            final BypassRow row = new BypassRow(bypass);
            model2gui.put(bypass, row);
            rows.add(row);
        }

        System.out.println("Got " + rows.size() + " bypasses");

        Platform.runLater(() ->
        {
            bypasses.getItems().setAll(rows);
            displayCount(cnt_total, model.getTotal());
            displayCounts();
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

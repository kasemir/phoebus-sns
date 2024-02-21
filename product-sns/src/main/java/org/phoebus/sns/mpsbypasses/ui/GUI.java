/*******************************************************************************
 * Copyright (c) 2019-2024 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.sns.mpsbypasses.ui;

import static org.phoebus.sns.mpsbypasses.MPSBypasses.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import org.phoebus.core.types.ProcessVariable;
import org.phoebus.framework.jobs.Job;
import org.phoebus.framework.jobs.JobManager;
import org.phoebus.framework.persistence.Memento;
import org.phoebus.framework.preferences.PhoebusPreferenceService;
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
import org.phoebus.ui.focus.FocusUtility;
import org.phoebus.ui.javafx.UpdateThrottle;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TableColumn.SortType;
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
    // Sort settings are saved to preferences to keep them
    // when user closes and then re-opens the MPS table.
    // Memento is used to persist instance via restarts.
    private Preferences prefs = PhoebusPreferenceService.userNodeForClass(GUI.class);

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

    /** Actual data, see createTable() for sorted list wrapper etc. */
    private final ObservableList<BypassRow> bypasses = FXCollections.observableArrayList(
            // ObservableList calls this 'extractor' to know which properties to monitor for changes
            // Bypass and request don't change at runtime, they'll be re-loaded
            // as part of a complete 'bypasses' update.
            // But state can change whenever the PV sends a new value
            row -> new Observable[]
            {
                row.state
            });
    /** Table that shows 'bypasses' */
    private final TableView<BypassRow> bypass_table;
    /** Map from bypass in model to GUI row */
    private final ConcurrentHashMap<Bypass, BypassRow> model2gui = new ConcurrentHashMap<>();

    private final BeamModeMonitor beam_monitor;
    private final MachineModeMonitor machine_monitor;

    private final UpdateThrottle full_table_update = new UpdateThrottle(500, TimeUnit.MILLISECONDS, this::updateAllTableRows);
    private volatile Job initial_load = null;


    public GUI(final BypassModel model) throws Exception
    {
        this.model = model;

        add(createSelector(), 0, 0);
        add(createCounts(), 0, 1);
        add(new HBox(createOpState(), createLegend()), 0, 2);
        bypass_table = createTable();
        configureSort(prefs.getInt("sort_col", -1),
                      prefs.getBoolean("sort_up", true));
        add(bypass_table, 0, 3);

        createContextMenu();

        beam_monitor = new BeamModeMonitor(this::updateBeamMode);
        beam_monitor.start();
        machine_monitor = new MachineModeMonitor(this::updateMachineMode);
        machine_monitor.start();

        model.addListener(this);

        // Schedule initial load
        initial_load = JobManager.schedule("MPS Bypasses", monitor ->
        {
            // If a memento was saved, that might soon cancel this and trigger another reload()
            Thread.sleep(2000);
            if (monitor.isCanceled())
                return;
            logger.log(Level.FINE, "Initial reload()");
            reload();
        });

    }

    private Node createSelector()
    {
        sel_mode.getItems().addAll(MachineMode.values());
        sel_mode.setValue(model.getMachineMode());
        sel_state.getItems().addAll(BypassState.values());
        sel_state.setValue(model.getBypassFilter());
        sel_req.getItems().addAll(RequestState.values());
        sel_req.setValue(model.getRequestFilter());

        reload.setTooltip(new Tooltip("Re-load bypass information from Relational Database"));

        final HBox row = new HBox(5,
                // "Mode", which really was the MPS "Chain" is no longer used.
                // Always displaying "all chains"
                // new Label("Machine Mode:"), sel_mode,
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

    private TableView<BypassRow> createTable()
    {
        // Sorting:
        //
        // TableView supports sorting as a default when user clicks on columns,
        // but order is lost when data is added/removed.
        //
        // Wrapping the raw data into a SortedList persists the sort order
        // when elements are added/removed in the original data.
        // https://stackoverflow.com/questions/34889111/how-to-sort-a-tableview-programmatically
        //
        // The 'extractor' of the 'bypasses' instructs the list to also
        // trigger a re-sort when properties of existing rows change.
        // https://rterp.wordpress.com/2015/05/08/automatically-sort-a-javafx-tableview/
        final SortedList<BypassRow> sorted = new SortedList<>(bypasses);
        final TableView<BypassRow> table = new TableView<>(sorted);

        // Ensure that the sorted rows are always updated as the column sorting
        // of the TableView is changed by the user clicking on table headers.
        sorted.comparatorProperty().bind(table.comparatorProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Label("No Bypasses"));

        TableColumn<BypassRow, String> col = new TableColumn<>("#");
        col.setCellValueFactory(cell -> cell.getValue().name);
        col.setCellFactory(c -> new RowIndexCell());
        col.setPrefWidth(300);
        col.setMaxWidth(300);
        col.setSortable(false);
        table.getColumns().add(col);

        col = new TableColumn<>("Bypass");
        col.setCellValueFactory(cell -> cell.getValue().name);
        table.getColumns().add(col);

        col = new TableColumn<>("State");
        col.setCellValueFactory(cell -> cell.getValue().state);
        col.setCellFactory(c -> new StateCell());
        table.getColumns().add(col);

        col = new TableColumn<>("Requestor");
        col.setCellValueFactory(cell -> cell.getValue().requestor);
        table.getColumns().add(col);

        col = new TableColumn<>("Request Date");
        col.setCellValueFactory(cell -> cell.getValue().date);
        table.getColumns().add(col);

        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        // Save sort settings to preferences
        table.comparatorProperty().addListener(obs ->
        {
            int sort_col = -1;
            boolean sort_up = true;
            final ObservableList<TableColumn<BypassRow, ?>> cols = bypass_table.getColumns();
            for (TableColumn<BypassRow, ?> c : bypass_table.getSortOrder())
                for (int i=0; i<cols.size(); ++i)
                    if (cols.get(i) == c)
                    {
                        sort_col = i;
                        sort_up = c.getSortType() == SortType.ASCENDING;
                        break;
                    }
            prefs.putInt("sort_col", sort_col);
            prefs.putBoolean("sort_up", sort_up);
        });

        return table;
    }

    private void createContextMenu()
    {
        final MenuItem info = new ShowInfo(bypass_table);
        final MenuItem enter_request = new OpenWeb("Enter Bypass Request", MPSBypasses.url_enter_bypass);
        final MenuItem display_request = new OpenWeb("Bypass Display", MPSBypasses.url_view_bypass);

        final ContextMenu menu = new ContextMenu(info, enter_request, display_request);
        bypass_table.setOnContextMenuRequested(event ->
        {
            menu.getItems().setAll(info, enter_request, display_request);
            // Publish PVs of selected rows
            final List<ProcessVariable> pvs = new ArrayList<>();
            for (BypassRow row : bypass_table.getSelectionModel().getSelectedItems())
            {
                pvs.add(new ProcessVariable(row.bypass.getJumperPVName()));
                pvs.add(new ProcessVariable(row.bypass.getMaskPVName()));
            }
            SelectionService.getInstance().setSelection(bypass_table, pvs);

            // Add PV entries
            if (ContextMenuHelper.addSupportedEntries(FocusUtility.setFocusOn(bypass_table), menu))
                menu.getItems().add(3, new SeparatorMenuItem());


            menu.show(bypass_table.getScene().getWindow(), event.getScreenX(), event.getScreenY());
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
            model.selectMachineMode(monitor, sel_mode.getValue());
            initial_load = null;
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

        full_table_update.trigger();
    }

    @Override
    public void bypassesChanged()
    {
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

        Platform.runLater(() ->
        {
            bypasses.setAll(rows);
            displayCount(cnt_total, model.getTotal());
            displayCounts();
        });
    }

    private void configureSort(final int sort_col, final boolean sort_up)
    {
        if (sort_col < 0)
            bypass_table.getSortOrder().clear();
        else
        {
            final TableColumn<BypassRow, ?> col = bypass_table.getColumns().get(sort_col);
            bypass_table.getSortOrder().setAll(List.of(col));
            col.setSortType(sort_up ? SortType.ASCENDING : SortType.DESCENDING);
        }
    }

    public void restore(Memento memento)
    {
        final Job safe = initial_load;
        if (safe != null)
            safe.cancel();
        memento.getString("mode").ifPresent(req -> sel_mode.setValue(MachineMode.fromString(req)));
        memento.getString("state").ifPresent(req -> sel_state.setValue(BypassState.fromString(req)));
        memento.getString("request").ifPresent(req -> sel_req.setValue(RequestState.fromString(req)));

        // Restore optional sort
        configureSort(memento.getNumber("sort_col").orElse(-1).intValue(),
                      memento.getBoolean("sort_up").orElse(true));

        logger.log(Level.FINE, "Restoring from memento");
        model.setFilter(sel_state.getValue(), sel_req.getValue());

        reload();
    }

    public void save(final Memento memento)
    {
        memento.setString("mode", sel_mode.getValue().name());
        memento.setString("state", sel_state.getValue().name());
        memento.setString("request", sel_req.getValue().name());

        // Determine if a column is used to sort, up or down
        int sort_col = -1;
        boolean sort_up = true;
        final ObservableList<TableColumn<BypassRow, ?>> sorted = bypass_table.getSortOrder();
        final ObservableList<TableColumn<BypassRow, ?>> cols = bypass_table.getColumns();
        for (TableColumn<BypassRow, ?> c : sorted)
            for (int i=0; i<cols.size(); ++i)
                if (cols.get(i) == c)
                {
                    sort_col = i;
                    sort_up = c.getSortType() == SortType.ASCENDING;
                    break;
                }
        memento.setNumber("sort_col", sort_col);
        memento.setBoolean("sort_up", sort_up);
    }

    public void dispose()
    {
        full_table_update.dispose();
        beam_monitor.stop();
        machine_monitor.stop();
        model.stop();
    }
}

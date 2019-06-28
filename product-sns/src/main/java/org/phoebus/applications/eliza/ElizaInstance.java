package org.phoebus.applications.eliza;

import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;
import org.phoebus.ui.dialog.ExceptionDetailsErrorDialog;
import org.phoebus.ui.docking.DockItem;
import org.phoebus.ui.docking.DockPane;

@SuppressWarnings("nls")
public class ElizaInstance implements AppInstance
{
    /** Singleton instance maintained by {@link ElizaApp#create()} */
    static ElizaInstance INSTANCE;

    private final AppDescriptor app;

    private ElizaUI ui;
    private DockItem dock_item = null;


    ElizaInstance(final AppDescriptor app)
    {
        this.app = app;
        try
        {
            ui = new ElizaUI();
            dock_item = new DockItem(this, ui);
            DockPane.getActiveDockPane().addTab(dock_item);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError("Error", "Cannot create Eliza App", ex);
        }
    }

    @Override
    public AppDescriptor getAppDescriptor()
    {
        return app;
    }

    public void raise()
    {
        dock_item.select();
    }
}

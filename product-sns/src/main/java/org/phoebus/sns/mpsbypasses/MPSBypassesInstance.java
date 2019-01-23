package org.phoebus.sns.mpsbypasses;

import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;
import org.phoebus.sns.mpsbypasses.model.BypassModel;
import org.phoebus.sns.mpsbypasses.ui.GUI;
import org.phoebus.ui.dialog.ExceptionDetailsErrorDialog;
import org.phoebus.ui.docking.DockItem;
import org.phoebus.ui.docking.DockPane;

@SuppressWarnings("nls")
class MPSBypassesInstance implements AppInstance
{
    // Singleton instance maintained by MPSBypasses#create
    static MPSBypassesInstance INSTANCE;

    private final AppDescriptor app;

    private GUI gui;
    private DockItem dock_item = null;

    MPSBypassesInstance(final AppDescriptor app)
    {
        this.app = app;

        try
        {
            final BypassModel model = new BypassModel();
            final GUI gui = new GUI(model);
            dock_item = new DockItem(this, gui);
            dock_item.addClosedNotification(this::dispose);
            DockPane.getActiveDockPane().addTab(dock_item);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError("Error", "Cannot create MPS Bypasses App", ex);
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

    private void dispose()
    {
        if (gui != null)
            gui.dispose();
        INSTANCE = null;
    }
}

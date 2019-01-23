package org.phoebus.sns.mpsbypasses.ui;

import org.phoebus.sns.mpsbypasses.model.Bypass;
import org.phoebus.sns.mpsbypasses.model.BypassState;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/** Creates bypass display colors used for indicating the state of a {@link Bypass}
 *  combined with its {@link Request}
 *
 *  @author Delphy Armstrong - Original Author
 *  @author Kay Kasemir
 */
public class BypassColors
{
    private static Background silver   = new Background(new BackgroundFill(Color.rgb(192, 192, 192), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background orange   = new Background(new BackgroundFill(Color.rgb(255, 140,   0), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background lavender = new Background(new BackgroundFill(Color.rgb(224, 102, 255), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background blue     = new Background(new BackgroundFill(Color.rgb(  0, 191, 255), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background red      = new Background(new BackgroundFill(Color.rgb(255,   0,   0), CornerRadii.EMPTY, Insets.EMPTY));
    private static Background gold     = new Background(new BackgroundFill(Color.rgb(218, 165,  32), CornerRadii.EMPTY, Insets.EMPTY));

    /** Return the appropriate color based on the bypass state and bypass request status.
     *  See color code table in the online help document for an explanation.
     *  @param bypass_state {@link BypassState}
     *  @param requested Was the bypass requested?
     *  @return {@link Background}
     */
    public static Background getBypassColor(final BypassState bypass_state, final boolean requested)
    {
        switch (bypass_state)
        {
        case Bypassed:
            if (requested)
                return silver;
            return orange;
        case Bypassable:
            if (requested)
                return lavender;
            return blue;
        case NotBypassable:
        case Disconnected:
            if (requested)
                return gold;
            return blue;
        case InError:
        default:
            return red;
        }
    }
}

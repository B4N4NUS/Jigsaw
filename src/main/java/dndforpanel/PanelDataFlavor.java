package dndforpanel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;

/**
 * Враппер вкуса для ДНД у панели.
 */
public class PanelDataFlavor extends DataFlavor {
    public static final PanelDataFlavor SHARED_INSTANCE = new PanelDataFlavor();

    public PanelDataFlavor() {
        super(JPanel.class, null);
    }
}

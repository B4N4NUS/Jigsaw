package dndforpanel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Враппер переносимого объекта для панели.
 */
public class PanelTransferable implements Transferable {
    // Вкусы для ДНДропа.
    private DataFlavor[] flavors = new DataFlavor[]{PanelDataFlavor.SHARED_INSTANCE};
    // Переносимая панель.
    private JPanel panel;

    public PanelTransferable(JPanel panel) {
        this.panel = panel;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean supported = false;

        for (DataFlavor mine : getTransferDataFlavors()) {
            if (mine.equals(flavor)) {
                supported = true;
                break;
            }
        }
        return supported;
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object data = null;
        if (isDataFlavorSupported(flavor)) {
            data = getPanel();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
        return data;
    }
}

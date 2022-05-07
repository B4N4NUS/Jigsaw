package dndforpanel;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * Хендлер ДНДропа.
 */
public class DragGestureHandler implements DragGestureListener, DragSourceListener {

    private Container parent;
    private JPanel child;

    public DragGestureHandler(JPanel child) {

        this.child = child;

    }

    public JPanel getPanel() {
        return child;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public Container getParent() {
        return parent;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {

        Container parent = getPanel().getParent();
        setParent(parent);

        // Убираем панель на время ДНД.
        parent.remove(getPanel());

        // Перерисовываем область.
        parent.invalidate();
        parent.repaint();

        // Запускаем процесс переноса.
        Transferable transferable = new PanelTransferable(getPanel());
        DragSource ds = dge.getDragSource();
        ds.startDrag(dge, Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), transferable, this);

    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {

        if (!dsde.getDropSuccess()) {
            getParent().remove(getPanel());
        }
        // Перерисовываем фрейм.
        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
        getParent().invalidate();
        getParent().repaint();

    }
}
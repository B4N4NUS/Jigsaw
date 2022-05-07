import com.formdev.flatlaf.FlatLaf;
import dndforpanel.PanelDataFlavor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import javax.swing.JPanel;

/**
 * Класс, отвечающий за отрисовку поля с фигурой и игровую логику.
 */
public class Table extends JPanel {
    // Логическое представление поля.
    static int[][] field = new int[9][9];
    // Графическое представление поля.
    Cell[][] UIField = new Cell[9][9];

    // Размеры клеток.
    final int cellx = 25;
    final int celly = 25;

    // Выявленные путем проб и ошибок погрешности позиции мыши относительно поля.
    final int xOfset = 126;
    final int yOfset = 0;

    // Счет.
    public int score = 0;

    // Цвет поля.
    public static Color color = Color.WHITE;

    // Фигура.
    FigurePanel currentFig;

    // Слушатель ДНДропа.
    DropTargetListener dropHandler;
    DropTarget dropTarget;

    @Override
    public void addNotify() {
        super.addNotify();
        dropHandler = new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(PanelDataFlavor.SHARED_INSTANCE)) {
                    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                boolean success = false;
                // Обработка корректности произведенного переноса.
                if (dtde.isDataFlavorSupported(PanelDataFlavor.SHARED_INSTANCE)) {
                    Transferable transferable = dtde.getTransferable();
                    try {
                        Object data = transferable.getTransferData(PanelDataFlavor.SHARED_INSTANCE);
                        if (data instanceof JPanel panel) {
                            DropTargetContext dtc = dtde.getDropTargetContext();
                            Component component = dtc.getComponent();
                            if (component instanceof JComponent) {
                                Container parent = panel.getParent();
                                if (parent != null) {
                                    parent.remove(panel);
                                }
                                GridBagConstraints constraints = new GridBagConstraints();
                                constraints.gridwidth = 3;
                                constraints.gridheight = 3;
                                ((JComponent) component).add(panel, constraints);

                                success = true;
                                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                                panel.invalidate();
                                panel.repaint();

                            } else {
                                success = false;
                                dtde.rejectDrop();
                            }
                        } else {
                            success = false;
                            dtde.rejectDrop();
                        }
                    } catch (Exception exp) {
                        success = false;
                        dtde.rejectDrop();
                        exp.printStackTrace();
                    }
                } else {
                    success = false;
                    dtde.rejectDrop();
                }

                // Выбранная клетка. (тут координаты - не пиксели, а индексы в таблице)
                Point selectedCell = new Point(10, 10);
                // Конец нынешней клетки. (правый нижний пиксель)
                Point currentPosEnd = new Point();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        // Если идем по самому правому или нижнему ряду.
                        if (i == 8 || j == 8) {
                                currentPosEnd.x = 2*(int)MainFrame.cellX + UIField[i][j].position.x;
                                currentPosEnd.y = 2*(int)MainFrame.cellY + UIField[i][j].position.y;
                        } else {
                            currentPosEnd = UIField[i+1][j+1].position;
                        }
                        // Если позиция мыши находится в границе нынешней клетки.
                        if (UIField[i][j].position.x <= dtde.getLocation().x + xOfset && currentPosEnd.x > dtde.getLocation().x + xOfset) {
                            if (UIField[i][j].position.y - 10 <= dtde.getLocation().y + yOfset && currentPosEnd.y - 10 > dtde.getLocation().y + yOfset) {
                                selectedCell = new Point(i, j);
                            }
                        }

                    }
                }

                // Инициализация нового поля.
                int[][] newField = new int[9][9];
                for(int i = 0; i < 9; i++) {
                    for(int j = 0; j < 9; j++) {
                        newField[i][j] = field[i][j];
                    }
                }

                Transferable target = dtde.getTransferable();
                try {
                    // Вытаскиваем панель из ивента.
                    FigurePanel dragged = (FigurePanel) target.getTransferData(target.getTransferDataFlavors()[0]);

                    // Если выбранная область попадает в игровое поле.
                    if (selectedCell.x != 10) {
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                // Если поле закончилось, а фигура нет.
                                if (i + selectedCell.x > 8) {
                                    for(int k = 0; k < 3; k++) {
                                        // Если в фигуре есть еще ненулевые клетки.
                                        if (dragged.figure[i][k] == 1) {
                                            success = false;
                                            System.out.println("WRONG SPOT X");
                                            dtde.dropComplete(success);
                                            return;
                                        }
                                    }
                                    break;
                                }
                                // Если поле закончилось, а фигура нет.
                                if (j + selectedCell.y > 8) {
                                    for(int k = 0; k < 3; k++) {
                                        // Если в фигуре есть еще ненулевые клетки.
                                        if (dragged.figure[k][j] == 1) {
                                            System.out.println("WRONG SPOT Y");
                                            success = false;
                                            dtde.dropComplete(success);
                                            return;
                                        }
                                    }
                                    break;
                                }
                                // Если в фигуре на выбранной позиции ненулевая клетка.
                                if (dragged.figure[i][j] == 1) {
                                    if (newField[i + selectedCell.x][j + selectedCell.y] == 0) {
                                        newField[i + selectedCell.x][j + selectedCell.y] = 1;
                                    } else {
                                        success = false;
                                        dtde.dropComplete(success);
                                        return;
                                    }
                                }
                            }
                        }
                        if (success) {
                            field = newField;
                        }
                        //System.out.println("Added new figure to the matrix");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Перекрашиваем стол.
                repaintTable(success);
                // Даем знать ивенту, как прошел перенос.
                dtde.dropComplete(success);
            }
        };
        dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, dropHandler, true);
    }

    /**
     * Метод очистки стола.
     */
    public void clearTable() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                field[i][j] = 0;
                UIField[i][j].setEnabled(false);
            }
        }
    }

    /**
     * Метод начала игры.
     */
    public void startGame() {
        clearTable();
        score = 0;
        System.out.println("Board Cleared");
        //currentFig = new FigurePanel();
        currentFig.draw();
        System.out.println("Figure shown");
        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
    }

    /**
     * Метод остановки игры.
     */
    public void stopGame() {
        currentFig = new FigurePanel();
        currentFig.setSize(new Dimension(MainFrame.prefX, MainFrame.prefY));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 10;
        constraints.gridy = 0;
        constraints.gridheight = 3;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.NONE;
        try{
            remove(getComponent(82));
            System.out.println("cleared figure");
            add(currentFig, constraints);
        }catch (Exception ex){
            add(currentFig, constraints);
        }
        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
    }

    /**
     * Метод перекраски стола.
     * @param addNew - нужно ли добавлять новую фигуру или оставить прошлую.
     */
    private void repaintTable(boolean addNew) {
        score = 0;
        // Перерисовка GUI стола.
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (field[i][j] == 1) {
                    score++;
                    if (score > MainFrame.highscore) {
                        MainFrame.highscore = score;
                    }
                    UIField[i][j].setEnabled(true);
                }
                UIField[i][j].setBackground(color);
            }
        }
        // Пробуем убрать лишние панели и перерисовать их как надо.
        try{
            remove(82);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.NONE;
            constraints.gridx = 10;
            constraints.gridwidth = 3;
            constraints.gridheight = 3;
            if (addNew) {
                currentFig = new FigurePanel();
                currentFig.draw();
            }
            add(currentFig, constraints);
            FlatLaf.updateUI();
            FlatLaf.repaintAllFramesAndDialogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Конструктор.
     */
    public Table() {
        // Инициализация переменных панели.
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0;
        constraints.weighty = 0;

        // Инициализация поля.
        for (constraints.gridx = 0; constraints.gridx < 9; constraints.gridx++) {
            for (constraints.gridy = 0; constraints.gridy < 9; constraints.gridy++) {
                UIField[constraints.gridx][constraints.gridy] = new Cell();
                add(UIField[constraints.gridx][constraints.gridy], constraints);
            }
        }

        // Добавление сепаратора.
        constraints.gridx = 9;
        add(Box.createRigidArea(new Dimension(100,50)), constraints);
        constraints.gridx = 10;

        // Инициализация фигуры.
        constraints.gridy = 0;
        constraints.gridheight = 3;
        constraints.gridwidth = 3;
        currentFig = new FigurePanel();
        add(currentFig, constraints);

        // Обновление GUI.
        setVisible(true);
        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
    }
}

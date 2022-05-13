import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Класс, отвечающий за отрисовку поля с фигурой и игровую логику.
 */
public class Table extends JPanel implements MouseListener, MouseMotionListener {
    // Поле.
    private static int[][] field = new int[9][9];
    // Нынешний счет.
    public int score = 0;
    // Идет ли сейчас игра.
    private boolean play = false;
    // Нынешняя фигура.
    private int[][] fig;

    MainFrame owner;

    Graphics2D g2;
    // Координаты фигуры.
    double x1, y1, x2, y2, size;
    double offsetX, offsetY;
    boolean dragging = false;

    public Connection connection;

    /**
     * Конструктор.
     */
    public Table(MainFrame owner, Connection connection) {
        this.connection = connection;

        x1 = MainFrame.prefX * 10;
        y1 = MainFrame.prefY * 3;
        size = MainFrame.prefX * 3;
        x2 = x1 + size;
        y2 = y1 + size;

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.requestFocus();

        fig = Figure.getBlankFigure();

        this.owner = owner;
        setMaximumSize(new Dimension(MainFrame.prefX * 9, MainFrame.prefY * 9));
    }

    /**
     * Метод очистки стола.
     */
    public void clearTable() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                field[i][j] = 0;
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
        try {
            if (Connection.figIndex == -1) {
                showMessageDialog(null, "Can't get data from server", "Error", ERROR_MESSAGE);
                //owner.dispose();
                owner.table.stopGame();
                owner.table.setVisible(false);
                owner.custom.setVisible(true);
                owner.bStartStop.state = true;
                //owner.bStartStop.doClick();
            } else {
                fig = Figure.figures[Connection.figIndex];
            }
        } catch (Exception ex) {
            showMessageDialog(null, "Can't get next figure from server", "Error", ERROR_MESSAGE);
            ex.printStackTrace();
            //owner.dispose();
            owner.table.stopGame();
            owner.table.setVisible(false);
            owner.custom.setVisible(true);
            owner.bStartStop.state = true;
            //owner.bStartStop.doClick();
        }
        repaint();
        play = true;
    }

    /**
     * Метод остановки игры.
     */
    public void stopGame() {
        try {
            Connection.figIndex = -1;
            fig = Figure.getBlankFigure();
            repaint();
        } catch (Exception ignored) {
        }
        play = false;
    }

    /**
     * Переопределяем метод перерисовки, чтобы на поле отрисовывалась тянущаяся фигура.
     *
     * @param g - графика
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ui != null) {
            // Вытаскиваем цвета.
            ColorUIResource color = (ColorUIResource) UIManager.get("ComboBox.background");
            ColorUIResource backColor = (ColorUIResource) UIManager.get("Panel.background");
            if (Math.abs(color.getRed() - backColor.getRed()) < 20 && Math.abs(color.getGreen() - backColor.getGreen()) < 20 && Math.abs(color.getBlue() - backColor.getBlue()) < 20) {
                color = new ColorUIResource(FlatLaf.isLafDark() ? Color.WHITE : Color.BLACK);
            }
            // Делаем новые кисти.
            GradientPaint enabled = new GradientPaint(new Point(0, 0), new Color(color.getRed(), color.getGreen(),
                    color.getBlue()), new Point(0, getHeight()), new Color(color.getRed(), color.getGreen(),
                    color.getBlue()));
            GradientPaint disabled = new GradientPaint(new Point(0, 0), new Color(backColor.getRed(), backColor.getGreen(),
                    backColor.getBlue()), new Point(0, getHeight()), new Color(backColor.getRed(), backColor.getGreen(),
                    backColor.getBlue()));

            // Рисуем фигуру на позиции курсора.
            g2 = (Graphics2D) g;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (fig[i][j] == 1) {
                        g2.setPaint(enabled);
                    } else {
                        g2.setPaint(disabled);
                    }
                    g2.fillRoundRect((int) (x1 + MainFrame.prefX * i + 2), (int) (y1 + MainFrame.prefY * j + 2), MainFrame.prefX - 3, MainFrame.prefX - 3, 5, 5);
                }
            }

            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                Graphics2D g2 = (Graphics2D) g.create();
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                // Рисуем игровое поле.
                g2.setPaint(enabled);
                for (int i = 0; i < 9 * MainFrame.prefX; i += MainFrame.prefX) {
                    for (int j = 0; j < 9 * MainFrame.prefY; j += MainFrame.prefY) {
                        if (field[i / MainFrame.prefX][j / MainFrame.prefY] == 1) {
                            g2.fillRoundRect(i + 2, j + 2, MainFrame.prefX - 3, MainFrame.prefY - 3, 5, 5);
                        } else {
                        }
                        g2.drawRoundRect(i + 2, j + 2, MainFrame.prefX - 3, MainFrame.prefY - 3, 5, 5);
                    }
                }


                g2.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert scratchGraphics != null;
                scratchGraphics.dispose();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Обработка начала перетягивания.
     *
     * @param ev - ивент мышки.
     */
    @Override
    public void mousePressed(MouseEvent ev) {
        double mx = ev.getX();
        double my = ev.getY();

        if (mx > x1 && mx < x2 && my > y1 && my < y2) {
            dragging = true;
            offsetX = mx - x1;
            offsetY = my - y1;
        }
    }

    /**
     * Обработка начала перетягивания.
     *
     * @param arg0 - ивент мышки.
     */
    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Если перетаскивают именно фигуру.
        if (dragging) {
            boolean reject = false;
            // Смотрим попадание в таблицу.
            if (-5 < (int) x1 && (int) x1 < MainFrame.prefX * 9 && -5 < (int) y1 && (int) y1 < MainFrame.prefY * 9) {
                Point cell = new Point();
                double min = 10000000;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        // Находим минимальное расстояние до клетки от позиции мыши.
                        if (Math.sqrt((i * MainFrame.prefX - x1) * (i * MainFrame.prefX - x1) + (j * MainFrame.prefY - y1) * (j * MainFrame.prefY - y1)) < min) {
                            cell.x = i;
                            cell.y = j;
                            min = Math.sqrt((i * MainFrame.prefX - x1) * (i * MainFrame.prefX - x1) + (j * MainFrame.prefY - y1) * (j * MainFrame.prefY - y1));
                        }
                    }
                }
                Point trueFigSize = new Point(0, 0);
                for (int i = 1; i < 3; i++) {
                    for (int j = 1; j < 3; j++) {
                        if (fig[i][j] == 1) {
                            if (trueFigSize.x != i) {
                                trueFigSize.x = i;
                            }
                            if (trueFigSize.y != j) {
                                trueFigSize.y = j;
                            }
                        }
                    }
                }
                // Если фигура не влезает в границы.
                if (trueFigSize.x + cell.x > 8 || trueFigSize.y + cell.y > 8) {
                    reject = true;
                }

                if (!reject) {
                    // Если фигура не влезает в границы.
                    for (int i = cell.x; i < cell.x + 3; i++) {
                        for (int j = cell.y; j < cell.y + 3; j++) {
                            if (fig[i - cell.x][j - cell.y] == 1 && field[i][j] == 1) {
                                reject = true;
                                break;
                            }
                        }
                    }
                }
                // Если фигура все таки влезла.
                if (!reject) {
                    for (int i = cell.x; i < cell.x + 3; i++) {
                        for (int j = cell.y; j < cell.y + 3; j++) {
                            if (fig[i - cell.x][j - cell.y] == 1) {
                                field[i][j] = 1;
                            }
                        }
                    }
                    System.out.println("Placed");
                    x1 = MainFrame.prefX * 10;
                    y1 = MainFrame.prefY * 3;
                    x2 = x1 + size;
                    y2 = y1 + size;

                    // Просим сервер выдать следующую фигуру.
                    try {
                        connection.writeToServer("0 " + Connection.figIndex);
                        Thread.sleep(50);
                        fig = Figure.figures[Connection.figIndex];
                    } catch (Exception ex) {
                        showMessageDialog(null, "Can't get next figure from server\nTry restarting app ", "Error", ERROR_MESSAGE);
                        owner.dispose();
                    }
                }
                if (reject) {
                    x1 = MainFrame.prefX * 10;
                    y1 = MainFrame.prefY * 3;
                    x2 = x1 + size;
                    y2 = y1 + size;
                } else {
                    score++;
                }
            } else {
                x1 = MainFrame.prefX * 10;
                y1 = MainFrame.prefY * 3;
                x2 = x1 + size;
                y2 = y1 + size;
            }
            repaint();
        }
        dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Обработка перетаскивания
     *
     * @param ev - ивент вышки.
     */
    @Override
    public void mouseDragged(MouseEvent ev) {
        if (play) {
            if (dragging) {
                double mx = ev.getX();
                double my = ev.getY();

                x1 = mx - offsetX;
                y1 = my - offsetY;
                x2 = x1 + size;
                y2 = y1 + size;
                //square = new Rectangle2D.Double(x1, y1, size, size);
                repaint();
                //System.out.println("SOSU");
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

import com.formdev.flatlaf.FlatLaf;
import dndforpanel.DragGestureHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Objects;

/**
 * Панель с фигурой.
 */
public class FigurePanel extends JPanel {
    // Логическое представление фигуры.
    public int[][] figure;
    // Визуальное представление фигуры.
    public JButton[][] UIFigure = new JButton[3][3];
    // Переменная, контролирующая отрисовку вспомогательной панели позади фигуры.
    static boolean visibleLayout = false;

    /**
     * Конструктор с инициализацией фигуры.
     */
    public FigurePanel() {
        figure = Figure.getRandomFigure();
    }

    /**
     * Метод, инициализирующий GUI фигуры.
     */
    public void draw() {
        setLayout(new OverlayLayout(this));
        JPanel fig = new JPanel();
        fig.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        fig.setBounds(0, 0, 300, 300);

//        if (visibleLayout) {
//            fig.setBackground(Color.white);
//        } else {
//            fig.setBackground(null);
//        }
//
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                constraints.gridx = i;
//                constraints.gridy = j;
//                if (figure[i][j] == 0) {
//                    UIFigure[i][j] = new JButton(" ");
//                    fig.add(Box.createRigidArea(new Dimension(20, 20)), constraints);
//                } else {
//
//                    UIFigure[i][j] = new JButton(" ");
//                    UIFigure[i][j].setBounds(new Rectangle(50, 50));
//                    UIFigure[i][j].setBackground(Table.color);
//                    UIFigure[i][j].setFocusPainted(false);
//                    UIFigure[i][j].setFocusable(false);
//                    UIFigure[i][j].setRolloverEnabled(false);
//                    fig.add(UIFigure[i][j], constraints);
//                }
//            }
//        }
        // Мои попытки перекрыть нажатие кнопок элементом GUI.
//        constraints.gridx = 0;
//        constraints.gridy = 0;
//        constraints.gridheight = 3;
//        constraints.gridwidth = 3;
//        Component layer = new JLabel();
//        layer.setEnabled(false);
//        layer.setBounds(0,0,140,140);
//        layer.setBackground(Color.red);
//        add(layer);
        add(fig);
        setVisible(true);
    }

    private DragGestureRecognizer dgr;
    private DragGestureHandler dragGestureHandler;

    @Override
    public void addNotify() {
        super.addNotify();

        // Добавление нового хендлера ДНДропа.
        if (dgr == null) {
            dragGestureHandler = new DragGestureHandler(this);
            dgr = DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                    this,
                    DnDConstants.ACTION_MOVE,
                    dragGestureHandler);

        }

    }

    @Override
    public void removeNotify() {
        if (dgr != null) {
            dgr.removeDragGestureListener(dragGestureHandler);
            dragGestureHandler = null;

        }
        dgr = null;
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (ui != null) {
            setSize(3*MainFrame.prefX, 3*MainFrame.prefY);
            ColorUIResource color = (ColorUIResource) UIManager.get("ComboBox.background");
            ColorUIResource fontColor = (ColorUIResource) UIManager.get("ComboBox.foreground");
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                Graphics2D g2 = (Graphics2D) g.create();
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                g2.setPaint(new GradientPaint(new Point(0, 0), new Color(color.getRed(), color.getGreen(),
                        color.getBlue()), new Point(0, getHeight()), new Color(color.getRed(), color.getGreen(),
                        color.getBlue())));
                for(int i = 0; i < 3*MainFrame.prefX; i += MainFrame.prefX) {
                    for(int j = 0; j < 3*MainFrame.prefY; j += MainFrame.prefY) {
                        g2.fillRoundRect(2, 2, i, j, MainFrame.prefX/2, MainFrame.prefY/2);
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
}

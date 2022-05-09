import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 * Панель с фигурой.
 */
public class FigurePanel extends JPanel implements MouseListener, MouseMotionListener {
    // Логическое представление фигуры.
    public int[][] figure;
    // Визуальное представление фигуры.
    public Cell[][] UIFigure = new Cell[3][3];
    // Переменная, контролирующая отрисовку вспомогательной панели позади фигуры.
    static boolean visibleLayout = false;


    Graphics2D g2;
    Rectangle2D square;
    Color colour;

    double x1, y1, x2, y2, size;
    double offsetX, offsetY;

    boolean dragging = false;



    /**
     * Конструктор с инициализацией фигуры.
     */
    public FigurePanel() {
        figure = Figure.getRandomFigure();

        x1 = 10.0;
        y1 = 10.0;
        size = 40.0;
        x2 = x1 + size;
        y2 = y1 + size;

        square = new Rectangle2D.Double(x1, y1, size, size);
        colour = Color.BLUE;
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.requestFocus();
    }

    /**
     * Метод, инициализирующий GUI фигуры.
     */
    public void draw() {
        //setLayout(new OverlayLayout(this));
        //JPanel fig = new JPanel();
        //fig.setLayout(new GridBagLayout());
        //GridBagConstraints constraints = new GridBagConstraints();
        //constraints.gridheight = 1;
        //constraints.gridwidth = 1;
        //fig.setBounds(0, 0, 300, 300);

//        if (visibleLayout) {
//            fig.setBackground(Color.white);
//        } else {
//            fig.setBackground(null);
//        }
////
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                constraints.gridx = i;
//                constraints.gridy = j;
//                if (figure[i][j] == 0) {
//                    UIFigure[i][j] = new Cell();
//                    add(Box.createRigidArea(new Dimension(20, 20)), constraints);
//                } else {
//
//                    UIFigure[i][j] = new JButton(" ");
//                    UIFigure[i][j].setBounds(new Rectangle(50, 50));
//                    UIFigure[i][j].setBackground(Table.color);
//                    UIFigure[i][j].setFocusPainted(false);
//                    UIFigure[i][j].setFocusable(false);
//                    UIFigure[i][j].setRolloverEnabled(false);
//                    add(UIFigure[i][j], constraints);
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
        //add(fig);
        setVisible(true);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        g2.draw(square);
        g2.setColor(colour);
        g2.draw(square);

        //        if (ui != null) {
//            setPreferredSize(new Dimension(3*MainFrame.prefX, 3*MainFrame.prefY));
//            ColorUIResource color = (ColorUIResource) UIManager.get("ComboBox.background");
//            ColorUIResource backColor = (ColorUIResource) UIManager.get("Panel.background");
//            Graphics scratchGraphics = (g == null) ? null : g.create();
//            try {
//                Graphics2D g2 = (Graphics2D) g.create();
//                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//                g2.setRenderingHints(qualityHints);
//
//                GradientPaint enabled = new GradientPaint(new Point(0, 0), new Color(color.getRed(), color.getGreen(),
//                        color.getBlue()), new Point(0, getHeight()), new Color(color.getRed(), color.getGreen(),
//                        color.getBlue()));
//                GradientPaint disabled = new GradientPaint(new Point(0, 0), new Color(0,0,0,0 ), new Point(0, getHeight()), new Color(0,0,0,0));
//                for(int i = 0; i < 3*MainFrame.prefX; i += MainFrame.prefX) {
//                    for(int j = 0; j < 3*MainFrame.prefY; j += MainFrame.prefY) {
//                        if (figure[i/ MainFrame.prefX][j/MainFrame.prefY] == 1) {
//                            g2.setPaint(enabled);
//                        } else {
//                            g2.setPaint(disabled);
//                        }
//                        g2.fillRoundRect(i+2, j+2,  MainFrame.prefX-3, MainFrame.prefY-3, 5,5);
//                        g2.drawRoundRect(i+2, j+2,  MainFrame.prefX-3, MainFrame.prefY-3, 5,5);
//                    }
//                }
//
//
//
//                g2.dispose();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                assert scratchGraphics != null;
//                scratchGraphics.dispose();
//            }
//        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

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

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent ev)
    {
        if (dragging)
        {
            double mx = ev.getX();
            double my = ev.getY();

            x1 = mx - offsetX;
            y1 = my - offsetY;
            x2 = x1 + size;
            y2 = y1 + size;
            square = new Rectangle2D.Double(x1, y1, size, size);
            repaint();
            System.out.println("SOSU");
        }


    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

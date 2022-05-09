import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Класс клетки игрового поля и фигур.
 */
public class Cell extends JPanel {
    public Point position;

    /**
     * Конструктор инициализации клетки.
     */
    public Cell() {
        setPreferredSize(new Dimension(MainFrame.prefX, MainFrame.prefY));
//        setText(" ");
        // Делаем кнопку неактивной.
        //setEnabled(false);
        //setBounds(new Rectangle(MainFrame.prefX, MainFrame.prefY));
        // Убираем фокусировку при наведении курсора.
//        setFocusPainted(false);
//        setFocusable(false);
//        setRolloverEnabled(false);
        // Выставляем цвет.
        //setBackground(Table.color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ui != null) {
            ColorUIResource color = (ColorUIResource) UIManager.get("ComboBox.background");
            ColorUIResource fontColor = (ColorUIResource) UIManager.get("ComboBox.foreground");
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                //BufferedImage img = scale(src, (int) Math.round(getHeight()*0.9), (int) Math.round(getHeight()*0.9));

                assert g != null;
                Graphics2D g2 = (Graphics2D) g.create();
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                g2.setPaint(new GradientPaint(new Point(0, 0), new Color(color.getRed(), color.getGreen(),
                        color.getBlue()), new Point(0, getHeight()), new Color(color.getRed(), color.getGreen(),
                        color.getBlue())));
                g2.fillRoundRect(2, 2, getHeight() - 3, getHeight() - 3, 5, 5);

                //g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
                //System.out.println("sosi");
                //ui.update(scratchGraphics, this);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert scratchGraphics != null;
                scratchGraphics.dispose();
            }
        }
    }
}

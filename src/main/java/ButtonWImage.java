

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Objects;

// Класс кастомной кнопочки, написанный специально для использования с FlatLaf.
public class ButtonWImage extends JButton {

    private final String path;
    private final String alterPath;
    public boolean state;

    /**
     * Конструктор.
     * @param path - путь до картинки.
     * @param alterPath - путь до альтернативной картинки.
     */
    public ButtonWImage(String path, String alterPath) {
        super(" ");
        this.path = path;
        this.alterPath = alterPath;
        state = true;
    }

    /**
     * Метод каста из Image в BufferedImage
     * @param img - изначальная картинка.
     * @return - переделанная картинка.
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (ui != null) {
            ColorUIResource color = (ColorUIResource) UIManager.get("ComboBox.background");
            ColorUIResource fontColor = (ColorUIResource) UIManager.get("ComboBox.foreground");
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                String text = getText();
                BufferedImage img;
                if (state) {
                    img = toBufferedImage(ImageIO.read(Objects.requireNonNull(
                                    ButtonWImage.class.getResource(path + ".png"))).
                            getScaledInstance((int) Math.round(getHeight() * 0.8), (int) Math.round(getHeight() * 0.8),
                                    Image.SCALE_SMOOTH));
                } else {
                    img = toBufferedImage(ImageIO.read(Objects.requireNonNull(
                                    ButtonWImage.class.getResource( alterPath + ".png" ))).
                            getScaledInstance((int) Math.round(getHeight() * 0.8), (int) Math.round(getHeight() * 0.8),
                                    Image.SCALE_SMOOTH));
                }
                assert g != null;

                Graphics2D g2 = (Graphics2D) g.create();
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                g2.setPaint(new GradientPaint(new Point(0, 0), new Color(color.getRed(), color.getGreen(),
                        color.getBlue()), new Point(0, getHeight()), new Color(color.getRed(), color.getGreen(),
                        color.getBlue())));
                g2.fillRoundRect(2, 2, getHeight() - 3, getHeight() - 3, 20, 20);
                g2.drawImage(img, (int) Math.round(getHeight() * 0.1), (int) Math.round(getHeight() * 0.1 ),
                        (img1, infoflags, x, y, width, height) -> false);

                g2.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert scratchGraphics != null;
                scratchGraphics.dispose();
            }
        }
    }
}

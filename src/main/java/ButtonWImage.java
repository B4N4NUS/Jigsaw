import com.formdev.flatlaf.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Objects;

public class ButtonWImage extends JButton {

    private final String path;
    private final String alterPath;
    public boolean state;

    public ButtonWImage(String path, String alterPath) {
        super(" ");
        this.path = path;
        this.alterPath = alterPath;
        state = true;
    }

    public static BufferedImage scale(BufferedImage src, int w, int h) {
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }


    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
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
                //BufferedImage img = scale(src, (int) Math.round(getHeight()*0.9), (int) Math.round(getHeight()*0.9));
                BufferedImage img;
                if (state) {
                    img = toBufferedImage(ImageIO.read(Objects.requireNonNull(
                                    ButtonWImage.class.getResource(!FlatLaf.isLafDark() ? path + ".png" : path + "-white.png"))).
                            getScaledInstance((int) Math.round(getHeight() * 0.8), (int) Math.round(getHeight() * 0.8),
                                    Image.SCALE_SMOOTH));
                } else {
                    img = toBufferedImage(ImageIO.read(Objects.requireNonNull(
                                    ButtonWImage.class.getResource(!FlatLaf.isLafDark() ? alterPath + ".png" : alterPath + "-white.png"))).
                            getScaledInstance((int) Math.round(getHeight() * 0.8), (int) Math.round(getHeight() * 0.8),
                                    Image.SCALE_SMOOTH));
                }
                Font font = getFont();
                assert g != null;
                FontMetrics fm = g.getFontMetrics();
                int width = fm.stringWidth(text);

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
                        new ImageObserver() {
                            @Override
                            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                                return false;
                            }
                        });

                //g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
                //System.out.println("sosi");
                //ui.update(scratchGraphics, this);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert scratchGraphics != null;
                scratchGraphics.dispose();
            }
        }
    }
}

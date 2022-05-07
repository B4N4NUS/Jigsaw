import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Класс, предоставляющий возможность сохранять и загружать настройки приложения.
 */
public class SettingsSaver {

    static Color color;
    static String theme;
    static boolean enableLayer;

    /**
     * Метод получения настроек из файла.
     *
     * @param path - путь до файла.
     * @throws IOException - эксепшн, связанный с чтением файла.
     */
    static void getSettings(String path) throws IOException {
        FileReader fw = new FileReader(path);
        String data = "";
        char[] raw = new char[100];
        int length = fw.read(raw);

        // Вытаскиваем информацию из файла.
        for (int i = 0; i < length; i++) {
            data += raw[i];
        }

        // Обрабатываем информацию
        String[] dataArray = data.split("\n");
        if (dataArray.length != 4) {
            throw new IOException("Incorrect File Data");
        } else {
            int r = 0, g = 0, b = 0;

            theme = dataArray[0];
            MainFrame.highscore = Integer.parseInt(dataArray[2]);
            String[] rawColor = dataArray[1].split(" ");

            if (rawColor.length != 3) {
                throw new IOException("Incorrect File Data");
            } else {
                r = Integer.parseInt(rawColor[0]);
                g = Integer.parseInt(rawColor[1]);
                b = Integer.parseInt(rawColor[2]);
            }

            color = new Color(r, g, b);
            enableLayer = Objects.equals(dataArray[3], "true");
        }
        //System.out.println("Reading - success");
        fw.close();
    }

    /**
     * Метод установки восстановленных данный.
     */
    static void setSettings() {
        Table.color = color;

        FigurePanel.visibleLayout = enableLayer;
        if (enableLayer) {
            SettingsFrame.enabledLayer = true;
        }

        Class<? extends FlatLaf> newLaf = FlatDarculaLaf.class;
        switch (theme) {
            case "Dark" -> {
                newLaf = FlatDarculaLaf.class;
                SettingsFrame.currentTheme = 0;
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/black.png");
            }
            case "Gradianto Deep Oceam" -> {
                newLaf = FlatGradiantoDeepOceanIJTheme.class;
                SettingsFrame.currentTheme = 1;
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/blue.png");
            }
            case "High Contrast" -> {
                newLaf = FlatHighContrastIJTheme.class;
                Table.color = Color.BLACK;
                SettingsFrame.currentTheme = 2;
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/orange.png");
            }
            case "Material Desigh Dark" -> {
                newLaf = FlatMaterialDesignDarkIJTheme.class;
                SettingsFrame.currentTheme = 3;
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/pink.png");
            }
        }

        try {
            UIManager.setLookAndFeel(newLaf.getName().toString());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
    }

    /**
     * Метод сохранения настроек в файл.
     * @param path - Путь.
     * @throws IOException - Эксепшн из-за запрета на запись.
     */
    static void saveSettings(String path) throws IOException {
        String str = "";
        switch (SettingsFrame.currentTheme) {
            case 0 -> {
                theme = "Dark";
            }
            case 1 -> {
                theme = "Gradianto Deep Oceam";
            }
            case 2 -> {
                theme = "High Contrast";
            }
            case 3 -> {
                theme = "Material Desigh Dark";
            }
        }
        color = Table.color;
        String savedColor = color.getRed() + " " + color.getGreen() + " " + color.getBlue();

        str = theme + "\n" + savedColor + "\n" + MainFrame.highscore + "\n" + FigurePanel.visibleLayout;
        FileWriter fw = new FileWriter(path);
        for (int i = 0; i < str.length(); i++) {
            fw.write(str.charAt(i));
        }
        //System.out.println("Writing - success");
        fw.close();
    }
}

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;

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

    public static boolean settingsAlive;
    public static Rectangle settingsBounds = null;
    public static Rectangle mainBounds = null;
    public static String theme;

    /**
     * Метод получения настроек из файла.
     *
     * @param path - путь до файла.
     * @throws IOException - эксепшн, связанный с чтением файла.
     */
    static void getSettings(String path) throws IOException {
        FileReader fw = new FileReader(path);
        //str = theme + "\n" + MainFrame.highscore + "\n" + settingsAlive + "\n" + sb + "\n" + mb;
        String data = "";
        char[] raw = new char[300];
        int length = fw.read(raw);

        // Вытаскиваем информацию из файла.
        for (int i = 0; i < length; i++) {
            data += raw[i];
        }

        // Обрабатываем информацию
        String[] dataArray = data.split("\n");


        int x, y, w, h;

        theme = dataArray[0];
        MainFrame.highscore = Integer.parseInt(dataArray[1]);
        String[] rawColor = dataArray[3].split(",");

        x = Integer.parseInt(rawColor[0]);
        y = Integer.parseInt(rawColor[1]);
        if (x == -1) {
            settingsBounds = null;
        } else {
            settingsBounds = new Rectangle(x, y, 1, 1);
        }
        System.out.println("settings " + settingsBounds);
        rawColor = dataArray[4].split(",");

        x = Integer.parseInt(rawColor[0]);
        y = Integer.parseInt(rawColor[1]);
        if (x == -1) {
            mainBounds = null;
        } else {
            mainBounds = new Rectangle(x, y,1,1);
        }
        System.out.println("main " + mainBounds);

        //System.out.println("Reading - success");
        fw.close();
    }

    /**
     * Метод установки восстановленных данный.
     */
    static void setSettings() {

        try {
            UIManager.setLookAndFeel(theme);
            for(var i : FlatAllIJThemes.INFOS) {
                if (Objects.equals(i.getClassName(), theme)) {
                    SettingsFrame.oldSel = i;
                    System.out.println(i.getName());
                } else {
                    //System.out.println(i.getClassName() + " " + theme);
                }
            }
           // SettingsFrame.oldSel = UIManager.getLookAndFeel()
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

//        FlatLaf.updateUI();
//        FlatLaf.repaintAllFramesAndDialogs();
    }

    /**
     * Метод сохранения настроек в файл.
     *
     * @param path - Путь.
     * @throws IOException - Эксепшн из-за запрета на запись.
     */
    static void saveSettings(String path) throws IOException {
        String str = "";
        theme = SettingsFrame.oldSel.getClassName();
        String sb, mb;
        if (settingsBounds == null) {
            sb = "-1,-1,-1,-1";
        } else {
            sb = settingsBounds.x + "," + settingsBounds.y + "," + settingsBounds.width + "," + settingsBounds.height;
        }
        mb = mainBounds.x + "," + mainBounds.y + "," + mainBounds.width + "," + mainBounds.height;
        str = theme + "\n" + MainFrame.highscore + "\n" + settingsAlive + "\n" + sb + "\n" + mb + "\n";
        FileWriter fw = new FileWriter(path);
        for (int i = 0; i < str.length(); i++) {
            fw.write(str.charAt(i));
        }
        //System.out.println("Writing - success");
        fw.close();
    }
}

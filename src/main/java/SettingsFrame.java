import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Класс диалога с настройками.
 */
public class SettingsFrame extends JDialog {
    // Настоящий цвет игрового поля.
    static Color realColor = Color.WHITE;
    // Выбранная тема.
    static int currentTheme = 0;
    // Имена тем.
    static String[] themeNames = {"Dark","Gradianto Deep Ocean", "High Contrast", "Material Desigh Dark"};
    // Переменная, отвечающая за отрисовку заднего плана у фигур.
    static boolean enabledLayer = false;
    // Кнопки.
    JButton[] themesButtons = new JButton[themeNames.length];
    JButton changeColor = new JButton("Select Figure Color");

    private JList<FlatAllIJThemes.FlatIJLookAndFeelInfo> themeList;
    public static FlatAllIJThemes.FlatIJLookAndFeelInfo oldSel = FlatAllIJThemes.INFOS[0];

    /**
     * Конструктор с инициализацией GUI.
     * @param owner - фрейм, что вызвал диалог.
     */
    public SettingsFrame(JFrame owner) {
        super(owner, true);
        Init();
        if (oldSel == null) {
            oldSel = themeList.getSelectedValue();
        }
    }

    protected void saveFrameInfo() {
        Rectangle b = getBounds();
        SettingsSaver.settingsBounds = b;
        try {
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ignored) {}
//        server.config.setProperty("wifi.width", b.width);
//        server.config.setProperty("wifi.height", b.height);
//        server.config.setProperty("wifi.posx", b.x);
//        server.config.setProperty("wifi.posy", b.y);
//        server.saveConfig();
    }

    /**
     * Метод инициализации GUI.
     */
    public void Init() {
        // Инициализируем параметры диалогового окна.
        setTitle("Themes");
        setBounds(0, 0, 450, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setLayout(null);
        setMinimumSize(new Dimension(450, 400));
        // Почему-то не работает(((((.
        setMaximumSize(new Dimension(451, 401));

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                saveFrameInfo();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                saveFrameInfo();
            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        // Инициализируем кнопки выбора тем.
        for (int i = 0; i < themeNames.length; i++) {
            themesButtons[i] = new JButton(themeNames[i] + " Theme");
            themesButtons[i].setBounds(0, i * 50, 200, 45);
            themesButtons[i].setActionCommand(themeNames[i]);
            //add(themesButtons[i]);
        }
        // Убираем подсветку выбора с первой кнопки на экране, чтобы не вводить пользователя в заблуждение.
        themesButtons[0].setSelected(false);
        themesButtons[0].setFocusPainted(false);
        themesButtons[currentTheme].setFocusPainted(true);
        themesButtons[currentTheme].setBorderPainted(true);

        // Если выбрана тема High Contrast, то не даем пользователю изменять цвет.
        if (currentTheme == 2) {
            changeColor.setEnabled(false);
        }

        themeList = new JList<>(FlatAllIJThemes.INFOS);
        //themeList.setPreferredSize(new Dimension(100,100));
        themeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(themeList);
        themeList.setLayoutOrientation(JList.VERTICAL);
        themeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                String name = ((FlatAllIJThemes.FlatIJLookAndFeelInfo) value).getName();
                int sep = name.indexOf('/');
                if (sep >= 0)
                    name = name.substring(sep + 1).trim();

                JComponent c = (JComponent) super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                c.setToolTipText(buildToolTip((FlatAllIJThemes.FlatIJLookAndFeelInfo) value));
                return c;
            }

            private String buildToolTip(FlatAllIJThemes.FlatIJLookAndFeelInfo ti) {
                return "Name: " + ti.getName();
            }
        });
        themeList.setModel(new AbstractListModel<FlatAllIJThemes.FlatIJLookAndFeelInfo>() {
            private static final long serialVersionUID = 1536029084261517876L;

            @Override
            public int getSize() {
                return FlatAllIJThemes.INFOS.length;
            }

            @Override
            public FlatAllIJThemes.FlatIJLookAndFeelInfo getElementAt(int index) {
                return FlatAllIJThemes.INFOS[index];
            }
        });

        if (oldSel != null) {
            for (int i = 0; i < FlatAllIJThemes.INFOS.length; i++) {
                FlatAllIJThemes.FlatIJLookAndFeelInfo theme = FlatAllIJThemes.INFOS[i];
                if (oldSel.getName().equals(theme.getName())) {
                    themeList.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < FlatAllIJThemes.INFOS.length; i++) {
                String currentName = FlatAllIJThemes.INFOS[i].getClassName();
//                if (currentName.equals(server.config.getString("laf"))) {
//                    themeList.setSelectedIndex(i);
//                    break;
//                }
            }
        }
        // select first theme if none selected
        if (themeList.getSelectedIndex() < 0)
            themeList.setSelectedIndex(0);
        // scroll selection into visible area
        int sel = themeList.getSelectedIndex();
        if (sel >= 0) {
            Rectangle bounds = themeList.getCellBounds(sel, sel);
            if (bounds != null)
                themeList.scrollRectToVisible(bounds);
        }
        themeList.addListSelectionListener(this::themesListValueChanged);

        JScrollPane themesPane;
        add(themeList);
        add(themesPane = new JScrollPane(themeList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        themesPane.setMinimumSize(new Dimension(200, 200));
        pack();

        if (SettingsSaver.settingsBounds != null) {
            setLocation(SettingsSaver.settingsBounds.x ,SettingsSaver.settingsBounds.y);
        }

    }
    private void themesListValueChanged(ListSelectionEvent e) {
        FlatAllIJThemes.FlatIJLookAndFeelInfo themeInfo = themeList.getSelectedValue();

        if (e.getValueIsAdjusting())
            return;

        EventQueue.invokeLater(() -> {
            setTheme(themeInfo);
            oldSel = themeList.getSelectedValue();
        });
    }
    public void setTheme(FlatAllIJThemes.FlatIJLookAndFeelInfo themeInfo) {
        try {
            UIManager.setLookAndFeel(themeInfo.getClassName());
//            server.config.setProperty("laf", themeInfo.getClassName());
//            server.saveConfig();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
        repaint();
    }


    @Override
    public void dispose() {
        super.dispose();
        // Пытаемся сохранить настройки.
        try {
            //SettingsSaver.settingsAlive = false;
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // На всякий случай выходим из приложения таким образом, во избежание проблем с закрытием потоков.
        //System.exit(0);
    }
}

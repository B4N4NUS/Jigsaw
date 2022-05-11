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
    private JList<FlatAllIJThemes.FlatIJLookAndFeelInfo> themeList;
    public static FlatAllIJThemes.FlatIJLookAndFeelInfo oldSel = FlatAllIJThemes.INFOS[0];

    /**
     * Конструктор с инициализацией GUI.
     *
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
        } catch (Exception ignored) {
        }
    }

    /**
     * Метод инициализации GUI.
     */
    public void Init() {
        setTitle("Themes");
        setBounds(0, 0, 450, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(450, 400));
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


        themeList = new JList<>(FlatAllIJThemes.INFOS);
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
        themeList.setModel(new AbstractListModel<>() {
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

            }
        }

        if (themeList.getSelectedIndex() < 0)
            themeList.setSelectedIndex(0);

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
            setLocation(SettingsSaver.settingsBounds.x, SettingsSaver.settingsBounds.y);
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
        try {
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

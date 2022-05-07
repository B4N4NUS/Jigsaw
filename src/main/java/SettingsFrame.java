import javax.swing.*;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Класс диалога с настройками.
 */
public class SettingsFrame extends JDialog implements ActionListener {
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

    /**
     * Конструктор с инициализацией GUI.
     * @param owner - фрейм, что вызвал диалог.
     */
    public SettingsFrame(JFrame owner) {
        super(owner, true);
        Init();
    }

    /**
     * Метод инициализации GUI.
     */
    public void Init() {
        // Инициализируем параметры диалогового окна.
        setTitle("Settings");
        setBounds(0, 0, 450, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(null);
        setMinimumSize(new Dimension(450, 400));
        // Почему-то не работает(((((.
        setMaximumSize(new Dimension(451, 401));

        // Инициализируем кнопки выбора тем.
        for (int i = 0; i < themeNames.length; i++) {
            themesButtons[i] = new JButton(themeNames[i] + " Theme");
            themesButtons[i].setBounds(0, i * 50, 200, 45);
            themesButtons[i].addActionListener(this);
            themesButtons[i].setActionCommand(themeNames[i]);
            add(themesButtons[i]);
        }
        // Убираем подсветку выбора с первой кнопки на экране, чтобы не вводить пользователя в заблуждение.
        themesButtons[0].setSelected(false);
        themesButtons[0].setFocusPainted(false);
        themesButtons[currentTheme].setFocusPainted(true);
        themesButtons[currentTheme].setBorderPainted(true);

        // Инициализация кнопки изменения цвета.

        changeColor.setBounds(210, 0, 200,100);
        changeColor.setActionCommand("color");
        changeColor.addActionListener(this);
        add(changeColor);

        JLabel layoutLabel = new JLabel("Enable Figure Layout? ");
        layoutLabel.setBounds(210, 100,150,50);
        add(layoutLabel);

        // Инициализация кнопки изменения подсветки фигуры.
        JRadioButton enableLayout = new JRadioButton(" ");
        enableLayout.setBounds(360,100,50,50);
        enableLayout.setActionCommand("layout");
        enableLayout.addActionListener(this);
        enableLayout.setSelected(enabledLayer);
        add(enableLayout);

        // Если выбрана тема High Contrast, то не даем пользователю изменять цвет.
        if (currentTheme == 2) {
            changeColor.setEnabled(false);
        }
    }

    /**
     * Хендлер нажатия на кнопку.
     * @param e - событие нажатия.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Выбор новой темы.
        Class<? extends FlatLaf> newLaf = FlatDarculaLaf.class;
        switch (e.getActionCommand()) {
            case "Dark" -> {
                newLaf = FlatDarculaLaf.class;
                Table.color = realColor;
                changeColor.setEnabled(true);
                currentTheme = 0;
                // Меняем иконку экрана с игрой.
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/black.png");
            }
            case "Gradianto Deep Ocean" -> {
                newLaf =FlatGradiantoDeepOceanIJTheme.class;
                Table.color = realColor;
                currentTheme = 1;
                changeColor.setEnabled(true);
                // Меняем иконку экрана с игрой.
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/blue.png");
            }
            case "High Contrast" -> {
                Table.color = Color.BLACK;
                newLaf = FlatHighContrastIJTheme.class;
                currentTheme = 2;
                changeColor.setEnabled(false);
                // Меняем иконку экрана с игрой.
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/orange.png");
            }
            case "Material Desigh Dark" -> {
                newLaf = FlatMaterialDesignDarkIJTheme.class;
                Table.color = realColor;
                changeColor.setEnabled(true);
                currentTheme = 3;
                // Меняем иконку экрана с игрой.
                MainFrame.Jigsaw = new ImageIcon("src/main/java/icons/pink.png");
            }
            case "color" -> {
                if (currentTheme == 2) {
                    Table.color = Color.BLACK;
                } else {
                    realColor = JColorChooser.showDialog(null, "Choose a color", Table.color);
                    Table.color = realColor;
                }
                FlatLaf.updateUI();
                FlatLaf.repaintAllFramesAndDialogs();
                return;
            }
            case "layout" -> {
                // Включаем/выключаем отрисовку панели на фигурах.
                enabledLayer = !enabledLayer;
                FigurePanel.visibleLayout = !FigurePanel.visibleLayout;
                System.out.println(FigurePanel.visibleLayout + " current layout option");
                FlatLaf.updateUI();
                FlatLaf.repaintAllFramesAndDialogs();
                return;
            }
        }
        // Пушим новый GUI в UIManager.
        try {
            UIManager.setLookAndFeel(newLaf.getName().toString());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        // Обновляем GUI.
        FlatLaf.updateUI();
        FlatLaf.repaintAllFramesAndDialogs();
    }
}

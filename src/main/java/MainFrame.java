import javax.swing.*;

import com.formdev.flatlaf.*;

import java.awt.*;
import java.awt.event.*;
import java.util.TimerTask;
import java.util.Timer;

public class MainFrame extends JFrame implements ActionListener {
    public static int prefX = 25;
    public static int prefY = 25;


    // Иконки.
//    ImageIcon imSettings = new ImageIcon("src/main/java/icons/settings-sliders-white.png");
//    ImageIcon imStart = new ImageIcon("src/main/java/icons/caret-circle-right-white.png");
//    ImageIcon imStop = new ImageIcon("src/main/java/icons/cross-circle-white.png");
    static ImageIcon Jigsaw = new ImageIcon("src/main/java/icons/black.png");
    // Игровое поле.
    private Table table;
    // Кнопки.
    JButton bStartStop = new JButton();
    JButton bSettings = new JButton();
    // Рекорд.
    public static int highscore = 0;
    // Длительность раунда.
    static long elapsedSeconds = 1;
    // Контроль таймера.
    boolean startTimer = false;
    // Таймер времени игры.
    Timer timer = new Timer("Timer");
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (startTimer) {
                setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[score: " + table.score + "] \t[highscore: "
                        + highscore + "]");
            }
        }
    };
    // Таймер обновления GUI.
    Timer updatePosition = new Timer("position updater");
    TimerTask update = new TimerTask() {
        @Override
        public void run() {
            if (table == null) {
                cancel();
            }
            // Обновляем иконку приложения.
            setIconImage(Jigsaw.getImage());
            // Обновляем абсолютное положение клеток игрового поля.
            updateAbsolutePosition(table);
            // Пытаемся обновить цвет игрового поля.
            try {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        table.UIField[i][j].setBackground(Table.color);
                    }
                }
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        table.currentFig.UIFigure[i][j].setBackground(Table.color);
                    }
                }
            } catch (Exception ex) {
                //System.out.println("Unable to update field");
            }
        }
    };

    /**
     * Метод, сжимающий картики под заданный размер.
     * @param wight - ширина области.
     * @param height - высота области.
     * @param icon - иконка.
     * @return - подогнанная под размеры картинка.
     */
    public static ImageIcon resizeImage(int wight, int height, ImageIcon icon) {
        var image = icon.getImage();
        return new ImageIcon(image.getScaledInstance((int) (wight * 0.8), (int) (height * 0.8), Image.SCALE_SMOOTH));
    }

    /**
     * Мейн метод, с которого начинается работа программы.
     * @param args - параметры запуска (не используются)
     */
    public static void main(String[] args) {
        // Пытаемся восстановить сохраненные настройки.
        try {
            SettingsSaver.getSettings("Jigsaw.save");
            SettingsSaver.setSettings();
        } catch (Exception ex) {
            FlatDarculaLaf.setup();
            System.out.println("No save file found!");
        }
        // Инициализаруем фрейм с игрой.
        MainFrame frame = new MainFrame();
        frame.Init();
    }

    /**
     * Метод, инициализирующий GUI игры.
     */
    public void Init() {
        // Задаем внешний вид окну.
        //setIconImage(Jigsaw.getImage());
        setTitle("Jigsaw");
        //setBounds(100, 100, 1200, 800);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(650,450));

//        GridBagConstraints constraints = new GridBagConstraints();
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.anchor = GridBagConstraints.NORTHWEST;
//        constraints.insets = new Insets(5, 5, 0, 0);

        // Запускаем таймер отсчета игрового времени.
        //timer.schedule(task, 0, 1000L);

        // Обрабатываем GUI кнопки настроек.
//        constraints.weightx = 0;
//        constraints.weighty = 0;
//        constraints.gridx = 0;
//        constraints.gridy = 0;
        bSettings = new ButtonWImage(" ", "/icons/settings", false);
        bSettings.setPreferredSize(new Dimension(50, 50));
        bSettings.setActionCommand("settings");
        bSettings.addActionListener(this);
        bSettings.setMnemonic(KeyEvent.VK_S);
        add(bSettings);
        bSettings.setBounds(0,0,50,50);
        //add(bSettings, constraints);
        //bSettings.setIcon(resizeImage(bSettings.getBounds().width, bSettings.getBounds().height, imSettings));

        // Обрабатываем GUI кнопки старта и окончания игры.
        //constraints.gridy = 1;
        bStartStop = new ButtonWImage(" ", "/icons/start", false);
        bStartStop.setPreferredSize(new Dimension(50, 50));
        bStartStop.setMnemonic(KeyEvent.VK_P);
        bStartStop.setActionCommand("start_game");
        bStartStop.addActionListener(this);
        //add(bStartStop, constraints);
        add(bStartStop);
        bStartStop.setBounds(0,60,50,50);
        add(Box.createVerticalStrut(1));
        //bStartStop.setIcon(resizeImage(bStartStop.getBounds().width, bStartStop.getBounds().height, imStart));

        // Обрабатываем GUI игрового поля.
        table = new Table();
        //updatePosition.schedule(update, 0, 500L);
        bStartStop.setPreferredSize(new Dimension(table.cellx * 9, table.celly * 9));
        //table.setBounds(0, 0, table.cellx * 9, table.celly * 9);
        table.setBounds((int)Math.round(getWidth()/2 - table.cellx*4.5),(int)Math.round(getHeight()/2 - table.celly*4.5),table.cellx*9,table.celly*9);
        add(table);

//        getComponent(0).setBounds(0, 0, 50, 50);
//        getComponent(1).setBounds(0, 60, 50, 50);
//        getComponent(2).setBounds(200, 200, 1000, 1000);
        // Обновляем GUI.
//        FlatDarculaLaf.updateUI();
//        FlatDarculaLaf.repaintAllFramesAndDialogs();

        // Обновляем абсолютную позицию клеток стола.
        //updateAbsolutePosition(table);

//        addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                // Обновляем абсолютную позицию клеток стола.
//                updateAbsolutePosition(table);
//                FlatLaf.updateUI();
//                FlatLaf.repaintAllFramesAndDialogs();
//            }
//        });
        //pack();
    }

    // Абсолютные позиции левой верхней и правой нижней клетки.
    static Point lockLeftUp;
    static Point lockRightBot;
    // Средняя высота и длина клетки.
    static double cellX;
    static double cellY;

    /**
     * Метод обновления игрового стола.
     * @param table - игровой стол.
     */
    private void updateAbsolutePosition(Table table) {
        lockLeftUp = SwingUtilities.convertPoint(table.UIField[0][0], table.getX(), table.getY(), getContentPane());
        lockRightBot = SwingUtilities.convertPoint(table.UIField[8][8], table.getX(), table.getY(), getContentPane());
        cellX = 1.0 * (lockRightBot.x - lockLeftUp.x) / 9;
        cellY = 1.0 * (lockRightBot.y - lockLeftUp.y) / 9;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                table.UIField[i][j].position = SwingUtilities.convertPoint(table.UIField[i][j], table.getX(),
                        table.getY(), getContentPane());
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // Отключаем таймеры.
        task.cancel();
        update.cancel();
        // Пытаемся сохранить настройки.
        try {
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ex) {
            System.out.println("No save file found!");
        }
        // На всякий случай выходим из приложения таким образом, во избежание проблем с закрытием потоков.
        System.exit(0);
    }

    /**
     * Хендлер нажатия на кнопки.
     * @param e - Информация о событие нажатия.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start_game" -> {
                // Начинаем игру.
                table.startGame();
                // Изменяем назначение и внешний вид кнопки.
                bStartStop = new ButtonWImage(" ", "/icons/settings-sliders", false);
                bStartStop.setSize(50, 50);
                bStartStop.setActionCommand("stop_game");
                // Запускаем таймер.
                startTimer = true;
            }
            case "stop_game" -> {
                // Заканчиваем игру.
                table.stopGame();
                // Изменяем кнопку.
                bStartStop.setActionCommand("start_game");
                bStartStop.setSize(50, 50);
                bStartStop = new ButtonWImage(" ", "/icons/settings-sliders", false);
                // Отрубаем таймер.
                startTimer = false;
                // Изменяем название программы.
                setTitle("Jigsaw \t[played for: " + (elapsedSeconds-1) + " s] \t[score: " + table.score + "] " +
                        "\t[highscore: " + highscore + "]");
                // Обнуляем количество прошедших секунд.
                elapsedSeconds = 1;
            }
            case "settings" -> {
                // Вызываем диалог с настройками.
                SettingsFrame settingsFrame = new SettingsFrame(this);
                settingsFrame.setVisible(true);
            }
        }
    }
}

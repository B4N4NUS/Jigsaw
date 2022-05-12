import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainFrame extends JFrame implements ActionListener {
    public static int prefX = 25;
    public static int prefY = 25;

    public static boolean won = false;
    public static boolean lost = false;
    public static Connection connection;

    private static final ImageIcon Jigsaw = new ImageIcon("/icons/black.png");

    private Table table;
    private PreGameCustomization custom;
    private ButtonWImage bStartStop;
    private ButtonWImage bSettings;

    public JLabel nameLabel, enemyLabel;

    public static int highscore = 0;
    static long elapsedSeconds = 1;

    boolean startTimer = false;

    Timer timer = new Timer("Timer");
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (startTimer) {
                setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[score: " + table.score + "] \t[highscore: "
                        + highscore + "]");
            }
            if (won) {
                table.stopGame();
                table.setVisible(false);
                showMessageDialog(null, "YOU WON LOL!", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
                won = false;
//                cancel();
//                Init();
            }
            if (lost) {
                table.stopGame();
                table.setVisible(false);
                showMessageDialog(null, "YOU LOST LOL!", "NOT Congratulation", ERROR_MESSAGE);
                lost = false;
//                cancel();
//                Init();
            }
        }
    };



    public static void main(String[] args) {
        // Пытаемся восстановить сохраненные настройки.
        try {
            SettingsSaver.getSettings("Jigsaw.save");
            SettingsSaver.setSettings();
        } catch (Exception ex) {
            FlatArcIJTheme.setup();
            System.out.println("No save file found!");
        }


        MainFrame frame = new MainFrame();
        frame.Init();

        if (SettingsSaver.mainBounds != null) {
            frame.setLocation(SettingsSaver.mainBounds.x, SettingsSaver.mainBounds.y);
        }
        try {
            Thread.sleep(10);
        } catch (Exception ignored) {
        }

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                frame.saveFrameInfo();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                frame.saveFrameInfo();
            }

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });

    }

    /**
     * Метод, инициализирующий GUI игры.
     */
    public void Init() {
        getContentPane().removeAll();
        try {
            setIconImage(ImageIO.read(MainFrame.class.getResource("/icons/black.png")));
        } catch (Exception ignored) {
        }
        setTitle("Jigsaw");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        timer.schedule(task, 0, 1000L);

        bSettings = new ButtonWImage("/icons/settings", "/icons/settings");
        bSettings.setPreferredSize(new Dimension(50, 50));
        bSettings.setActionCommand("settings");
        bSettings.addActionListener(this);
        bSettings.setMnemonic(KeyEvent.VK_S);
        add(bSettings);
        bSettings.setBounds(5, 0, 50, 50);


        setMinimumSize(new Dimension(650, 450));


        bStartStop = new ButtonWImage("/icons/clock", "/icons/clock");
        bStartStop.setPreferredSize(new Dimension(75, 75));
        bStartStop.setMnemonic(KeyEvent.VK_P);
        bStartStop.setActionCommand("start_game");
        bStartStop.addActionListener(this);
        add(bStartStop);
        bStartStop.setBounds(5, 60, 50, 50);
        add(Box.createVerticalStrut(1));

        nameLabel = new JLabel("oh the misery, everybody");
        add(nameLabel);
        nameLabel.setPreferredSize(new Dimension(100, 50));
        nameLabel.setBounds(5, 200, 100, 50);
        enemyLabel = new JLabel("wants to be my enemy");
        add(enemyLabel);
        enemyLabel.setPreferredSize(new Dimension(100, 50));
        enemyLabel.setBounds(5, 400, 100, 50);


        custom = new PreGameCustomization(this);
        custom.setPreferredSize(new Dimension(prefX * 11, prefY * 9));
        add(custom);
        custom.setBounds((int) Math.round(getWidth() / 2 - prefX * 4.5), 0, prefX * 15, prefY * 4);


        table = new Table(this, connection);
        table.setVisible(false);
        table.setPreferredSize(new Dimension(prefX * 11, prefY * 9));
        add(table);
        add(Box.createVerticalStrut(1));
        table.setBounds((int) Math.round(getWidth() / 2 - prefX * 4.5), (int) Math.round(getHeight() / 2 - prefY * 4.5), prefX * 15, prefY * 11);
        add(Box.createVerticalStrut(1));

        pack();
    }


    protected void saveFrameInfo() {
        Rectangle b = getBounds();
        SettingsSaver.mainBounds = b;
        try {
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        task.cancel();


        try {
            SettingsSaver.saveSettings("Jigsaw.save");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            connection.writeToServer("4 0 4");
        } catch (Exception ignored) {}

        System.exit(0);
    }

    /**
     * Хендлер нажатия на кнопки.
     *
     * @param e - Информация о событие нажатия.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start_game" -> {
                connection = new Connection(custom.port.getText(), custom.name.getText(), this);
                try {
                    connection.openSocket();
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //connection.readFromServer();
                // Начинаем игру.
                table.setVisible(true);
                custom.setVisible(false);
                table.connection = connection;
                table.startGame();
                // Изменяем назначение и внешний вид кнопки.
                bStartStop.state = !bStartStop.state;
                bStartStop.setSize(50, 50);
                bStartStop.setActionCommand("stop_game");
                // Запускаем таймер.
                startTimer = true;
            }
            case "stop_game" -> {
                // Заканчиваем игру.
                try {
                    connection.writeToServer("4 ima ded lol");
                    connection.closeSocket();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                table.stopGame();
                // Изменяем кнопку.
                bStartStop.setActionCommand("start_game");
                bStartStop.setSize(50, 50);
                bStartStop.state = !bStartStop.state;
                // Отрубаем таймер.
                startTimer = false;
                // Изменяем название программы.
                setTitle("Jigsaw \t[played for: " + (elapsedSeconds - 1) + " s] \t[score: " + table.score + "] " +
                        "\t[highscore: " + highscore + "]");
                // Обнуляем количество прошедших секунд.
                elapsedSeconds = 1;
            }
            case "settings" -> {
                // Вызываем диалог с настройками.
                SettingsSaver.settingsAlive = true;
                SettingsFrame settingsFrame = new SettingsFrame(this);
                settingsFrame.setVisible(true);
                SettingsSaver.settingsAlive = false;
            }
        }
    }
}

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme;

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
    // Общие размеры элементов интерфейса для комфортной работы.
    public static int prefX = 25;
    public static int prefY = 25;

    // Переменные, отвечающие за победу/поражение игрока.
    public static boolean won = false;
    public static boolean lost = false;

    // Класс с сокетом для подключения к серверу.
    public static Connection connection;

    // Игровое поле.
    public Table table;

    // Элементы интерфейса.
    public PreGameCustomization custom;
    public ButtonWImage bStartStop;
    public ButtonWImage bSettings;
    public JLabel nameLabel, enemyLabel;

    // Счетчик прошедших секунд.
    public static long elapsedSeconds = 1;

    boolean startTimer = false;

    public static int secondScore = 0;

    // Таймер, работающий каждую секунду и меняющий ЮИ.
    Timer timer = new Timer("Timer");
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (startTimer) {
                // Если задано время матча.
                if (Connection.maxTime != -1) {
                    // Если игра должна была закончиться, но сервер ничего не сказал.
                    if (Connection.maxTime == elapsedSeconds) {
                        setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[score: " + table.score + "]");
                        if (table.score > secondScore) {
                            won = true;
                        } else {
                            lost = true;
                        }
                    } else {
                        setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[max time: " + Connection.maxTime + "] \t[score: " + table.score + "]");
                    }
                } else {
                    setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[score: " + table.score + "]");
                }

            }
            // Если игрок победил.
            if (won) {
                table.stopGame();
                table.setVisible(false);
                custom.setVisible(true);
                bStartStop.doClick();
                showMessageDialog(null, "YOU WON!\nYour score: " +table.score +"\n" + (secondScore > 0? enemyLabel.getText() + "'s score: " + secondScore: ""), "Congratulation", JOptionPane.INFORMATION_MESSAGE);
                won = false;
                try {
                    connection.closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Если игрок проиграл.
            if (lost) {
                table.stopGame();
                table.setVisible(false);
                custom.setVisible(true);
                bStartStop.doClick();
                showMessageDialog(null, "YOU LOST!\nYour score: " +table.score +"\n" + (secondScore > 0? enemyLabel.getText() + "'s score: " + secondScore: ""), "NOT Congratulation", ERROR_MESSAGE);
                lost = false;
            }
        }
    };


    /**
     * Место запуска программы.
     * @param args - входные аргументы
     */
    public static void main(String[] args) {
        FlatMaterialDarkerContrastIJTheme.setup();
        MainFrame frame = new MainFrame();
        frame.Init();
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


        bStartStop = new ButtonWImage("/icons/start", "/icons/busy");
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

        setLocationRelativeTo(null);
        pack();
    }

    /**
     * Переопределенный метод очистки ресурсов.
     */
    @Override
    public void dispose() {
        super.dispose();
        task.cancel();
        // Пытаемся закрыть сокет.
        try {
            if (connection != null) {
                if (connection.socket != null) {
                    if (!connection.socket.isClosed()) {
                        connection.writeToServer("4 ima ded lol");
                    }
                    connection.closeSocket();
                }
            }
        } catch (IOException ignored) {}

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
                connection = new Connection(custom.port.getText(), custom.name.getText(),custom.ip.getText(), this);
                try {
                    connection.openSocket();
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
                //connection.readFromServer();
                // Начинаем игру.
                table.setVisible(true);
                custom.setVisible(false);
                table.connection = connection;
                table.startGame();
                // Изменяем назначение и внешний вид кнопки.
                //bStartStop.state = !bStartStop.state;
                bStartStop.setSize(50, 50);
                bStartStop.setActionCommand("stop_game");
                // Запускаем таймер.
                startTimer = true;
            }
            case "stop_game" -> {
                // Заканчиваем игру.
                    connection.writeToServer("4 ima ded lol");

                table.stopGame();
                // Изменяем кнопку.
                bStartStop.setActionCommand("start_game");
                bStartStop.setSize(50, 50);
                bStartStop.state = !bStartStop.state;
                // Отрубаем таймер.
                startTimer = false;
                // Изменяем название программы.
                setTitle("Jigsaw \t[played for: " + (elapsedSeconds - 1) + " s] \t[score: " + table.score + "] ");
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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends JFrame implements ActionListener {
    // Общие размеры элементов интерфейса для комфортной работы.
    public static int prefX = 25;
    public static int prefY = 25;

    // Класс с сокетом для подключения к серверу.
    public Connection connection;

    // Игровое поле.
    public Table table;

    // Элементы интерфейса.
    public PreGameCustomization custom;
    public ButtonWImage bStartStop;
    public ButtonWImage bTop;
    public JLabel nameLabel, enemyLabel;
    public JPanel buttonsPane;

    // Счетчик прошедших секунд.
    public static long elapsedSeconds = 1;

    public boolean firstGame = true;
    public boolean startTimer = false;

    public String maxTime = null;

    // Таймер, работающий каждую секунду и меняющий имя программы в зависимости от действий пользователя.
    Timer timer = new Timer("Timer");
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (startTimer) {
                // Если задано время матча.
                if (maxTime != null) {
                    setTitle("Jigsaw \t[time: " + elapsedSeconds++ + " s] \t[score: " + table.score + "]");
                }

            } else {
                if (maxTime != null) {
                    setTitle("Jigsaw \t[max time: " + maxTime + "]");
                } else {
                    setTitle("Jigsaw");
                }
            }
            if (enemyLabel != null && nameLabel != null) {
                if (connection != null && connection.running && connection.enemy != null) {
                    enemyLabel.setText("Enemy: " + connection.enemy);
                } else {
                    enemyLabel.setText("");
                }
                if (custom != null) {
                    nameLabel.setText("You: " + custom.name.getText());
                }
            }
        }
    };


    /**
     * Место запуска программы.
     *
     * @param args - входные аргументы
     */
    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.Init();
    }

    /**
     * Метод, инициализирующий GUI игры.
     */
    public void Init() {
        getContentPane().removeAll();
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(MainFrame.class.getResource("/icons/black.png"))));
        } catch (Exception ignored) {
        }
        setTitle("Jigsaw");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(650, 450));
        setSize(new Dimension(650, 450));
        setLayout(null);

        timer.schedule(task, 0, 1000L);

        buttonsPane = new JPanel();
        buttonsPane.setLayout(null);

        bTop = new ButtonWImage("/icons/top", "");
        bTop.setToolTipText("Top players");
        bTop.setMnemonic(KeyEvent.VK_P);
        bTop.setActionCommand("top");
        bTop.addActionListener(this);
        buttonsPane.add(bTop);
        bTop.setBounds(5, 5, 50, 50);
        bTop.addActionListener(e -> connection.getTop());
        buttonsPane.add(Box.createVerticalStrut(1));

        bStartStop = new ButtonWImage("/icons/start", "/icons/busy");
        bStartStop.setToolTipText("Start/Stop game");
        bStartStop.setMnemonic(KeyEvent.VK_P);
        bStartStop.setActionCommand("start_game");
        bStartStop.addActionListener(this);
        buttonsPane.add(bStartStop);
        bStartStop.setBounds(5, 60, 50, 50);
        buttonsPane.add(Box.createVerticalStrut(1));

        add(buttonsPane);
        buttonsPane.setBounds(5, 5, 100, 450);
        buttonsPane.setVisible(true);

        nameLabel = new JLabel("name");
        buttonsPane.add(nameLabel);
        nameLabel.setPreferredSize(new Dimension(100, 50));

        enemyLabel = new JLabel("enemy");
        buttonsPane.add(enemyLabel);
        enemyLabel.setPreferredSize(new Dimension(100, 50));

        nameLabel.setBounds(10, 200, 100, 50);
        enemyLabel.setBounds(10, 250, 100, 50);

        custom = new PreGameCustomization(this);
        add(custom);
        custom.setBounds(50, 5, 550, 450);

        table = new Table(this);
        table.setVisible(false);
        table.setPreferredSize(new Dimension(prefX * 11, prefY * 9));
        add(table);
        add(Box.createVerticalStrut(1));
        table.setBounds((int) Math.round(1.0 * getWidth() / 2 - prefX * 4.5), (int) Math.round(1.0 * getHeight() / 2 - prefY * 4.5), prefX * 15, prefY * 11);
        add(Box.createVerticalStrut(1));

        setLocationRelativeTo(null);
        buttonsPane.setVisible(false);
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
                if (Connection.socket != null) {
                    if (!Connection.socket.isClosed()) {
                        connection.writeToServer("3 closed app");
                    }
                    connection.closeSocket();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    /**
     * Метод, меняющий текущий вид ЮИ.
     *
     * @param startingScreen - показывать ли стартовое окно приложения.
     */
    public void changeVisibleElems(boolean startingScreen) {
        if (startingScreen) {
            buttonsPane.setVisible(false);
            table.setVisible(false);
            custom.setVisible(true);
        } else {
            buttonsPane.setVisible(true);
            table.setVisible(true);
            custom.setVisible(false);
        }
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
                System.out.println("Game started");
                if (firstGame) {
                    firstGame = false;
                } else {
                    System.out.println("Opened new socket");
                }
                // Говорим серверу о своей готовности.
                connection.writeToServer("2 ready");
                connection.playing = true;
                // Начинаем игру.
                table.startGame();
                bStartStop.state = !bStartStop.state;
                bStartStop.setActionCommand("stop_game");
            }
            case "stop_game" -> {
                startTimer = false;
                maxTime = null;
                // Заканчиваем игру.
                table.stopGame();
                // Изменяем кнопку.
                bStartStop.setActionCommand("start_game");
                //bStartStop.setSize(50, 50);
                bStartStop.state = true;
                // Отрубаем таймер.
                startTimer = false;
                // Изменяем название программы.
                setTitle("Jigsaw \t[played for: " + (elapsedSeconds - 1) + " s] \t[score: " + table.score + "] ");
                // Обнуляем количество прошедших секунд.
                elapsedSeconds = 1;
                changeVisibleElems(true);
            }
        }
    }
}

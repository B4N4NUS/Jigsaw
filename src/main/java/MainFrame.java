import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public Connection connection;

    // Игровое поле.
    public Table table;

    // Элементы интерфейса.
    public PreGameCustomization custom;
    public ButtonWImage bStartStop;
    public ButtonWImage bTop;
    public JLabel nameLabel, enemyLabel;

    // Счетчик прошедших секунд.
    public static long elapsedSeconds = 1;

    boolean startTimer = false;

    public static int secondScore = 0;

    public JPanel buttonsPane;

    public boolean firstGame = true;

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
                revalidate();
                repaint();
                showMessageDialog(null, "YOU WON!\nYour score: " + table.score + "\n" + (secondScore > 0 ? enemyLabel.getText() + "'s score: " + secondScore : ""), "Congratulation", JOptionPane.INFORMATION_MESSAGE);
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
                revalidate();
                repaint();
                showMessageDialog(null, "YOU LOST!\nYour score: " + table.score + "\n" + (secondScore > 0 ? enemyLabel.getText() + "'s score: " + secondScore : ""), "NOT Congratulation", ERROR_MESSAGE);
                lost = false;

            }
        }
    };


    /**
     * Место запуска программы.
     *
     * @param args - входные аргументы
     */
    public static void main(String[] args) {
        //FlatMaterialDarkerContrastIJTheme.setup();
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
        setMinimumSize(new Dimension(650, 450));
        setSize(new Dimension(650, 450));
        setLayout(null);


        timer.schedule(task, 0, 1000L);

        buttonsPane = new JPanel();
        buttonsPane.setLayout(null);

        bTop = new ButtonWImage("/icons/top", "");
        bTop.setToolTipText("Top players");
        //bTop.setPreferredSize(new Dimension(75, 75));
        bTop.setMnemonic(KeyEvent.VK_P);
        bTop.setActionCommand("top");
        bTop.addActionListener(this);
        buttonsPane.add(bTop);
        bTop.setBounds(5, 5, 50, 50);
        bTop.addActionListener(e -> {
            connection.getTop();
        });
        buttonsPane.add(Box.createVerticalStrut(1));

        bStartStop = new ButtonWImage("/icons/start", "/icons/busy");
        bStartStop.setToolTipText("Start/Stop game");
        //bStartStop.setPreferredSize(new Dimension(25, 25));
        bStartStop.setMnemonic(KeyEvent.VK_P);
        bStartStop.setActionCommand("start_game");
        bStartStop.addActionListener(this);
        buttonsPane.add(bStartStop);
        bStartStop.setBounds(5, 60, 50, 50);
        buttonsPane.add(Box.createVerticalStrut(1));

        add(buttonsPane);
        buttonsPane.setBounds(5, 5, 100, 450);
        //buttonsPane.setBackground(Color.red);
        buttonsPane.setVisible(true);
        //buttonsPane.setLocation(0,0);


        nameLabel = new JLabel("name");
        buttonsPane.add(nameLabel);
        nameLabel.setPreferredSize(new Dimension(100, 50));

        enemyLabel = new JLabel("enemy");
        buttonsPane.add(enemyLabel);
        enemyLabel.setPreferredSize(new Dimension(100, 50));

        nameLabel.setBounds(10, 200, 100, 50);
        enemyLabel.setBounds(10, 250, 100, 50);


        custom = new PreGameCustomization(this);
        //custom.setPreferredSize(new Dimension(prefX * 11, prefY * 9));
        add(custom);
        custom.setBounds(50, 5, 550, 450);


        table = new Table(this);
        table.setVisible(false);
        table.setPreferredSize(new Dimension(prefX * 11, prefY * 9));
        add(table);
        add(Box.createVerticalStrut(1));
        table.setBounds((int) Math.round(getWidth() / 2 - prefX * 4.5), (int) Math.round(getHeight() / 2 - prefY * 4.5), prefX * 15, prefY * 11);
        add(Box.createVerticalStrut(1));

        setLocationRelativeTo(null);
        buttonsPane.setVisible(false);
        //pack();
    }

    /**
     * Переопределенный метод очистки ресурсов.
     */
    @Override
    public void dispose() {
        super.dispose();
        task.cancel();
        // Пытаемся закрыть сокет.
        // TODO закрыть сокет.
        try {
            if (connection != null) {
                if (connection.socket != null) {
                    if (!connection.socket.isClosed()) {
                        //connection.writeToServer("4 ima ded lol");
                    }
                    connection.closeSocket();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

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
                    try {
                        //connection = new Connection(custom.port.getText(), custom.name.getText(),custom.ip.getText(), this);
                        //connection.openSocket();
                        System.out.println("Opened new socket");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
//                try {
//                    //connection.gameEnded();
//                    connection.openSocket();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
                connection.writeToServer("2 ready");
                connection.playing = true;
                //changeVisibleElems(false);
                table.startGame();
                bStartStop.state = !bStartStop.state;
                //bStartStop.setSize(50, 50);
                bStartStop.setActionCommand("stop_game");
//                connection = new Connection(custom.port.getText(), custom.name.getText(),custom.ip.getText(), this);
//                try {
//                    connection.openSocket();
//                    Thread.sleep(100);
//                } catch (Exception ex) {
//                    return;
//                }
//                //connection.readFromServer();
//                // Начинаем игру.
//                table.setVisible(true);
//                custom.setVisible(false);
//                table.connection = connection;
//                table.startGame();
//                // Изменяем назначение и внешний вид кнопки.
//                //bStartStop.state = !bStartStop.state;
//                bStartStop.setSize(50, 50);
//                bStartStop.setActionCommand("stop_game");
//                bStartStop.state = false;
//                // Запускаем таймер.
//                startTimer = true;
            }
            case "stop_game" -> {
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


    public void showTop(ArrayList<String[]> top) {
        showMessageDialog(this, top, "Top Games", JOptionPane.INFORMATION_MESSAGE);
    }
}

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Connection extends Thread{
    public Socket socket;
    public static boolean running = true;
    private String name;
    private String ipAdress;
    private int port;

    public static boolean disconected = false;
    public static int figIndex = -1;
    public static int maxTime = -1;
    private MainFrame owner;

    /**
     * Конструктор.
     * @param port - порт сервера.
     * @param name - имя игрока.
     * @param ipAdress - адрес сервера.
     * @param owner - главное окно приложения.
     */
    public Connection(String port, String name, String ipAdress, MainFrame owner) {
        this.port = Integer.parseInt(port);
        this.name = name;
        this.owner = owner;
        this.ipAdress = ipAdress;
    }

    /**
     * Метод, поднимающий сокет для связи с сервером.
     * @throws IOException - кидает эксепшены при закрытом сокете.
     */
    public void openSocket() throws IOException {
        // Открываем сокет.
        try {
            socket = new Socket(ipAdress, port);
            socket.setSoTimeout(1000);
        } catch(ConnectException ce) {
            showMessageDialog(null,"Cant connect to server", "Error", ERROR_MESSAGE);
            //owner.table.stopGame();
            owner.table.setVisible(false);
            owner.custom.setVisible(true);

            //owner.bStartStop.doClick();


            //owner.table.stopGame();
            // Изменяем кнопку.
            owner.bStartStop.setActionCommand("start_game");
            owner.bStartStop.state = !owner.bStartStop.state;
            // Отрубаем таймер.
            owner.startTimer = false;
            // Обнуляем количество прошедших секунд.
            owner.elapsedSeconds = 1;
            throw ce;
        }
        // Поток записи на сервер.
        Thread writer = new Thread("Writer") {
            @Override
            public void run() {
                System.out.println("started writing");
                writeToServer("3 "+ name);
                owner.nameLabel.setText(name);
                System.out.println("ended writing");
            }
        };
        writer.start();

        // Поток чтения с сервера.
        Thread reader = new Thread("Reader") {
            @Override
            public void run() {
                System.out.println("started reading");
                // Пока игра идет.
                while(running) {
                    try {
                        // Проверка пульса сокета.
                        if (socket.isClosed()) {
                            break;
                        }
                        // Поток записи на сервер.
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        ArrayList<String> buf = new ArrayList<>();

                        // Крутим пока в буфере есть данные.
                        while(in.ready()) {
                            buf.add(in.readLine());
                        }
                        // Обрабатываем полученные данные.
                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                // Получение новой фигуры.
                                case '0' -> {
                                    figIndex = Integer.parseInt(s.split(" ")[1]);
                                    System.out.println("new fig index " + figIndex);
                                }
                                // Получение новой информации об очках.
                                case '1' -> {
                                    System.out.println("new score " + s.split(" ")[1]);
                                }
                                // Получения нового максимального времени игры.
                                case '2' -> {
                                    maxTime = Integer.parseInt(s.split(" ")[1]);
                                    System.out.println("new max time " + maxTime);
                                }
                                // Получение имени оппонента.
                                case '3' -> {
                                    owner.enemyLabel.setText(s.split(" ")[1]);
                                }
                                // Получение информации об отключении оппонента.
                                case '4' -> {
                                    disconected = true;
                                    MainFrame.won = true;
                                    System.out.println("Enemy disconnected " + disconected);
                                }
                                // Информация о победителе.
                                case '5' -> {
                                    switch (s.split(" ")[1]) {
                                        // Игрок победил.
                                        case "won" -> {
                                            MainFrame.won = true;

                                        }
                                        // Игрок проиграл.
                                        case "lost" -> {
                                            MainFrame.lost = true;
                                        }
                                        // Сервер не закончил свою работу и был закрыт.
                                        case "dead" -> {
                                            owner.table.stopGame();
                                            owner.table.setVisible(false);
                                            owner.custom.setVisible(true);
                                            owner.bStartStop.doClick();
                                            showMessageDialog(null, "Lost connection with server!", "ERROR", ERROR_MESSAGE);
                                            try {
                                                closeSocket();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                // Получение счета оппонента.
                                case '6' -> {
                                    MainFrame.secondScore = Integer.parseInt(s.split(" ")[1]) - 1;
                                    //System.out.println("Second player's score " + MainFrame.secondScore);
                                }
                                default -> {
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
                System.out.println("ended reading");
            }
        };
        reader.start();
    }

    /**
     * Закрытие сокетов.
     * @throws IOException - невозможность закрытия.
     */
    public void closeSocket() throws IOException {
        socket.close();
    }

    /**
     * Метод, посылающий информацию на сервер.
     * @param text - сообщение.
     */
    public void writeToServer(String text) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

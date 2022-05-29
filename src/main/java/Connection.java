import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Connection extends Thread {
    public static Socket socket;

    public boolean running = true;
    public boolean playing = false;

    public String enemy = null;
    public String enemyScore = null;
    private final String name;
    private final String ipAddress;

    private final int port;
    public int figIndex = -1;
    private final MainFrame owner;

    /**
     * Конструктор.
     *
     * @param port      - порт сервера.
     * @param name      - имя игрока.
     * @param ipAddress - адрес сервера.
     * @param owner     - главное окно приложения.
     */
    public Connection(String port, String name, String ipAddress, MainFrame owner) {
        this.port = Integer.parseInt(port);
        this.name = name;
        this.owner = owner;
        this.ipAddress = ipAddress;
    }

    public void gameEnded() {
        writeToServer("3 stopped game");
        playing = false;
        running = false;
        try {
            socket.close();
            socket = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void getTop() {
        if (socket != null) {
            if (!socket.isClosed()) {
                writeToServer("9 give me top");
                return;
            }
        }
        showMessageDialog(null, "Cant connect to server", "Error", ERROR_MESSAGE);
    }

    public void workWithServer() {
        System.out.println("Working with server...");

        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    // Проверка пульса сокета.
                    if (socket.isClosed()) {
                        break;
                    }
                    // Поток записи на сервер.
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ArrayList<String> buf = new ArrayList<>();

                    // Крутим пока в буфере есть данные.
                    while (in.ready()) {
                        buf.add(in.readLine());
                        System.out.println("Pre Game Buffer: " + buf.get(buf.size() - 1));
                    }
                    // Обрабатываем полученные данные.
                    for (String s : buf) {
                        switch (s.charAt(0)) {
                            case '6' -> {
                                System.out.println("Got game time: " + s.split(" ")[1]);
                                owner.maxTime = s.split(" ")[1];
                            }
                            case '5' -> {
                                playing = false;
                                running = false;
                                owner.changeVisibleElems(true);

                                showMessageDialog(owner, "Lost connection with server!", "Eror", ERROR_MESSAGE);
                                try {
                                    closeSocket();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            case '7' -> {
                                enemy = s.split(" ")[1];
                                System.out.println("Got enemy name: " + enemy);
                            }
                            case '9' -> {
                                String rawTop = s.substring(2);
                                if (rawTop.equals("null")) {
                                    showMessageDialog(null, "There are no recordings yet", "Info", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    System.out.println(rawTop);
                                    String[] mediumRareTop = rawTop.split("\b");
                                    new TopDialog(owner, mediumRareTop);
                                }
                                System.out.println("shown top dialog");
                            }
                            default -> {
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }


                while (playing) {
                    try {
                        // Проверка пульса сокета.
                        if (socket.isClosed()) {
                            break;
                        }
                        // Поток записи на сервер.
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        ArrayList<String> buf = new ArrayList<>();

                        // Крутим пока в буфере есть данные.
                        while (in.ready()) {
                            buf.add(in.readLine());
                            System.out.println("Buffer: " + buf.get(buf.size() - 1));
                        }
                        // Обрабатываем полученные данные.
                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                case '0' -> {
                                    figIndex = Integer.parseInt(s.split(" ")[1]);
                                    System.out.println("new fig index " + figIndex);
                                }
                                case '4' -> {
                                    System.out.println("Second player disconnected ");
                                    playing = false;
                                    running = false;
                                    owner.bStartStop.doClick();
                                    showMessageDialog(owner, "Your opponent disconnected\nYOU WON!", "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
                                }
                                case '5' -> {
                                    System.out.println("got game results");
                                    playing = false;
                                    running = false;
                                    owner.bStartStop.doClick();
                                    switch (s.split(" ")[1]) {
                                        // Игрок победил.
                                        case "won" -> {
                                            if (enemy == null) {
                                                showMessageDialog(owner, "YOU WON!\n Your score: " + owner.table.score, "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                showMessageDialog(owner, "YOU WON!\n Your score: " + owner.table.score + "\n" + enemy + "'s score: " + s.split(" ")[2], "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
                                            }
                                        }
                                        // Игрок проиграл.
                                        case "lost" -> showMessageDialog(owner, "YOU LOST!\n Your score: " + owner.table.score + "\n" + enemy + "'s score: " + s.split(" ")[2], "Sorry :(", JOptionPane.WARNING_MESSAGE);
                                        // Сервер не закончил свою работу и был закрыт.
                                        case "dead" -> {
                                            showMessageDialog(owner, "Lost connection with server!", "Error", ERROR_MESSAGE);
                                            try {
                                                closeSocket();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                case '7' -> {
                                    enemy = s.split(" ")[1];
                                    System.out.println("Got enemy name: " + enemy);
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
            }
            System.out.println("Connection lost");
        });
        worker.start();
    }

    public void giveNewFig() {
        writeToServer("0 give new");
    }

    /**
     * Метод, поднимающий сокет для связи с сервером.
     *
     * @throws IOException - кидает эксепшены при закрытом сокете.
     */
    public void openSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(ipAddress, port);
        } else {
            if (socket.getPort() != port) {
                socket.close();
                socket = new Socket(ipAddress, port);
            }
        }
        socket.setSoTimeout(1000);
        writeToServer("-");
        long start = System.currentTimeMillis();
        ArrayList<String> buf = new ArrayList<>();
        while (start + 400 > System.currentTimeMillis()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (in.ready()) {
                buf.add(in.readLine());
            }
        }
        if (buf.size() == 0) {
            throw new ConnectException("Server is not responding to ping-pong packet");
        }

        writeToServer("1 " + name);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writeToServer("5 " + df.getTimeZone().getID());
        playing = false;
        running = true;
        System.out.println("opened socket");

        workWithServer();
    }

    /**
     * Закрытие сокетов.
     *
     * @throws IOException - невозможность закрытия.
     */
    public void closeSocket() throws IOException {
        socket.close();
    }


    /**
     * Метод, посылающий информацию на сервер.
     *
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

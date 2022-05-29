import javax.swing.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Timer;

import static javax.swing.JOptionPane.showMessageDialog;

public class Server extends Thread {
    private final int port;
    private final int timeout = 20;

    MainFrame gui;
    public static boolean running = true;
    public static boolean secondPlayer = false;
    public static ServerSocket serverSocket;

    static Socket firstSocket;
    static Socket secondSocket;

    boolean firstLost = false;
    boolean secondLost = false;

    String firstName = null;
    String secondName = null;

    String firstTimeZone = null;
    String secondTimeZone = null;

    boolean firstReady = false;
    boolean secondReady = false;

    boolean startReceiving = false;

    int firstIndex = 0;
    int secondIndex = 0;

    /**
     * Конструктор.
     *
     * @param port - порт.
     * @param name - имя потока.
     * @param gui  - главное окно приложения.
     */
    public Server(int port, String name, MainFrame gui) {
        super(name);
        this.port = port;
        this.gui = gui;
        running = true;
    }

    /**
     * Переопределенный метод работы потока.
     */
    @Override
    public void run() {


        try {
            // Проверяем открытость и неизменность порта.
            if (serverSocket == null) {
                serverSocket = new ServerSocket(port);
            } else {
                if (serverSocket.getLocalPort() != port) {
                    System.out.println("Old socket closed");
                    serverSocket.close();
                    serverSocket = new ServerSocket(port);
                }
            }
            gui.ip.setText(String.valueOf(InetAddress.getLocalHost()).split("/")[1]);
            System.out.println(serverSocket.getInetAddress());

            // Генерируем массив фигур для передачи игрокам.
            Random random = new Random();
            int[] figs = new int[81];
            for (int i = 0; i < 81; i++) {
                figs[i] = random.nextInt(31);
            }
            for (int i = 0; i < 80; i++) {
                if (figs[i] == figs[i + 1]) {
                    figs[i + 1] = 31 - figs[i];
                }
            }
            System.out.println("\nFigure list was generated");
            System.out.println("Server was created\nWaiting for connection");


            Thread first = new Thread(() -> {
                try {
                    firstSocket = serverSocket.accept();
                    System.out.println(serverSocket.getInetAddress());
                    writeToClient("-");
                    firstLost = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                while (running) {
                    try {
                        // Если второй игрок отключился.
                        if (secondLost) {
                            secondLost = false;
                            writeToClient("4 second player is dead lol");
                        }

                        // Начинаем читать информацию из сокета.
                        BufferedReader in = new BufferedReader(new InputStreamReader(firstSocket.getInputStream()));
                        ArrayList<String> buf = new ArrayList<>();

                        // Получаем всю информацию.
                        while (in.ready()) {
                            System.out.println("[P1] Buffer:");
                            buf.add(in.readLine());
                            System.out.println(buf.get(buf.size() - 1));
                            if (buf.get(buf.size() - 1).equals("-")) {
                                System.out.println("[P1] Got ping-pong");
                                writeToClient("-");
                            }
                        }

                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                case '0' -> {
                                    while (!startReceiving) {
                                        System.out.print("");
                                    }
                                    writeToClient("0 " + figs[firstIndex++]);
                                    System.out.println("[P1] Gave new fig (" + figs[firstIndex - 1] + ")");
                                }
                                case '1' -> {
                                    firstName = s.split(" ")[1];
                                    System.out.println("[P1] Got name (" + firstName + ")");
                                    writeToClient("6 " + gui.time.getText());
                                }

                                case '2' -> {
                                    firstReady = true;
                                    System.out.println("[P1] " + firstName + " ready!");
                                    if (secondPlayer) {
                                        if (secondReady) {
                                            writeToClient("7 " + secondName);
                                            startReceiving = true;
                                        }
                                    } else {
                                        startReceiving = true;
                                    }
                                }
                                case '3' -> {
                                    System.out.println("[P1] " + firstName + " ended game!");
                                    if (secondPlayer) {
                                        writeToClient2("4 first player is dead");
                                    }
                                    firstLost = true;
                                    running = false;
                                }
                                case '5' -> firstTimeZone = s.substring(2);
                                case '9' -> {
                                    writeToClient("9 " + DataBaseConnection.getDataForClient(firstTimeZone));
                                    System.out.println("[P1] Sent top games");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                System.out.println("[P1] Thread ended");
            });
            Thread second = new Thread(() -> {
                try {
                    secondSocket = serverSocket.accept();
                    writeToClient2("-");
                    secondLost = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                while (running) {
                    try {
                        // Если второй игрок отключился.
                        if (firstLost) {
                            firstLost = false;
                            writeToClient2("4 second player is dead lol");
                        }

                        // Начинаем читать информацию из сокета.
                        BufferedReader in = new BufferedReader(new InputStreamReader(secondSocket.getInputStream()));
                        ArrayList<String> buf = new ArrayList<>();

                        // Получаем всю информацию.
                        while (in.ready()) {
                            System.out.println("[P2] Buffer:");
                            buf.add(in.readLine());
                            System.out.println(buf.get(buf.size() - 1));
                            if (buf.get(buf.size() - 1).equals("-")) {
                                System.out.println("[P2] Got ping-pong");
                                writeToClient2("-");
                            }
                        }

                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                case '0' -> {
                                    while (!startReceiving) {
                                        System.out.print("");
                                    }
                                    writeToClient2("0 " + figs[secondIndex++]);
                                    System.out.println("[P2] Gave new fig (" + figs[secondIndex - 1] + ")");
                                }
                                case '1' -> {
                                    secondName = s.split(" ")[1];
                                    System.out.println("[P2] Got name (" + secondName + ")");
                                }
                                case '2' -> {
                                    secondReady = true;
                                    System.out.println("[P2] " + secondName + " ready!");
                                    writeToClient2("6 " + gui.time.getText());
                                    writeToClient2("7 " + firstName);

                                    if (firstReady) {
                                        startReceiving = true;
                                    }
                                }
                                case '3' -> {
                                    System.out.println("[P2] " + secondName + " ended game!");
                                    writeToClient("4 second player is dead");
                                    secondLost = true;
                                    running = false;
                                }
                                case '5' -> secondTimeZone = s.substring(2);
                                case '9' -> {
                                    writeToClient2("9 " + DataBaseConnection.getDataForClient(secondTimeZone));
                                    System.out.println("[P2] Sent top games");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                System.out.println("[P2] Thread ended");
            });

            // Таймер конца игры.
            Timer timer = new Timer("timer");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Time has come");
                    secondIndex--;
                    firstIndex--;
                    if (firstName != null) {
                        if (secondPlayer & secondName != null) {
                            if (secondIndex <= firstIndex) {
                                writeToClient("5 won " + firstIndex + " " + secondIndex);
                                writeToClient2("5 lost " + secondIndex + " " + firstIndex);
                                DataBaseConnection.addNewData(firstName, firstIndex, Integer.parseInt((gui.time.getText())));
                            } else {
                                writeToClient("5 lost " + firstIndex + " " + secondIndex);
                                writeToClient2("5 won " + secondIndex + " " + firstIndex);
                                DataBaseConnection.addNewData(secondName, secondIndex, Integer.parseInt((gui.time.getText())));
                            }
                            running = false;
                            cancel();
                        }
                        if (!secondPlayer) {
                            writeToClient("5 won " + firstIndex + " " + firstIndex);
                            DataBaseConnection.addNewData(firstName, firstIndex, Integer.parseInt((gui.time.getText())));
                            running = false;
                            cancel();
                        }
                    }
                    System.out.println(firstName + " [First] score: " + firstIndex);
                    if (secondPlayer) {
                        System.out.println(secondName + "[Second] score: " + secondIndex);
                    }
                    cancel();
                }
            };
            TimerTask startTimer = new TimerTask() {
                @Override
                public void run() {

                    if (!secondPlayer & firstReady || firstReady & secondReady) {
                        System.out.println("Started game");
                        timer.schedule(task, Integer.parseInt(gui.time.getText()) * 1000L, 100);
                        cancel();
                    }
                }
            };

            // Таймер таймаута сервера.
            TimerTask stopServer = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Stopping server");
                    if (!(!secondPlayer & firstReady || firstReady & secondReady)) {
                        running = false;
                        System.out.println(secondIndex + " " + firstIndex);
                        showMessageDialog(gui, "Server waited for " + timeout + " seconds\nBut game was not started", "Error", JOptionPane.WARNING_MESSAGE);
                        writeToClient("5 dead");
                        if (secondName != null) {
                            writeToClient2("5 dead");
                        }
                    }
                    cancel();
                }
            };

            // Старт потоков игроков.
            first.start();
            if (secondPlayer) {
                System.out.println("Waiting for second player");
                second.start();
            }

            // Старт таймеров.
            Timer timerStop = new Timer("stopServer");
            timerStop.schedule(stopServer, timeout * 1000, 1);
            System.out.println("Started delay");
            timer.schedule(startTimer, 0, 10);

            // Замораживаем поток пока игроки не доиграют.
            while (running) {
                System.out.print("");
            }
            // Убиваем таймер таймаута.
            timerStop.cancel();

            // Обнуляем все поля.
            System.out.println("Server closed");
            firstName = null;
            secondName = null;
            firstLost = false;
            secondLost = false;
            gui.reboot();
            firstSocket = null;
            secondSocket = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Метод закрытия сокетов.
     */
    public static void closeSockets() {
        try {
            secondSocket.close();
            firstSocket.close();
            serverSocket.close();
            System.out.println("Sockets closed successfully");
        } catch (Exception ignored) {
        }
    }

    /**
     * Метод, вызывающийся при отключении сервера. Рассылаем игрокам информацию о смерти сервера.
     */
    public void death() {
        try {
            writeToClient("5 dead");
            if (secondPlayer) {
                writeToClient2("5 dead");
            }
            System.out.println("Sent info about closure to players");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Метод, пишущий информацию в сокет первого игрока.
     *
     * @param text - информация.
     */
    public void writeToClient(String text) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(firstSocket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ignored) {
        }
    }

    /**
     * Метод, пишущий информацию в сокет второго игрока.
     *
     * @param text - информация.
     */
    public void writeToClient2(String text) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(secondSocket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ignored) {
        }
    }
}

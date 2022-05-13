import javax.swing.*;
import java.io.*;
import java.net.*;
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

    boolean sendNames = true;

    int firstIndex = 0;
    int secondIndex = 0;

    /**
     * Конструктор.
     * @param port - порт.
     * @param name - имя потока.
     * @param gui - главное окно приложения.
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

            // Создаем поток первого игрока.
            Thread first = new Thread() {
                @Override
                public void run() {
                    // Нынешняя фигура.
                    int fig = -1;
                    // Имя игрока.
                    String name = "";

                    System.out.println("[P1] Thread started");
                    // Ждем подключение игрока.
                    try {
                        firstSocket = serverSocket.accept();
                        firstSocket.setSoTimeout(1000);
                        System.out.println("[P1] Connection gained");

                        // Высылаем ему первую фигуру.
                        writeToClient("0 " + figs[secondIndex++]);
                        writeToClient("2 " + Integer.parseInt(gui.time.getText()));
                    } catch (IOException ignored) {}

                    // Крутим пока игра идет.
                    while (running) {
                        try {
                            // Если второй игрок отключился.
                            if (secondLost) {
                                secondLost = false;
                                writeToClient("4 second player is dead lol");
                            }
                            // Если оба игрока подключились.
                            if (firstName != null && secondName != null && sendNames) {
                                sendNames = false;
                                writeToClient("3 " + secondName);
                                if (secondPlayer) {
                                    writeToClient2("3 " + firstName);
                                }
                            }

                            // Начинаем читать информацию из сокета.
                            BufferedReader in = new BufferedReader(new InputStreamReader(firstSocket.getInputStream()));
                            ArrayList<String> buf = new ArrayList<>();

                            // Получаем всю информацию.
                            while (in.ready()) {
                                System.out.println("\n[P1] Buffer:");
                                buf.add(in.readLine());
                                System.out.println(buf.get(buf.size() - 1));
                            }

                            for (String s : buf) {
                                switch (s.charAt(0)) {
                                    // Если это сообщение о получении новой фигуры.
                                    case '0' -> {
                                        fig = Integer.parseInt(s.split(" ")[1]);
                                        writeToClient("0 " + figs[secondIndex++]);
                                        writeToClient("1 " + (secondIndex - 1));
                                        System.out.println("[P1] Sent (0" + figs[secondIndex - 1] + ")");
                                    }
                                    // Если это сообщение об имени игрока.
                                    case '3' -> {
                                        name = s.split(" ")[1];
                                        firstName = name;
                                        System.out.println("[P1] Got name (" + name + ")");
                                    }
                                    // Если это сообщение об отключении игрока.
                                    case '4' -> {
                                        firstLost = true;
                                        System.out.println("[P1] Lost connection");
                                    }
                                }
                            }
                            // Отсылаем игроку счет соперника.
                            writeToClient("6 " + firstIndex);
                        } catch (Exception ignored) {}
                    }
                    System.out.println("[P1] Thread ended");
                }
            };

            // Создаем поток второго игрока.
            Thread second = new Thread() {
                @Override
                public void run() {
                    int fig = -1;
                    String name = "";

                    System.out.println("[P2] Thread started");
                    try {
                        secondSocket = serverSocket.accept();
                        secondSocket.setSoTimeout(1000);
                        System.out.println("[P2] Connection gained");

                        writeToClient2("0 " + figs[firstIndex++]);
                        writeToClient2("2 " + Integer.parseInt(gui.time.getText()));
                    } catch (IOException ignored) {
                    }

                    while (running) {
                        try {
                            if (firstLost) {
                                firstLost = false;
                                writeToClient2("4 first player is dead lol");
                            }

                            BufferedReader in = new BufferedReader(new InputStreamReader(secondSocket.getInputStream()));
                            ArrayList<String> buf = new ArrayList<>();

                            while (in.ready()) {
                                System.out.println("\n[P1] Buffer:");
                                buf.add(in.readLine());
                                System.out.println(buf.get(buf.size() - 1));
                            }

                            for (String s : buf) {
                                //System.out.println("[P2] Got " + s);
                                switch (s.charAt(0)) {
                                    case '0' -> {
                                        fig = Integer.parseInt(s.split(" ")[1]);
                                        writeToClient2("0 " + figs[firstIndex++]);
                                        writeToClient2("1 " + (firstIndex - 1));
                                        System.out.println("[P2] Sent (" + figs[firstIndex - 1] + ")");
                                    }
                                    case '3' -> {
                                        name = s.split(" ")[1];
                                        secondName = name;
                                        System.out.println("[P2] Got name (" + name + ")");
                                    }
                                    case '4' -> {
                                        secondLost = true;
                                        System.out.println("[P2] Lost connection");
                                    }
                                }
                            }
                            writeToClient2("6 " + secondIndex);
                        } catch (Exception ignored) {}
                    }
                    System.out.println("[P2] Thread ended");
                }
            };

            // Таймер конца игры.
            Timer timer = new Timer("timer");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Time has come");
                    if (firstName != null) {
                        if (secondPlayer & secondName != null) {
                            if (secondIndex >= firstIndex) {
                                writeToClient("5 won "+ secondIndex + " " + firstIndex);
                                writeToClient2("5 lost "+ firstIndex + " " + secondIndex);
                            } else {
                                writeToClient("5 lost "+ secondIndex + " " + firstIndex);
                                writeToClient2("5 won "+ firstIndex + " " + secondIndex);
                            }
                            running = false;
                            cancel();
                        }
                        if (!secondPlayer) {
                            writeToClient("5 won " + secondIndex + " " + firstIndex);
                            running = false;
                            cancel();
                        }
                    }
                }
            };
            TimerTask startTimer = new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("Started timer");
                    if (firstName != null) {
                        if (!secondPlayer || secondName != null) {
                            timer.schedule(task, Integer.parseInt(gui.time.getText()) * 1000, 100);
                            cancel();
                        }
                    }
                }
            };

            // Таймер таймаута сервера.
            TimerTask stopServer = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Stopping server");
                    if (secondIndex == 0 || (secondPlayer && firstIndex == 0)) {
                        running = false;
                        System.out.println(secondIndex + " " + firstIndex);
                        showMessageDialog(null, "No one connected to server in " + timeout + " seconds\nShutting down", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                    cancel();
                }
            };

            // Старт потоков игроков.
            first.start();
            if (secondPlayer) {
                second.start();
            }

            // Старт таймеров.
            Timer timerStop = new Timer("stopServer");
            timerStop.schedule(stopServer, timeout * 1000, 1);
            System.out.println("Started delay");
            timer.schedule(startTimer, 0, 1);

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
            firstSocket = new Socket();
            secondSocket = new Socket();
            gui.reboot();
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception ignored) {}
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

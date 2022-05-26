import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Connection extends Thread {
    public static Socket socket;
    public static boolean running = true;
    private String name;
    private String ipAdress;
    private int port;

    public static boolean disconected = false;
    public int figIndex = -1;
    public static int maxTime = -1;
    private MainFrame owner;

    public boolean playing = false;


    /**
     * Конструктор.
     *
     * @param port     - порт сервера.
     * @param name     - имя игрока.
     * @param ipAdress - адрес сервера.
     * @param owner    - главное окно приложения.
     */
    public Connection(String port, String name, String ipAdress, MainFrame owner) {
        this.port = Integer.parseInt(port);
        this.name = name;
        this.owner = owner;
        this.ipAdress = ipAdress;

        TimeZone tz = TimeZone.getTimeZone("<local-time-zone>");
        System.out.println(tz);
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

        Thread worker = new Thread() {
            @Override
            public void run() {
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
                            System.out.println("Buffer: " + buf.get(buf.size() - 1));
                        }
                        // Обрабатываем полученные данные.
                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                case '6' -> {
                                    System.out.println("NEED TO CLICK");
                                    owner.bStartStop.state = !owner.bStartStop.state;
                                    //bStartStop.setSize(50, 50);
                                    owner.bStartStop.setActionCommand("stop_game");
                                    owner.connection.playing = true;
                                    //changeVisibleElems(false);
                                    owner.table.startGame();
                                    owner.repaint();
                                    owner.buttonsPane.repaint();
                                    owner.table.repaint();
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
                                                //MainFrame.won = true;
                                                showMessageDialog(owner, "YOU WON!", "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
                                            }
                                            // Игрок проиграл.
                                            case "lost" -> {
                                                //MainFrame.lost = true;
                                                showMessageDialog(owner, "YOU LOST!", "Sorry :(", JOptionPane.WARNING_MESSAGE);
                                            }
                                            // Сервер не закончил свою работу и был закрыт.
                                            case "dead" -> {


                                                showMessageDialog(owner, "Lost connection with server!", "Eror", ERROR_MESSAGE);
                                                try {
                                                    closeSocket();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
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
            }
        };
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
    public void openSocket() throws IOException, InterruptedException {
//        // Открываем сокет.
//        try {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(ipAdress, port);
        } else {
            if (socket.getPort() != port) {
                socket.close();
                socket = new Socket(ipAdress, port);
            }
        }
        //Thread.sleep(200);
        //socket.getOutputStream().flush();
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
//           (new ObjectOutputStream(socket.getOutputStream())).flush();
//            (new FilterOutputStream(socket.getOutputStream())).flush();
//            (new BufferedOutputStream(socket.getOutputStream())).flush();
            writeToServer("awdaw");
            throw new ConnectException("Server is not responding to ping-pong packet");
        }

        writeToServer("1 " + name);
        playing = false;
        running = true;
        System.out.println("opened socket");

        workWithServer();
//        } catch(ConnectException ce) {
//            showMessageDialog(null,"Cant connect to server", "Error", ERROR_MESSAGE);
////            //owner.table.stopGame();
////            owner.table.setVisible(false);
////            owner.custom.setVisible(true);
////
////            //owner.bStartStop.doClick();
////
////
////            //owner.table.stopGame();
////            // Изменяем кнопку.
////            owner.bStartStop.setActionCommand("start_game");
////            owner.bStartStop.state = true;
////            // Отрубаем таймер.
////            owner.startTimer = false;
////            // Обнуляем количество прошедших секунд.
////            owner.elapsedSeconds = 1;
////            throw ce;
//        }
//        // Поток записи на сервер.
//        Thread writer = new Thread("Writer") {
//            @Override
//            public void run() {
//                System.out.println("started writing");
//                writeToServer("3 "+ name);
//                owner.nameLabel.setText("You: " +name);
//                System.out.println("ended writing");
//            }
//        };
//        writer.start();
//
//        // Поток чтения с сервера.
//        Thread reader = new Thread("Reader") {
//            @Override
//            public void run() {
//                System.out.println("started reading");
//                // Пока игра идет.
//                while(running) {
//                    try {
//                        // Проверка пульса сокета.
//                        if (socket.isClosed()) {
//                            break;
//                        }
//                        // Поток записи на сервер.
//                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                        ArrayList<String> buf = new ArrayList<>();
//
//                        // Крутим пока в буфере есть данные.
//                        while(in.ready()) {
//                            buf.add(in.readLine());
//                        }
//                        // Обрабатываем полученные данные.
//                        for (String s : buf) {
//                            switch (s.charAt(0)) {
//                                // Получение новой фигуры.
//                                case '0' -> {
//                                    figIndex = Integer.parseInt(s.split(" ")[1]);
//                                    System.out.println("new fig index " + figIndex);
//                                }
//                                // Получение новой информации об очках.
//                                case '1' -> {
//                                    System.out.println("new score " + s.split(" ")[1]);
//                                }
//                                // Получения нового максимального времени игры.
//                                case '2' -> {
//                                    maxTime = Integer.parseInt(s.split(" ")[1]);
//                                    System.out.println("new max time " + maxTime);
//                                }
//                                // Получение имени оппонента.
//                                case '3' -> {
//                                    owner.enemyLabel.setText("Enemy: "+s.split(" ")[1]);
//                                }
//                                // Получение информации об отключении оппонента.
//                                case '4' -> {
//                                    disconected = true;
//                                    MainFrame.won = true;
//                                    System.out.println("Enemy disconnected " + disconected);
//                                }
//                                // Информация о победителе.
//                                case '5' -> {
//                                    switch (s.split(" ")[1]) {
//                                        // Игрок победил.
//                                        case "won" -> {
//                                            MainFrame.won = true;
//
//                                        }
//                                        // Игрок проиграл.
//                                        case "lost" -> {
//                                            MainFrame.lost = true;
//                                        }
//                                        // Сервер не закончил свою работу и был закрыт.
//                                        case "dead" -> {
//                                            owner.table.stopGame();
//                                            owner.table.setVisible(false);
//                                            owner.custom.setVisible(true);
//                                            owner.bStartStop.doClick();
//                                            showMessageDialog(null, "Lost connection with server!", "ERROR", ERROR_MESSAGE);
//                                            try {
//                                                closeSocket();
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                                // Получение счета оппонента.
//                                case '6' -> {
//                                    MainFrame.secondScore = Integer.parseInt(s.split(" ")[1]) - 1;
//                                    //System.out.println("Second player's score " + MainFrame.secondScore);
//                                }
//                                case '7' -> {
//                                    String rawTop = s.substring(2);
//                                    System.out.println(rawTop);
//                                    String[] mediumRareTop = rawTop.split("\b");
//                                    new TopDialog(owner, mediumRareTop);
//                                    System.out.println("shown top dialog");
//                                }
//                                default -> {
//                                }
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        break;
//                    }
//                }
//                System.out.println("ended reading");
//            }
//        };
//        reader.start();
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

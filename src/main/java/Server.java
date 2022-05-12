import jdk.jshell.execution.Util;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.lang.module.FindException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Consumer;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Server extends Thread {
    private final int port;
    MainFrame gui;
    public static boolean running = true;
    public static boolean secondPlayer = false;

    public Server(int port, String name, MainFrame gui) {
        super(name);
        this.port = port;
        this.gui = gui;
    }

    Socket socket;
    Socket ssocket;

    boolean firstLost = false;
    boolean secondLost = false;
    String firstName = null;
    String secondName = null;
    boolean sendNames = true;

    int index2 = 0;
    int index = 0;

    @Override
    public void run() {
        try {


            Random random = new Random();
            int[] figs = new int[81];
            for (int i = 0; i < 81; i++) {
                figs[i] = random.nextInt(31);
            }
            for (int i = 0; i < 80; i++) {
                if (figs[i] == figs[i + 1]) {
                    figs[i + 1] = 31 - figs[i];
                }
                //System.out.print(figs[i] + " ");
            }
            System.out.println("Figure list was generated");
            //System.out.println(figs[80]);

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server was created\nWaiting for connection");


            Thread first = new Thread() {
                @Override
                public void run() {
                    int fig = -1;
                    String name = "";
                    boolean disconnected = false;

                    System.out.println("[P1] Thread started");
                    try {
                        socket = serverSocket.accept();
                        socket.setSoTimeout(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[P1] Connection gained");

                    writeToClient("0 " + figs[index++]);
                    writeToClient("2 " + 10);

                    while (running) {
                        try {
                            if (secondLost) {
                                secondLost = false;
                                writeToClient("4 second player is dead lol");
                            }
                            if (firstName != null && secondName != null && sendNames) {
                                sendNames = false;
                                writeToClient("3 " + secondName);
                                if (secondPlayer) {
                                    writeToClient2("3 " + firstName);
                                }
                            }
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            ArrayList<String> buf = new ArrayList<>();

                            while (in.ready()) {
                                System.out.println("\n[P1] Buffer:");
                                buf.add(in.readLine());
                                System.out.println(buf.get(buf.size() - 1));
                            }

                            for (String s : buf) {
                                System.out.println("[P1] Got (" + s+")");
                                switch (s.charAt(0)) {
                                    case '0' -> {
                                        fig = Integer.parseInt(s.split(" ")[1]);
                                        writeToClient("0 " + figs[index++]);
                                        writeToClient("1 " + (index - 1));
                                        System.out.println("[P1] Sent (0" + figs[index - 1]+")");
                                    }
                                    case '3' -> {
                                        name = s.split(" ")[1];
                                        firstName = name;
                                        System.out.println("[P1] Got name " + name);
                                    }
                                    case '4' -> {
                                        firstLost = true;
                                        System.out.println("[P1] Lost connection");
                                    }
                                }
                            }


                        } catch (Exception ignored) {
                            //ignored.printStackTrace();
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[P1] Thread ended");
                }
            };

            Thread second = new Thread() {
                @Override
                public void run() {
                    int fig = -1;
                    String name = "";
                    boolean disconnected = false;

                    System.out.println("[P2] Thread started");
                    try {
                        ssocket = serverSocket.accept();
                        ssocket.setSoTimeout(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[P2] Connection gained");


                    writeToClient2("0 " + figs[index2++]);
                    writeToClient2("2 " + 10);

                    while (running) {
                        try {
                            if (firstLost) {
                                firstLost = false;
                                writeToClient2("4 first player is dead lol");
                            }

                            BufferedReader in = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
                            ArrayList<String> buf = new ArrayList<>();

                            while (in.ready()) {
                                buf.add(in.readLine());
                                System.out.println("[P2] Buffer: ("+buf.get(buf.size() - 1) + ")");
                            }

                            for (String s : buf) {
                                System.out.println("[P2] Got " + s);
                                switch (s.charAt(0)) {
                                    case '0' -> {
                                        fig = Integer.parseInt(s.split(" ")[1]);
                                        writeToClient2("0 " + figs[index2++]);
                                        System.out.println("[P2] Sent (" + figs[index2 - 1]+")");
                                    }
                                    case '3' -> {
                                        name = s.split(" ")[1];
                                        secondName = name;
                                        System.out.println("[P2] Got name (" + name+")");
                                    }
                                    case '4' -> {
                                        secondLost = true;
                                        System.out.println("[P2] Lost connection");
                                    }
                                }
                            }


                        } catch (Exception ignored) {
                            //ignored.printStackTrace();
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[P2] Thread ended");
                }
            };

            Timer timer = new Timer("timer");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Delayed");
                    if (firstName != null) {
                        if (secondPlayer & secondName != null) {
                            if (index > index2) {
                                writeToClient("5 won");
                                writeToClient2("5 lost");
                            } else {
                                writeToClient("5 lost");
                                writeToClient2("5 won");
                            }
                            running = false;
                            cancel();
                        }
                        if (!secondPlayer) {
                            writeToClient("5 won");
                            running = false;
                            cancel();
                        }
                    }
                }
            };

            TimerTask startTimer = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Started timer");
                    if (firstName != null) {
                        if (!secondPlayer || secondName != null) {
                            timer.schedule(task,Integer.parseInt(gui.time.getText())*1000,100);
                            cancel();
                        }
                    }
                }
            };
            first.start();
            if (secondPlayer) {
                second.start();
            }
            System.out.println("Started delay");
            timer.schedule(startTimer, 0, 1);
            while (running) {
                System.out.print("");
            }

            System.out.println("Server closed");
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToClient(String text) {
        try {
            socket.setSoTimeout(1000);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String readFromClient() throws IOException {
        try {
            socket.setSoTimeout(1000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return in.readLine();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void writeToClient2(String text) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(ssocket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String readFromClient2() throws IOException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
            return in.readLine();
        } catch (Exception ex) {
            throw ex;
        }
    }
}

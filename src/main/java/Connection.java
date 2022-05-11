import com.sun.tools.javac.Main;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Connection extends Thread{
    private Socket socket;
    public static boolean running = true;
    private String name;
    private int port;


    public static boolean disconected = false;
    public static int figIndex = -1;
    public static int maxTime = -1;
    private MainFrame owner;

    public Connection(String port, String name, MainFrame owner) {
        this.port = Integer.parseInt(port);
        this.name = name;
        this.owner = owner;
    }

    public boolean exist() {
        return socket != null;
    }

    public void openSocket() throws IOException {
        String IPAddress = "localhost";
        try {
            socket = new Socket(IPAddress, port);
            socket.setSoTimeout(1000);
        } catch(ConnectException ce) {
            showMessageDialog(null,"Cant connect to server", "Error", ERROR_MESSAGE);
            owner.dispose();
        }
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
        Thread reader = new Thread("Reader") {
            @Override
            public void run() {
                System.out.println("started reading");
                while(running) {
                    try {
                        //System.out.println("Reading");
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        ArrayList<String> buf = new ArrayList<>();


                        while(in.ready()) {
                            System.out.println("\nBuffer:");
                            buf.add(in.readLine());
                            System.out.println(buf.get(buf.size()-1));
                        }
                        for (String s : buf) {
                            switch (s.charAt(0)) {
                                case '0' -> {
                                    figIndex = Integer.parseInt(s.split(" ")[1]);
                                    System.out.println("new fig index " + figIndex);
                                }
                                case '1' -> {
                                    System.out.println("new score " + s.split(" ")[1]);
                                }
                                case '2' -> {
                                    maxTime = Integer.parseInt(s.split(" ")[1]);
                                    System.out.println("new max time " + maxTime);
                                }
                                case '3' -> {
                                    owner.enemyLabel.setText(s.split(" ")[1]);
                                }
                                case '4' -> {
                                    disconected = true;
                                    MainFrame.won = true;
                                    System.out.println("new disconnection status " + disconected);
                                }
                                case '5' -> {
                                    if (s.split(" ")[1].equals("won")) {
                                        MainFrame.won = true;
                                    } else {
                                        MainFrame.lost = true;
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
                System.out.println("ended reading");
            }
        };
        reader.start();
    }
    public void closeSocket() throws IOException {
        socket.close();
    }

    public void writeToServer(String text) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String readFromServer() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String buf = "";
            while(in.ready()) {
                buf = in.readLine();
                System.out.println(buf);
            }
            if (buf.equals("*")) {
                MainFrame.won = true;
            }
            return buf;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

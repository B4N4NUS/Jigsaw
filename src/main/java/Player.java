//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.Socket;
//import java.util.ArrayList;
//
//public class Player extends Thread{
//
//
//    public Player(int playerNum, Socket socket) {
//
//    }
//
//    @Override
//    public void run() {
//        int fig = -1;
//        String name = "";
//        boolean disconnected = false;
//
//        System.out.println("Thread first started");
//        try {
//            socket = serverSocket.accept();
//            socket.setSoTimeout(1000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("First connection gained");
//
//        writeToClient("0 " + figs[index++]);
//        writeToClient("2 " + 10);
//
//        while (running) {
//            try {
//                if (secondLost) {
//                    secondLost = false;
//                    writeToClient("4 second player is dead lol");
//                }
//                if (firstName != null && secondName != null && sendNames) {
//                    sendNames = false;
//                    writeToClient("3 " + secondName);
//                    if (secondPlayer) {
//                        writeToClient2("3 " + firstName);
//                    }
//                }
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                ArrayList<String> buf = new ArrayList<>();
//
//                while (in.ready()) {
//                    System.out.println("\n1 Buffer:");
//                    buf.add(in.readLine());
//                    System.out.println(buf.get(buf.size() - 1));
//                }
//
//                for (String s : buf) {
//                    System.out.println("1 Got " + s);
//                    switch (s.charAt(0)) {
//                        case '0' -> {
//                            fig = Integer.parseInt(s.split(" ")[1]);
//                            writeToClient("0 " + figs[index++]);
//                            writeToClient("1 " + (index - 1));
//                            System.out.println("1 Sent " + figs[index - 1]);
//                        }
//                        case '3' -> {
//                            name = s.split(" ")[1];
//                            firstName = name;
//                            System.out.println("1 Name " + name);
//                        }
//                        case '4' -> {
//                            firstLost = true;
//                            System.out.println("1 Disconnected");
//                        }
//                    }
//                }
//
//
//            } catch (Exception ignored) {
//                //ignored.printStackTrace();
//            }
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Thread first ended");
//    }
//}

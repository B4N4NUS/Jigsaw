
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;

import static javax.swing.JOptionPane.showMessageDialog;

public class MainFrame extends JFrame {
    static Server server;

    public JTextArea ip, port, time;
    JRadioButton second;
    ButtonWImage start, top;
    // Уже бывавшие в использовании сервера закрытые порты.
    private ArrayList<Integer> usedPorts = new ArrayList<>();

    /**
     * Мейн метод, с которого начинается работа программы.
     *
     * @param args - параметры запуска (не используются)
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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        try {
            setIconImage(ImageIO.read(MainFrame.class.getResource("/icons/black.png")));
        } catch (Exception ignored) {
        }

        setTitle("Jigsaw Server");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();

        JLabel ipLabel, portLabel, timeLabel;

        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.insets = new Insets(5, 10, 5, 5);
        cons.gridx = 1;
        add(ipLabel = new JLabel("IP:   "), cons);

        cons.gridx = 2;
        add(ip = new JTextArea(), cons);
        ip.setText("server is not running");
        ip.setEnabled(false);

        cons.gridx = 1;
        cons.gridy = 1;
        add(portLabel = new JLabel("Port: "), cons);

        cons.gridx = 2;
        add(port = new JTextArea(), cons);
        port.setText("6969");

        port.setPreferredSize(new Dimension(300, 25));
        ip.setPreferredSize(new Dimension(300, 25));

        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridheight = 4;
        top = new ButtonWImage("/icons/top", "/icons/clock");
        top.setPreferredSize(new Dimension(50, 50));
        add(top, cons);
        top.setToolTipText("Get all recordings from database");
        try {
            DataBaseConnection.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        top.addActionListener(e -> {
            System.out.println("Data from derby DB:\n" + DataBaseConnection.getAllData());
        });

        cons.gridx = 0;
        cons.gridy = 3;
        cons.gridheight = 4;
        start = new ButtonWImage("/icons/start", "/icons/clock");
        start.setPreferredSize(new Dimension(50, 50));
        add(start, cons);
        start.setToolTipText("Start server");
        start.addActionListener(e -> {
            // Обработка контента текстовых полей.
            int truePort = -1, trueTime = -1;
            try {
                truePort = Integer.parseInt(port.getText());
            } catch (Exception ex) {
                showMessageDialog(null, "Port must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (truePort < 1) {
                showMessageDialog(null, "Port must be a positive number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                trueTime = Integer.parseInt(time.getText());
            } catch (Exception ex) {
                showMessageDialog(null, "Time must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (trueTime < 1) {
                showMessageDialog(null, "Time must be a positive number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (usedPorts.contains(truePort) && usedPorts.get(usedPorts.size() - 1) != truePort) {
                showMessageDialog(null, "Too late, I've already closed that port\nTry restarting server", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Начало работы серверного потока.
            usedPorts.add(truePort);
            start.state = !start.state;
            start.setEnabled(false);
            time.setEnabled(false);
            port.setEnabled(false);
            second.setEnabled(false);
            server = new Server(Integer.parseInt(port.getText()), "Jigsaw Server", this);
            server.start();
        });

        cons.gridheight = 1;
        cons.gridx = 1;
        cons.gridy = 4;
        second = new JRadioButton("second player");
        add(second, cons);
        second.addActionListener(e -> {
            Server.secondPlayer = second.isSelected();
        });

        cons.gridy = 3;
        time = new JTextArea();
        time.setText("5");
        time.setPreferredSize(new Dimension(300, 25));

        timeLabel = new JLabel("Session time: ");
        add(timeLabel, cons);
        cons.gridx = 2;
        add(time, cons);

        pack();
        setLocationRelativeTo(null);

        try {
            DataBaseConnection.init();
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Cant initialize database");
            top.setEnabled(false);
        }
    }

    /**
     * Метод, возвращающий интерфейс в изначальное состояние.
     */
    public void reboot() {
        start.setEnabled(true);
        start.state = true;
        time.setEnabled(true);
        port.setEnabled(true);
        second.setEnabled(true);
        ip.setText("server is not running");
    }

    /**
     * Переопределенный метод освобождения системных ресурсов.
     */
    @Override
    public void dispose() {
        if (Server.running) {
            Server.running = false;
            if (server != null) {
                server.death();
            }
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }

        super.dispose();
        Server.closeSockets();
        System.exit(0);
    }
}

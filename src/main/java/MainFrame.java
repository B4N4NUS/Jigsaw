import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame extends JFrame{
    JLabel connected = new JLabel();
    static Server server;
    public JTextArea ip,port, time;
    /**
     * Мейн метод, с которого начинается работа программы.
     *
     * @param args - параметры запуска (не используются)
     */
    public static void main(String[] args) {
        // Пытаемся восстановить сохраненные настройки.

            FlatMaterialDarkerContrastIJTheme.setup();
            //ex.printStackTrace();

        // Инициализаруем фрейм с игрой.
        MainFrame frame = new MainFrame();
        frame.Init();
    }



    /**
     * Метод, инициализирующий GUI игры.
     */
    public void Init() {
        try{
        setIconImage(ImageIO.read(MainFrame.class.getResource("/icons/black.png")));
        } catch (Exception ignored) {}

        setTitle("Jigsaw Server");
        //setBounds(100, 100, 1200, 800);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();


        //add(connected);

        JLabel ipLabel, portLabel, timeLabel;

        add(ipLabel  =  new JLabel("IP:   "), cons);

        cons.gridx = 1;
        add(ip = new JTextArea(), cons);
        ip.setText("localhost");

        cons.gridx = 0;
        cons.gridy = 1;
        add(portLabel = new JLabel("Port: "), cons);

        cons.gridx = 1;
        add(port = new JTextArea(), cons);
        port.setText("6969");

        port.setPreferredSize(new Dimension(300,25));
        ip.setPreferredSize(new Dimension(300,25));
        //setSize(new Dimension(300,300));

        cons.gridx = 0;
        cons.gridy = 2;
        cons.gridwidth = 1;
        JButton start = new JButton("start server");
        add(start, cons);
        start.addActionListener(e-> {
            server = new Server(Integer.parseInt(port.getText()), "Jigsaw Server", this);
            server.start();
        });
        cons.gridx = 1;
        cons.gridy = 2;
        cons.gridwidth = 1;
        JButton stop = new JButton("stop server");
        add(stop, cons);
        stop.addActionListener(e-> {
            Server.running = false;
        });

        cons.gridy = 3;
        JRadioButton second = new JRadioButton("second player");
        add(second, cons);
        second.addActionListener(e-> {
            Server.secondPlayer = second.isSelected();
        });

        cons.gridy = 4;
        cons.gridx = 0;
        time = new JTextArea();
        timeLabel = new JLabel("Session time: ");
        add(timeLabel, cons);
        cons.gridx = 1;
        add(time, cons);

        pack();
    }

    @Override
    public void dispose() {
        Server.running = false;
        super.dispose();
        System.exit(0);
        // Отключаем таймеры.
//        task.cancel();
//        update.cancel();
        // Пытаемся сохранить настройки.
        // На всякий случай выходим из приложения таким образом, во избежание проблем с закрытием потоков.
        //System.exit(0);
    }
}

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

        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.insets = new Insets(5,10,5,5);
        cons.gridx = 1;
        add(ipLabel  =  new JLabel("IP:   "), cons);

        cons.gridx = 2;
        add(ip = new JTextArea(), cons);
        ip.setText("localhost");

        cons.gridx = 1;
        cons.gridy = 1;
        add(portLabel = new JLabel("Port: "), cons);

        cons.gridx = 2;
        add(port = new JTextArea(), cons);
        port.setText("6969");

        port.setPreferredSize(new Dimension(300,25));
        ip.setPreferredSize(new Dimension(300,25));
        //setSize(new Dimension(300,300));

        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridheight = 4;
        ButtonWImage start = new ButtonWImage("/icons/start","/icons/clock");
        start.setPreferredSize(new Dimension(50,50));
        add(start, cons);
        start.addActionListener(e-> {
            start.state = !start.state;
            //start.repaint();
            server = new Server(Integer.parseInt(port.getText()), "Jigsaw Server", this);
            server.run();
        });

        cons.gridheight = 1;
        cons.gridx = 1;
        cons.gridy = 4;
        JRadioButton second = new JRadioButton("second player");
        add(second, cons);
        second.addActionListener(e-> {
            Server.secondPlayer = second.isSelected();
        });

        cons.gridy = 3;
        time = new JTextArea();
        time.setText("10");
        time.setPreferredSize(new Dimension(300,25));
        timeLabel = new JLabel("Session time: ");
        add(timeLabel, cons);
        cons.gridx = 2;
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

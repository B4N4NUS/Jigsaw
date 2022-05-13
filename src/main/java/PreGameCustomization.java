import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class PreGameCustomization extends JPanel {
    MainFrame gui;
    static public JTextArea name, port, ip;

    /**
     * Конструктор
     * @param gui - главное окно приложения.
     */
    public PreGameCustomization(MainFrame gui) {
        this.gui = gui;
        Init();
    }

    /**
     * Метод отрисовки компонентов.
     */
    private void Init() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.insets = new Insets(5, 10, 5, 5);
        cons.anchor = GridBagConstraints.CENTER;

        //add(connected);

        JLabel nameLabel, portLabel, ipLabel;

        add(nameLabel = new JLabel("Name: "), cons);

        cons.gridx = 1;
        name = new JTextArea();
        add(name, cons);
        Random random = new Random();
        name.setText((char) (random.nextInt(25)+65) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97));

        cons.gridx = 0;
        cons.gridy = 1;
        add(ipLabel = new JLabel("IP:   "), cons);

        cons.gridx = 1;
        ip = new JTextArea();
        add(ip, cons);
        ip.setText("localhost");

        cons.gridx = 0;
        cons.gridy = 2;
        add(portLabel = new JLabel("Port: "), cons);

        cons.gridx = 1;
        port = new JTextArea();
        add(port, cons);
        port.setText("6969");


        ip.setPreferredSize(new Dimension(300, 25));
        port.setPreferredSize(new Dimension(300, 25));
        name.setPreferredSize(new Dimension(300, 25));
    }

}

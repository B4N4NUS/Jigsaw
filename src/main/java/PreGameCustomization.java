import javax.swing.*;
import java.awt.*;
import java.net.ConnectException;
import java.util.Random;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Класс, являющийся панелью с заполняемыми полями для подключения к серверу.
 */
public class PreGameCustomization extends JPanel {
    MainFrame gui;
    public JTextArea name, port, ip;

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
        cons.anchor = GridBagConstraints.SOUTH;
        cons.fill = GridBagConstraints.BOTH;

        JButton connect;

        add(new JLabel("Name: "), cons);

        cons.gridx = 1;

        name = new JTextArea();
        add(name, cons);
        Random random = new Random();
        name.setText((char) (random.nextInt(25)+65) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97));

        cons.gridx = 0;
        cons.gridy = 1;
        cons.weightx = 0;
        add(new JLabel("IP:   "), cons);

        cons.gridx = 1;
        cons.weightx = 1;
        ip = new JTextArea();
        add(ip, cons);
        ip.setText("localhost");

        cons.gridx = 0;
        cons.gridy = 2;
        cons.weightx = 0;
        add(new JLabel("Port: "), cons);

        cons.gridx = 1;
        cons.weightx = 1;
        port = new JTextArea();
        add(port, cons);
        port.setText("6969");

        cons.gridx = 2;
        cons.gridy = 0;
        cons.gridheight = 3;
        cons.weightx = 0;
        cons.fill = GridBagConstraints.NONE;
        add(connect = new ButtonWImage("icons/server", "icons/server"), cons);
        connect.setToolTipText("Connect to server");
        connect.setPreferredSize(new Dimension(75,75));
        connect.addActionListener(e-> {
            try {
                // Пробуем открыть сокет.
                gui.connection = new Connection(port.getText(), name.getText(),ip.getText(), gui);
                gui.connection.openSocket();
                gui.changeVisibleElems(false);
            }catch (ConnectException ex) {
                // Если не удается подключиться, оповещаем пользователя.
                showMessageDialog(null, "Cant connect to server", "Error", ERROR_MESSAGE);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}

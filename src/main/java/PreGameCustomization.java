import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Random;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

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
        cons.anchor = GridBagConstraints.SOUTH;
        cons.fill = GridBagConstraints.BOTH;
        //add(connected);

        JLabel nameLabel, portLabel, ipLabel;
        JButton connect;

        add(nameLabel = new JLabel("Name: "), cons);

        cons.gridx = 1;

        name = new JTextArea();
        add(name, cons);
        Random random = new Random();
        name.setText((char) (random.nextInt(25)+65) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97) + "" + (char) (random.nextInt(25)+97));

        cons.gridx = 0;
        cons.gridy = 1;
        cons.weightx = 0;
        add(ipLabel = new JLabel("IP:   "), cons);

        cons.gridx = 1;
        cons.weightx = 1;
        ip = new JTextArea();
        add(ip, cons);
        ip.setText("localhost");

        cons.gridx = 0;
        cons.gridy = 2;
        cons.weightx = 0;
        add(portLabel = new JLabel("Port: "), cons);

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
                gui.connection = new Connection(port.getText(), name.getText(),ip.getText(), gui);
                gui.connection.openSocket();
                gui.changeVisibleElems(false);
            }catch (ConnectException ex) {
                showMessageDialog(null, "Cant connect to server", "Error", ERROR_MESSAGE);
                //ex.printStackTrace();
//                try {
//                    gui.connection.socket.getOutputStream().flush();
//                } catch (IOException exc) {
//                    exc.printStackTrace();
//                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        //setBackground(Color.red);


        //ip.setPreferredSize(new Dimension(300, 25));
        //port.setPreferredSize(new Dimension(300, 25));
        //name.setPreferredSize(new Dimension(300, 25));
    }

}

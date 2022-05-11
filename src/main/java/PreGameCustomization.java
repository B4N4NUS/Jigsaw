import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class PreGameCustomization extends JPanel {
    MainFrame gui;

    public PreGameCustomization(MainFrame gui) {
        this.gui = gui;
        Init();
    }

    static public JTextArea name,port;

    private void Init() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();


        //add(connected);

        JLabel nameLabel, portLabel;

        add(nameLabel  =  new JLabel("Name: "), cons);

        cons.gridx = 1;
        add(name = new JTextArea(), cons);
        Random random = new Random();
        name.setText((char)random.nextInt() + "" + (char)random.nextInt() + "" + (char)random.nextInt() + "" + (char)random.nextInt() );

        cons.gridx = 0;
        cons.gridy = 1;
        add(portLabel = new JLabel("Port: "), cons);

        cons.gridx = 1;
        add(port = new JTextArea(), cons);
        port.setText("6969");

        port.setPreferredSize(new Dimension(300,25));
        name.setPreferredSize(new Dimension(300,25));
    }

}

import javax.swing.*;
import java.awt.*;

/**
 * Старший брат класса TopDialog из клиентской части проекта.
 */
public class DataBasePlayersList extends JDialog {
    MainFrame owner;
    String[] data;

    public DataBasePlayersList(MainFrame owner, String[] data) {
        super(owner, true);
        this.owner = owner;
        this.data = data;
        build();
    }

    public void build() {
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        setTitle("Top of all " + data.length + " games");

        JLabel header;
        cons.gridy = 0;
        cons.gridx = 0;
        cons.anchor = GridBagConstraints.CENTER;
        cons.insets = new Insets(5,5,5,5);
        add(header = new JLabel("Place"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 1;
        add(header = new JLabel("ID"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 2;
        add(header = new JLabel("Login"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 3;
        add(header = new JLabel("Date"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 4;
        add(header = new JLabel("Score"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 5;
        add(header = new JLabel("Duration"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 0;

        for(int i = 0; i < data.length; i++) {
            String[] temp = data[i].split("\t");
            cons.gridy = i+1;
            cons.gridx = 0;
            add(new JLabel((i+1)+""), cons);
            for(int j = 0; j < 5; j++) {
                cons.gridx = j+1;
                JLabel tempoLab;
                add(tempoLab = new JLabel(temp[j]),cons);
                tempoLab.setFont(new Font("Verdana", Font.PLAIN, 15));
            }
        }
        pack();
        setVisible(true);
    }
}

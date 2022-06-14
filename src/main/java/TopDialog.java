import javax.swing.*;
import java.awt.*;

/**
 * Класс диалога с топом игроков, полученных из БД.
 */
public class TopDialog extends JDialog {
    MainFrame owner;
    String[] top;

    public TopDialog(MainFrame owner, String[] top) {
        super(owner, true);
        this.owner = owner;
        this.top = top;
        build();
    }

    public void build() {
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        setTitle("Top " + top.length + " Games");

        JLabel header;
        cons.gridy = 0;
        cons.gridx = 0;
        cons.anchor = GridBagConstraints.CENTER;
        cons.insets = new Insets(5,5,5,5);
        add(header = new JLabel("Place"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 1;
        add(header = new JLabel("Login"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 2;
        add(header = new JLabel("Date"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 3;
        add(header = new JLabel("Score"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 4;
        add(header = new JLabel("Duration"), cons);
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        cons.gridx = 0;

        for(int i = 0; i < top.length; i++) {
            String[] temp = top[i].split("\\|");
            cons.gridy = i+1;
            cons.gridx = 0;
            add(new JLabel((i+1)+""), cons);
            for(int j = 0; j < 4; j++) {
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

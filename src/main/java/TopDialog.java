import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
        setLayout(new BoxLayout(getContentPane(), 1));
        setTitle("Top "+top.length + " Games");
        JLabel header = new JLabel(" Place |   Login   |              Date               | Score | Duration ");
        System.out.println(header.getText().length());
        JLabel text;
        header.setFont(new Font("Verdana", Font.BOLD, 15));
        add(header);
        for (int i = 0; i < top.length; i++) {
            if (i == 9) {
                text = new JLabel(String.format("%8s",i+1) + " | " + top[i]);
            } else {
                text = new JLabel(String.format("%9s", i + 1) + " | " + top[i]);
            }
            text.setFont(new Font("Verdana", Font.PLAIN, 15));
            add(text);
        }
        pack();
        setVisible(true);

    }
}

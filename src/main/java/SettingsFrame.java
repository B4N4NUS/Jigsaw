
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Класс диалога с настройками.
 */
public class SettingsFrame extends JDialog {


    /**
     * Конструктор с инициализацией GUI.
     *
     * @param owner - фрейм, что вызвал диалог.
     */
    public SettingsFrame(JFrame owner) {
        super(owner, true);
        Init();

    }

    /**
     * Метод инициализации GUI.
     */
    public void Init() {
        setTitle("Themes");
        setBounds(0, 0, 450, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(450, 400));
        setMaximumSize(new Dimension(451, 401));


        pack();
    }


    private void themesListValueChanged(ListSelectionEvent e) {

    }





    @Override
    public void dispose() {
        super.dispose();
    }
}

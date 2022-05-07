import javax.swing.*;
import java.awt.*;

/**
 * Класс клетки игрового поля и фигур.
 */
public class Cell extends JButton {
    public Point position;

    /**
     * Конструктор инициализации клетки.
     */
    public Cell() {
        setText(" ");
        // Делаем кнопку неактивной.
        setEnabled(false);
        setBounds(new Rectangle(25,25));
        // Убираем фокусировку при наведении курсора.
        setFocusPainted(false);
        setFocusable(false);
        setRolloverEnabled(false);
        // Выставляем цвет.
        setBackground(Table.color);
    }
}

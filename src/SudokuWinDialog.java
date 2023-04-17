import javax.swing.*;
import java.awt.*;

public class SudokuWinDialog extends JDialog {

    private JLabel time;
    public SudokuWinDialog(Window panel, boolean modal, String time) {
        super((Frame) panel, "Solved!", modal);

        setLayout(new GridBagLayout());

        this.time = new JLabel("<html><div style='text-align:center;'>Congratulations! <br>You solved the sudoku in: %s</div></html>".formatted(time));
        add(this.time);

        setSize(400, 150);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}

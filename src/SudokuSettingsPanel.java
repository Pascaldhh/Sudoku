import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SudokuSettingsPanel extends JPanel {
    public static SudokuSettingsPanel sudokuSettingsPanel;
    private JLabel title;
    protected JButton btnStart;
    protected JButton btnReset;
    protected JPanel timerPanel;
    protected JLabel labelTime;
    public SudokuSettingsPanel() {
        SudokuSettingsPanel.sudokuSettingsPanel = this;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 25, 0, 0));

        JPanel heading = new JPanel();
        heading.setLayout(new GridLayout(0, 1));

        this.title = new JLabel("Sudoku");
        this.title.setFont(new Font("", Font.BOLD, 30));
        this.title.setHorizontalAlignment(SwingConstants.CENTER);
        heading.add(this.title);

        this.timerPanel = new JPanel();
        this.labelTime = new JLabel("00:00");
        this.timerPanel.add(this.labelTime);
        heading.add(this.timerPanel);
        add(heading, BorderLayout.PAGE_START);

        JPanel btnGroup = new JPanel();
        btnGroup.setFocusable(false);
        btnGroup.setLayout(new GridLayout(0, 1));

        this.btnStart = new JButton("play");
        this.btnStart.setFocusable(false);
        this.btnStart.setPreferredSize(new Dimension(75, 50));
        btnGroup.add(this.btnStart);

        this.btnReset = new JButton("reset");
        this.btnReset.setFocusable(false);
        btnGroup.add(this.btnReset);

        add(btnGroup, BorderLayout.PAGE_END);

        setVisible(true);
    }
}

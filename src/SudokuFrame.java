import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuFrame extends JFrame implements ActionListener {
    private SudokuPanel sudokuPanel;
    private SudokuSettingsPanel settingsPanel;
    public SudokuFrame() {
        this.sudokuPanel = new SudokuPanel(new Sudoku(3));
        this.settingsPanel = new SudokuSettingsPanel();
        this.settingsPanel.setPreferredSize(new Dimension(200, 500));
        this.settingsPanel.btnStart.addActionListener(this);
        this.settingsPanel.btnReset.addActionListener(this);

        setTitle("Sudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLayout(new GridBagLayout());

        add(this.sudokuPanel);
        add(this.settingsPanel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == this.settingsPanel.btnStart && !this.sudokuPanel.getSudoku().isRunning()) this.sudokuPanel.startSudoku();
        else if(e.getSource() == this.settingsPanel.btnReset) this.sudokuPanel.resetSudoku();
    }
}

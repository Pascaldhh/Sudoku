import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SudokuPanel extends JPanel implements MouseListener, KeyListener {
    private Sudoku sudoku;
    private Color resetBackground;
    private Color gameBackground;
    public SudokuPanel(Sudoku sudoku) {
        this.sudoku = sudoku;
        this.resetBackground = getBackground();
        this.gameBackground = Color.WHITE;

        setPreferredSize(new Dimension(500, 500));
        setVisible(true);
    }

    private void setSudoku(Sudoku sudoku) {
        this.sudoku = sudoku;
    }

    public Sudoku getSudoku() {
        return sudoku;
    }

    public void startSudoku() {
        this.sudoku.stopGame();
        setBackground(this.gameBackground);
        this.setSudoku(new Sudoku(3));
        this.sudoku.startGame();
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        repaint();
    }

    public void resetSudoku() {
        this.sudoku.stopGame();
        setBackground(this.resetBackground);
        this.setSudoku(new Sudoku(3));
        removeMouseListener(this);
        removeKeyListener(this);
        setFocusable(false);
        repaint();
    }

    public void wonSudoku() {
        if(this.sudoku.hasWon() && this.sudoku.isRunning()) {
            this.sudoku.wonGame(SwingUtilities.windowForComponent(this));
            setBackground(this.resetBackground);
            removeMouseListener(this);
            removeKeyListener(this);
            setFocusable(false);
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.sudoku.generateSudoku(this, (Graphics2D) g);
        this.wonSudoku();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.sudoku.setActive(e.getX(), e.getY());
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) this.sudoku.setValue('0');

        if(e.getKeyCode() == KeyEvent.VK_UP) this.sudoku.setActive("up");
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) this.sudoku.setActive("down");
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) this.sudoku.setActive("left");
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) this.sudoku.setActive("right");

        this.sudoku.setValue(e.getKeyChar());
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.stream.Collectors;

public class Sudoku {
    private int size;
    private final BasicStroke thicknessBigRect;
    private final BasicStroke thicknessSmallRect;
    private final ArrayList<Rectangle2D> smallRects;
    private Rectangle2D activeRect;
    private final Color activeColor;
    private final Color wrongColor;
    private int activeIndex;
    private final Timer timer;
    private int seconds;

    private int difficulty;

    private boolean running;

    private final int[] valueFields;
    private final boolean[] startFields;

    public Sudoku(int size) {
        this.thicknessBigRect = new BasicStroke(3);
        this.thicknessSmallRect = new BasicStroke(2);

        this.activeColor = new Color(187,222,251);
        this.wrongColor = Color.RED;

        this.smallRects = new ArrayList<>();

        this.timer = new Timer(1000, e -> {
            this.seconds++;
            int minutes = this.seconds / 60;
            int seconds = this.seconds % 60;
            SudokuSettingsPanel.sudokuSettingsPanel.labelTime.setText(String.format("%02d:%02d", minutes, seconds));
            SudokuSettingsPanel.sudokuSettingsPanel.repaint();
        });

        this.setSize(size);

        this.valueFields = new int[(this.size * this.size) * (this.size * this.size)];
        this.startFields = new boolean[(this.size * this.size) * (this.size * this.size)];
        this.difficulty = 80;
    }

    public void startGame() {
        this.running = true;
        this.startTimer();
        this.generateValues();
    }

    public void stopGame() {
        this.running = false;
        this.stopTimer();
        SudokuSettingsPanel.sudokuSettingsPanel.labelTime.setText(String.format("%02d:%02d", 0, 0));
    }

    public void wonGame(Window panel) {
        this.activeIndex = 0;
        this.activeRect = null;
        this.running = false;
        this.stopTimer();
        
        int minutes = this.seconds / 60;
        int seconds = this.seconds % 60;
        SudokuWinDialog dialog = new SudokuWinDialog(panel, true, String.format("%02d:%02d", minutes, seconds));
    }

    public boolean isRunning() {
        return running;
    }

    public void generateSudoku(SudokuPanel panel, Graphics2D g) {
        this.drawBigRects(g, panel);
        this.drawSmallRects(g, panel);
        this.drawActiveRect(g);
        this.drawValues(g, panel);
    }

    public boolean generateValues() {
        for (int i = 0; i < this.valueFields.length; i++) {
            if (this.valueFields[i] == 0) {
                ArrayList<Integer> availableNums = this.getAvailableNums(i);
                if (availableNums.isEmpty()) return false;
                for (int j = 0; j < availableNums.size(); j++) {
                    this.valueFields[i] = availableNums.get(new Random().nextInt(availableNums.size()));
                    if (this.generateValues()) return true;
                    this.valueFields[i] = 0;
                }
                return false;
            }
        }

        for(int i = 0; i < this.difficulty; i++) {
            this.valueFields[new Random().nextInt(this.valueFields.length)] = 0;
        }

        for(int i = 0; i < this.valueFields.length; i++) {
            if(this.valueFields[i] != 0) this.startFields[i] = true;
        }

        return true;
    }

    private ArrayList<Integer> getAvailableNums(int index) {
        int row = index / 9;
        int col = index % 9;

        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<Integer> availableNums = new ArrayList<>();

        for(int i = 1; i <= this.size * this.size; i++) {
            availableNums.add(i);
        }

        // horizontal
        for(int i = 0; i < this.size * this.size; i++) {
            numbers.add(this.valueFields[row * (this.size * this.size) + i]);
        }

        // vertical
        for(int i = 0; i < this.size * this.size; i++) {
            numbers.add(this.valueFields[col + (i * (this.size * this.size))]);
        }

        // square
        int rowStart = (row / 3) * 3;
        int colStart = (col / 3) * 3;
        int oldStart = colStart;

        for(int i = 0; i < this.size * this.size; i++) {
            if(i % this.size == 0 && i != 0) {
                colStart = oldStart;
                rowStart++;
            }
            numbers.add(this.valueFields[rowStart * (this.size * this.size) + colStart]);
            colStart++;
        }

        return availableNums.stream().filter(num -> !numbers.contains(num)).collect(Collectors.toCollection(ArrayList::new));
    }

    private void drawBigRects(Graphics2D g, SudokuPanel panel) {
        Stroke oldstroke = g.getStroke();
        g.setStroke(this.thicknessBigRect);
        g.setColor(Color.BLACK);

        double x = 0;
        double y = 0;

        double width = (double) panel.getWidth() / this.size;
        double height = (double) panel.getHeight() / this.size;

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                Rectangle2D r2d = new Rectangle2D.Double(x, y, width, height);
                g.draw(r2d);
                x += width;
            }
            x = 0;
            y += height;
        }

        g.setStroke(oldstroke);
    }

    private void drawSmallRects(Graphics2D g, SudokuPanel panel) {
        Stroke oldstroke = g.getStroke();
        g.setStroke(this.thicknessSmallRect);
        g.setColor(Color.BLACK);

        double x = 0;
        double y = 0;

        double width = (double) (panel.getWidth() / this.size) / this.size;
        double height = (double) (panel.getHeight() / this.size) / this.size;

        for(int i = 0; i < this.size * this.size; i++) {
            for(int j = 0; j < this.size * this.size; j++) {
                Rectangle2D r2d = new Rectangle2D.Double(x, y, width, height);
                this.smallRects.add(r2d);
                g.draw(r2d);
                x += width;
            }
            y += height;
            x = 0;
        }

        g.setStroke(oldstroke);
    }

    public void setActive(double x, double y) {
        this.activeRect = this.smallRects.stream()
                .filter(rect -> (rect.getX() < x && rect.getX() + rect.getWidth() > x) && (rect.getY() < y && rect.getY() + rect.getHeight() > y))
                .toList().get(0);
        this.activeIndex = this.smallRects.indexOf(this.activeRect);
    }

    private void setActive(int index) {
        if(index < 0 || index > this.valueFields.length) return;

        this.activeRect = this.smallRects.get(index);
        this.activeIndex = index;
    }

    public void setActive(String direction) {
        if(this.activeRect == null) {
            this.setActive(0);
            return;
        }

        if(direction.contains("up")) {
            if((this.activeIndex - this.size * this.size) < 0) this.setActive(this.valueFields.length - (this.size * this.size) + this.activeIndex);
            else this.setActive(this.activeIndex - this.size * this.size);
        }

        else if(direction.contains("down")) {
            if((this.activeIndex + this.size * this.size) > this.valueFields.length-1) this.setActive((this.size * this.size) - (this.valueFields.length - this.activeIndex));
            else this.setActive(this.activeIndex + this.size * this.size);
        }

        else if(direction.contains("right")) {
            if(this.activeIndex % (this.size * this.size) == (this.size * this.size -1)) this.setActive(this.activeIndex - (this.size * this.size - 1));
            else this.setActive(this.activeIndex + 1);
        }

        else if(direction.contains("left")) {
            if(this.activeIndex % (this.size * this.size) == 0) this.setActive(this.activeIndex + (this.size * this.size - 1));
            else this.setActive(this.activeIndex - 1);
        }
    }

    private void drawActiveRect(Graphics2D g) {
        if(this.activeRect == null) return;

        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();
        g.setStroke(new BasicStroke(3));

        g.setColor(this.activeColor);
        g.fill(this.activeRect);

        g.setColor(oldColor);
        g.setStroke(oldStroke);
    }

    private void drawValues(Graphics2D g, SudokuPanel panel) {
        g.setFont(new Font("Verdana", Font.PLAIN, 23));
        FontMetrics metrics = g.getFontMetrics(g.getFont());

        double width = (double) (panel.getWidth() / this.size) / this.size;
        double height = (double) (panel.getHeight() / this.size) / this.size;

        double x = width / 2;
        double y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        int index = 0;

        ArrayList<Integer> duplicatedIndexes = this.sudokuLogic();

        for(int i = 0; i < this.size * this.size; i++) {
            for(int j = 0; j < this.size * this.size; j++) {
                if(duplicatedIndexes.contains(index)) g.setColor(this.wrongColor);
                else if(this.startFields[index]) g.setColor(Color.BLACK);
                else g.setColor(Color.BLUE);

                if(!(this.valueFields[index] == 0)) g.drawString(String.valueOf(this.valueFields[index]), (int) x - (metrics.stringWidth(String.valueOf(this.valueFields[index])) / 2), (int) y);


                x += width;
                index++;
            }
            x = width / 2;
            y += height;
        }

    }

    public void setValue(char value) {
        if(!Character.isDigit(value) || this.startFields[this.activeIndex]) return;
        this.valueFields[this.activeIndex] = Integer.parseInt(String.valueOf(value));
    }

    private ArrayList<Integer> sudokuLogic() {
        ArrayList<Integer> allDuplicates = new ArrayList<>();

        ArrayList<Integer> horizontalDuplicates = this.checkHorizontal();
        allDuplicates.addAll(horizontalDuplicates);

        ArrayList<Integer> verticalDuplicates = this.checkVertical();
        allDuplicates.addAll(verticalDuplicates);

        ArrayList<Integer> squareDuplicates = this.checkSquare();
        allDuplicates.addAll(squareDuplicates);

        return allDuplicates;
    }

    public boolean hasWon() {
        return this.sudokuLogic().isEmpty() && Arrays.stream(this.valueFields).allMatch(num -> num != 0);
    }

    private ArrayList<Integer> checkHorizontal() {
        int startIndex = 0;
        int endIndex = this.size * this.size;

        ArrayList<Integer> duplicatedIndexes = new ArrayList<>();

        for(int i = 0; i < this.size * this.size; i++) {
            int[] rowArray = Arrays.copyOfRange(this.valueFields, startIndex, endIndex);
            ArrayList<Integer> arrayList = new ArrayList<>();
            ArrayList<Integer> duplicatedNumbers = new ArrayList<>();

            for (int num : rowArray) {
                if (num == 0) continue;

                if (arrayList.contains(num)) duplicatedNumbers.add(num);
                else arrayList.add(num);
            }

            for(int j = 0; j < rowArray.length; j++) if(duplicatedNumbers.contains(rowArray[j])) duplicatedIndexes.add(j+startIndex);

            startIndex += this.size * this.size;
            endIndex += this.size * this.size;
        }

        return duplicatedIndexes;
    }

    private ArrayList<Integer> checkVertical() {
        ArrayList<Integer> duplicatedIndexes = new ArrayList<>();

        for(int i = 0; i < this.size * this.size; i++) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            ArrayList<Integer> duplicatedNumbers = new ArrayList<>();

            for(int j = i; j < this.valueFields.length; j += this.size * this.size) {
                if (this.valueFields[j] == 0) continue;

                if (arrayList.contains(this.valueFields[j])) duplicatedNumbers.add(this.valueFields[j]);
                else arrayList.add(this.valueFields[j]);
            }
            for(int j = i; j < this.valueFields.length; j += this.size * this.size) if(duplicatedNumbers.contains(this.valueFields[j])) duplicatedIndexes.add(j);
        }

        return duplicatedIndexes;
    }

    private ArrayList<Integer> checkSquare() {
        ArrayList<Integer> duplicatedIndexes = new ArrayList<>();

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                ArrayList<Integer> arrayList = new ArrayList<>();
                ArrayList<Integer> duplicatedNumbers = new ArrayList<>();

                for(int o = 0; o < this.size; o++) {
                    for (int k = 0; k < this.size; k++) {
                        int index = k + (o * (this.size * this.size)) + (j * this.size) + (i * (this.size * (this.size * this.size)));
                        if (this.valueFields[index] == 0) continue;
                        if (arrayList.contains(this.valueFields[index])) duplicatedNumbers.add(this.valueFields[index]);
                        else arrayList.add(this.valueFields[index]);
                    }
                }

                for(int o = 0; o < this.size; o++) {
                    for (int k = 0; k < this.size; k++) {
                        int index = k + (o * (this.size * this.size)) + (j * this.size) + (i * (this.size * (this.size * this.size)));
                        if(duplicatedNumbers.contains(this.valueFields[index])) duplicatedIndexes.add(index);
                    }
                }
            }
        }

        return duplicatedIndexes;
    }

    public void startTimer() {
        this.timer.start();
    }

    public void stopTimer() {
        this.timer.stop();
    }

    private void setSize(int size) {
        this.size = size;
    }
}

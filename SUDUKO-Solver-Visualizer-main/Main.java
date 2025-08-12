import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int CELL_SIZE = 60;
    private static final int BOARD_SIZE = CELL_SIZE * SIZE;

    private int[][] board = new int[SIZE][SIZE];
    private boolean[][] initial = new boolean[SIZE][SIZE];
    private Random random = new Random();

    public Main() {
        setTitle("Sudoku Solver Visualizer");
        setSize(BOARD_SIZE, BOARD_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        initializeBoard();
        initializeBoard();
        initializeBoard();
        new Thread(this::solveSudoku).start();
    }

    private void initializeBoard() {
        // Fill a few random cells with valid numbers
        for (int i = 0; i < SIZE; i++) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            int num = random.nextInt(SIZE) + 1;
            if (isValid(row, col, num)) {
                board[row][col] = num;
                initial[row][col] = true;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Image offScreen = createImage(getWidth(), getHeight());
        Graphics offG = offScreen.getGraphics();
        paintComponent(offG);
        g.drawImage(offScreen, 0, 0, this);
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (initial[row][col]) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] != 0) {
                    g.setColor(initial[row][col] ? Color.BLACK : Color.BLUE);
                    g.drawString(String.valueOf(board[row][col]), col * CELL_SIZE + 25, row * CELL_SIZE + 40);
                }
            }
        }

        for (int i = 0; i <= SIZE; i++) {
            if (i % SUBGRID_SIZE == 0) {
                g.setColor(Color.BLACK);
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_SIZE);
                g.drawLine(0, i * CELL_SIZE, BOARD_SIZE, i * CELL_SIZE);
            } else {
                g.setColor(Color.GRAY);
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_SIZE);
                g.drawLine(0, i * CELL_SIZE, BOARD_SIZE, i * CELL_SIZE);
            }
        }
    }

    private boolean solveSudoku() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            repaint();
                            try {
                                Thread.sleep(100); // Adjust this delay for visualization speed
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (solveSudoku()) {
                                return true;
                            }

                            board[row][col] = 0;
                            repaint();
                            try {
                                Thread.sleep(100); // Adjust this delay for visualization speed
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Blink red to show backtracking
                    Graphics g = getGraphics();
                    g.setColor(Color.RED);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    repaint();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num ||
                    board[row - row % SUBGRID_SIZE + i / SUBGRID_SIZE][col - col % SUBGRID_SIZE + i % SUBGRID_SIZE] == num) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}

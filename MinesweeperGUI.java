import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MinesweeperGUI extends JPanel {

    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int NUM_MINES = 10;

    private char[][] board;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private boolean gameOver;
    private Random random;

    public MinesweeperGUI() {
        this.board = new char[ROWS][COLS];
        this.revealed = new boolean[ROWS][COLS];
        this.flagged = new boolean[ROWS][COLS];
        this.gameOver = false;
        this.random = new Random();

        // Initialize board with empty spaces
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '-';
            }
        }

        // Place mines randomly on the board
        for (int i = 0; i < NUM_MINES; i++) {
            int row, col;
            do {
                row = random.nextInt(ROWS);
                col = random.nextInt(COLS);
            } while (board[row][col] == '*');
            board[row][col] = '*';
        }

        // Add mouse listener to handle clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / 30;
                int col = e.getX() / 30;

                if (e.getButton() == MouseEvent.BUTTON1) { // Left click
                    reveal(row, col);
                } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    flag(row, col);
                }

                repaint();
            }
        });

        // Add restart button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JFrame frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        frame.add(restartButton, BorderLayout.SOUTH);
        frame.setSize(300, 350);
        frame.setVisible(true);
    }

    private void restartGame() {
        // Reset game state
        gameOver = false;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '-';
                revealed[i][j] = false;
                flagged[i][j] = false;
            }
        }

        // Place mines randomly on the board
        for (int i = 0; i < NUM_MINES; i++) {
            int row, col;
            do {
                row = random.nextInt(ROWS);
                col = random.nextInt(COLS);
            } while (board[row][col] == '*');
            board[row][col] = '*';
        }

        repaint();
    }

    private void reveal(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }
    
        if (board[row][col] == '*') {
            revealed[row][col] = true;
            gameOver = true;
            JOptionPane.showMessageDialog(null, "Game over! You hit a mine.");
            restartGame(); // Automatically restart the game
        } else {
            revealed[row][col] = true;
    
            int adjacentMines = countAdjacentMines(row, col);
            if (adjacentMines > 0) {
                board[row][col] = (char) (adjacentMines + '0');
            } else {
                board[row][col] = ' ';
                revealEmptySpaces(row, col);
            }
    
            // Check if the game has been won
            if (hasWon()) {
                JOptionPane.showMessageDialog(null, "Congratulations! You won!");
            }
        }
    
        repaint();
    }
    
    private void restartGame() {
        // Reset the game state
        board = new char[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        gameOver = false;
        initializeBoard(); // Reinitialize the board with mines and numbers
        repaint(); // Refresh the UI
    }
    
    private void initializeBoard() {
        // Logic to set up the board with mines and numbers
        // This method needs to be implemented according to your game's logic
    }
    
    private boolean hasWon() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != '*' && !revealed[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void flag(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }

        flagged[row][col] = !flagged[row][col];
        repaint();
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int adjacentRow = row + i;
                int adjacentCol = col + j;
                if (adjacentRow >= 0 && adjacentRow < ROWS && adjacentCol >= 0 && adjacentCol < COLS) {
                    if (board[adjacentRow][adjacentCol] == '*') {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void revealEmptySpaces(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int adjacentRow = row + i;
                int adjacentCol = col + j;
                if (adjacentRow >= 0 && adjacentRow < ROWS && adjacentCol >= 0 && adjacentCol < COLS) {
                    if (!revealed[adjacentRow][adjacentCol] && board[adjacentRow][adjacentCol] != '*') {
                        int adjacentMines = countAdjacentMines(adjacentRow, adjacentCol);
                        if (adjacentMines == 0) {
                            revealed[adjacentRow][adjacentCol] = true;
                            board[adjacentRow][adjacentCol] = ' ';
                            revealEmptySpaces(adjacentRow, adjacentCol);
                        } else {
                            revealed[adjacentRow][adjacentCol] = true;
                            board[adjacentRow][adjacentCol] = (char) (adjacentMines + '0');
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (revealed[i][j]) {
                    if (board[i][j] == '*') {
                        g.setColor(Color.RED);
                        g.fillRect(j * 30, i * 30, 30, 30);
                    } else if (board[i][j] == ' ') {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * 30, i * 30, 30, 30);
                    } else {
                        g.setColor(Color.BLACK);
                        g.drawString(String.valueOf(board[i][j]), j * 30 + 10, i * 30 + 20);
                    }
                } else if (flagged[i][j]) {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * 30, i * 30, 30, 30);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(j * 30, i * 30, 30, 30);
                }
                g.setColor(Color.BLACK);
                g.drawRect(j * 30, i * 30, 30, 30);
            }
        }
    }

    public static void main(String[] args) {
        new MinesweeperGUI();
    }
}
                       

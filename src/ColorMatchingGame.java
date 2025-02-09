import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class representing the Color Matching Game
public class ColorMatchingGame extends JFrame {

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;
    private static final int TOTAL_PAIRS = 8;

    private List<Color> colorList; // Used to store colors on the game board
    private boolean[][] matchedPairs;  // Matrix to track matching color pairs
    private Color[][] gameBoard; // Matrix to store colors on the game board
    private int firstClickRow = -1;
    private int firstClickCol = -1;
    private int secondClickRow = -1;
    private int secondClickCol = -1;
    private Timer timer; // Used to show unmatched colors for a certain time and then hide them
    private int remainingAttempts; // Used to track how many attempts the user has left

    // Main constructor of the game
    public ColorMatchingGame() {
        initializeGame();
        setupUI();
    }

    // Method to set up the initial state of the game
    private void initializeGame() {
        colorList = generateRandomColors(TOTAL_PAIRS); // Generate random colors and shuffle
        Collections.shuffle(colorList);

        gameBoard = new Color[ROWS][COLUMNS];
        matchedPairs = new boolean[ROWS][COLUMNS];
        int colorIndex = 0;

        // Place colors on the game board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gameBoard[i][j] = colorList.get(colorIndex);
                colorIndex++;
            }
        }

        initializeTimer(); // Start the timer and monitor the game state
        remainingAttempts = 3; // The user starts with three attempts
    }

    // Method to set up the user interface
    private void setupUI() {
        setTitle("Color Matching Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        JPanel gamePanel = new GamePanel(); // A special JPanel class to draw the game board
        gamePanel.addMouseListener(new ClickListener()); // Listen to mouse clicks
        add(gamePanel);

        setVisible(true);
    }

    // Method to generate a specified number of random colors
    private List<Color> generateRandomColors(int count) {
        List<Color> colors = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int red = (int) (Math.random() * 256);
            int green = (int) (Math.random() * 256);
            int blue = (int) (Math.random() * 256);
            colors.add(new Color(red, green, blue));
        }

        // Add matches for each color
        colors.addAll(new ArrayList<>(colors));

        return colors;
    }

    // Method to initialize the timer
    private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Event from the timer: Stop the timer and decrease attempts
                timer.stop();
                remainingAttempts--;

                // Check for losing
                if (remainingAttempts == 0) {
                    JOptionPane.showMessageDialog(ColorMatchingGame.this, "Game over! You lost.");
                    dispose();
                }

                // Reset first and second click information
                firstClickRow = -1;
                firstClickCol = -1;
                secondClickRow = -1;
                secondClickCol = -1;

                // Repaint
                repaint();
            }
        });
    }

    // Inner class to listen for mouse clicks
    private class ClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int clickedRow = e.getY() / (getHeight() / ROWS);
            int clickedCol = e.getX() / (getWidth() / COLUMNS);

            if (firstClickRow == -1) {
                // First click
                firstClickRow = clickedRow;
                firstClickCol = clickedCol;
            } else {
                // Second click
                secondClickRow = clickedRow;
                secondClickCol = clickedCol;

                if (!gameBoard[firstClickRow][firstClickCol].equals(gameBoard[secondClickRow][secondClickCol])) {
                    // No match, start the timer
                    timer.start();
                } else {
                    // Match found, mark the pairs as matched
                    matchedPairs[firstClickRow][firstClickCol] = true;
                    matchedPairs[secondClickRow][secondClickCol] = true;
                }
            }

            // Check for game end
            if (isGameFinished()) {
                JOptionPane.showMessageDialog(ColorMatchingGame.this, "Congratulations! You won.");
                dispose();
            }

            // Repaint
            repaint();
        }
    }

    // Inner class representing a specialized JPanel to draw the game board
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int cellWidth = getWidth() / COLUMNS;
            int cellHeight = getHeight() / ROWS;

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (remainingAttempts == 0 || matchedPairs[i][j] || (i == firstClickRow && j == firstClickCol) || (i == secondClickRow && j == secondClickCol)) {
                        // Show all cells when the game is over or there are matched pairs or clicks
                        drawCircle(g, j * cellWidth, i * cellHeight, cellWidth, gameBoard[i][j]);
                    } else {
                        // Show only unclicked and unmatched cells in other cases
                        drawClosedCell(g, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        }

        // Method to draw closed cells
        private void drawClosedCell(Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }

        // Method to draw circles
        private void drawCircle(Graphics g, int x, int y, int diameter, Color color) {
            g.setColor(color);
            g.fillOval(x, y, diameter, diameter);
        }
    }

    // Method to check if the game is finished
    private boolean isGameFinished() {
        // Check if the game is finished
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (!matchedPairs[i][j]) {
                    // If there is still an unmatched cell, the game is not finished
                    return false;
                }
            }
        }
        return true; // If all cells are matched, the game is finished
    }

    // Main method to start the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ColorMatchingGame();
        });
    }
}

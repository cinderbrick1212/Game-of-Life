package main.java.com.Game_of_life.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public class Life {
    public static final int ROWS = 100;
    public static final int COLS = 200;
    public static final int CELL_SIZE = 8;
    public static final int TIME_DELAY = 500;
    private static JFrame frame;
    private static GamePanel gamePanel;
    private static volatile boolean isRunning = true;
    private static Board currentBoard;
    private static JLabel generationLabel;
    private static int generationCount = 0;

    static class GamePanel extends JPanel {
        private Board board;

        public GamePanel(Board board) {
            this.board = board;
            setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
            setBackground(Color.BLACK);

            // Add mouse listener for interactive cell toggling
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!isRunning) {
                        int col = e.getX() / CELL_SIZE;
                        int row = e.getY() / CELL_SIZE;
                        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                            int currentValue = board.get(row, col);
                            board.set(row, col, currentValue == 0 ? 1 : 0);
                            repaint();
                        }
                    }
                }
            });
        }

        public void updateBoard(Board newBoard) {
            this.board = newBoard;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw grid
            g2d.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= ROWS; i++) {
                g2d.drawLine(0, i * CELL_SIZE, COLS * CELL_SIZE, i * CELL_SIZE);
            }
            for (int i = 0; i <= COLS; i++) {
                g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, ROWS * CELL_SIZE);
            }

            // Draw cells with gradient effect
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (board.get(row, col) == 1) {
                        // Create gradient from center
                        int x = col * CELL_SIZE;
                        int y = row * CELL_SIZE;

                        // Different colors based on neighbor count for visual appeal
                        int neighbors = board.no_of_neighboursReturn(row, col);
                        Color cellColor;
                        switch (neighbors) {
                            case 0:
                            case 1:
                                cellColor = Color.RED;
                                break;
                            case 2:
                            case 3:
                                cellColor = Color.GREEN;
                                break;
                            default:
                                cellColor = Color.BLUE;
                                break;
                        }

                        // Create radial gradient
                        RadialGradientPaint gradient = new RadialGradientPaint(
                                x + CELL_SIZE / 2f, y + CELL_SIZE / 2f, CELL_SIZE / 2f,
                                new float[]{0f, 1f},
                                new Color[]{cellColor, cellColor.darker()}
                        );

                        g2d.setPaint(gradient);
                        g2d.fillOval(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                    }
                }
            }
        }
    }

    public static void initializeBoard(Board b) {
        // Create some interesting patterns
        // Clear the board first
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                b.set(row, col, 0);
            }
        }

        // Add some random cells
        for (int i = 0; i < (ROWS * COLS) / 8; i++) {
            int row = (int) (Math.random() * ROWS);
            int col = (int) (Math.random() * COLS);
            b.set(row, col, 1);
        }

        // Add a glider pattern
        if (ROWS > 10 && COLS > 10) {
            int startRow = 5;
            int startCol = 5;
            b.set(startRow, startCol + 1, 1);
            b.set(startRow + 1, startCol + 2, 1);
            b.set(startRow + 2, startCol, 1);
            b.set(startRow + 2, startCol + 1, 1);
            b.set(startRow + 2, startCol + 2, 1);
        }
    }

    public static Board calculateNextGeneration(Board currentBoard, Board nextBoard) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int neighborCount = currentBoard.no_of_neighboursReturn(row, col);
                int currentCell = currentBoard.get(row, col);

                nextBoard.set(row, col,
                        (currentCell == 1 && (neighborCount == 2 || neighborCount == 3)) ||
                                (currentCell == 0 && neighborCount == 3) ? 1 : 0);
            }
        }
        return nextBoard;
    }

    public static void transferNextToCurrent(Board board, Board nextBoard) {
        for (int row = 0; row < ROWS; row++) {
            System.arraycopy(nextBoard.b[row], 0, board.b[row], 0, COLS);
        }
    }

    private static void slow(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Conway's Game of Life - Enhanced Graphics");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            currentBoard = new Board(ROWS, COLS);
            gamePanel = new GamePanel(currentBoard);

            // Create control panel with enhanced UI
            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.setBackground(Color.DARK_GRAY);

            JButton pauseButton = new JButton("⏸️ Pause");
            JButton playButton = new JButton("▶️ Play");
            JButton resetButton = new JButton("🔄 Reset");
            JButton clearButton = new JButton("🗑️ Clear");

            generationLabel = new JLabel("Generation: 0");
            generationLabel.setForeground(Color.WHITE);
            generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

            JLabel instructionLabel = new JLabel("Click cells when paused to toggle them");
            instructionLabel.setForeground(Color.LIGHT_GRAY);
            instructionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

            // Speed control
            JLabel speedLabel = new JLabel("Speed:");
            speedLabel.setForeground(Color.WHITE);
            JSlider speedSlider = new JSlider(50, 1000, TIME_DELAY);
            speedSlider.setBackground(Color.DARK_GRAY);

            // Style buttons
            styleButton(pauseButton);
            styleButton(playButton);
            styleButton(resetButton);
            styleButton(clearButton);

            pauseButton.addActionListener(e -> {
                isRunning = false;
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
                System.out.println("Game paused");
            });

            playButton.addActionListener(e -> {
                isRunning = true;
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
                System.out.println("Game resumed");
            });

            resetButton.addActionListener(e -> {
                initializeBoard(currentBoard);
                generationCount = 0;
                generationLabel.setText("Generation: " + generationCount);
                gamePanel.updateBoard(currentBoard);
                System.out.println("Board reset");
            });

            clearButton.addActionListener(e -> {
                for (int row = 0; row < ROWS; row++) {
                    for (int col = 0; col < COLS; col++) {
                        currentBoard.set(row, col, 0);
                    }
                }
                generationCount = 0;
                generationLabel.setText("Generation: " + generationCount);
                gamePanel.updateBoard(currentBoard);
                System.out.println("Board cleared");
            });

            playButton.setEnabled(false); // Start with play disabled since we start running

            controlPanel.add(playButton);
            controlPanel.add(pauseButton);
            controlPanel.add(resetButton);
            controlPanel.add(clearButton);
            controlPanel.add(Box.createHorizontalStrut(20));
            controlPanel.add(speedLabel);
            controlPanel.add(speedSlider);
            controlPanel.add(Box.createHorizontalStrut(20));
            controlPanel.add(generationLabel);
            controlPanel.add(Box.createHorizontalStrut(20));
            controlPanel.add(instructionLabel);

            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(gamePanel), BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setSize(COLS * CELL_SIZE + 50, ROWS * CELL_SIZE + 100);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            initializeBoard(currentBoard);

            // Game loop in separate thread
            new Thread(() -> {
                Board nextBoard = new Board(ROWS, COLS);
                AtomicReference<Integer> currentDelay = new AtomicReference<>(TIME_DELAY);

                // Update delay when slider changes
                speedSlider.addChangeListener(e -> currentDelay.set(speedSlider.getValue()));

                while (true) {
                    if (isRunning) {
                        gamePanel.updateBoard(currentBoard);
                        slow(currentDelay.get());
                        calculateNextGeneration(currentBoard, nextBoard);
                        transferNextToCurrent(currentBoard, nextBoard);
                        generationCount++;
                        System.out.println("Generation: " + generationCount);
                        SwingUtilities.invokeLater(() ->
                                generationLabel.setText("Generation: " + generationCount));
                    } else {
                        slow(100);
                    }
                }
            }).start();
        });
    }

    private static void styleButton(JButton button) {
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        BorderFactory.createDashedBorder(Color.WHITE, 1, 1);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        // Add debug print
        System.out.println("Button styled: " + button.getText());
    }
}
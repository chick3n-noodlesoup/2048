package TwentyFortyEight;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PConstants;
import processing.core.PFont;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

//import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static int GRID_SIZE = 4; // 4x4 grid
    public static final int CELLSIZE = 100; // Cell size in pixels
    public static final int CELL_BUFFER = 8; // Space between cells
    public static int WIDTH = GRID_SIZE * CELLSIZE;
    public static int HEIGHT = GRID_SIZE * CELLSIZE;
    public static final int FPS = 30;

    // Color constants
    public final int BACKGROUND_COLOR = color(155, 138, 122);  // Standard 2048 background
    public final int GRID_COLOR = color(189, 172, 152);       // Empty cell color
    public final int GRID_BORDER_COLOR = color(155, 138, 122); // Grid border color
        
    // Animation constants
    public static final int ANIMATION_FRAMES = 8; // Animation duration in frames (0.5 seconds at 30fps)
    private int currentAnimationFrame = 0;
    private boolean isAnimating = false;

    private Cell[][] board;
    private Cell[][] previousBoard; // Store previous state for animation

    public static Random random = new Random();

    private long startTime;
    private boolean gameOver;
    private boolean gameStarted;
    private boolean waitingForNewTile = false;

    private PFont font;
    public static PImage two;
    public static PImage four;
    public static PImage eight;
    public static PImage sixteen;
    public static PImage thirtyTwo;
    public static PImage sixtyFour;
    public static PImage oneTwentyEight;
    public static PImage twoFiftySix;
    public static PImage fiveTwelve;
    public static PImage oneThousandTwentyFour;
    public static PImage twoThousandFortyEight;
    public static PImage fourThousandNinetySix;

    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    public App() {
        //this.configPath = "config.json";
        this.board = new Cell[GRID_SIZE][GRID_SIZE];
        this.previousBoard = new Cell[GRID_SIZE][GRID_SIZE];
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);

        // See PApplet javadoc:
        // loadJSONObject(configPath)
        this.two = loadImage(this.getClass().getResource("2.png").getPath().replace("%20", ""));
        this.four = loadImage(this.getClass().getResource("4.png").getPath().replace("%20", ""));
        this.eight = loadImage(this.getClass().getResource("8.png").getPath().replace("%20", ""));
        this.sixteen = loadImage(this.getClass().getResource("16.png").getPath().replace("%20", ""));
        this.thirtyTwo = loadImage(this.getClass().getResource("32.png").getPath().replace("%20", ""));
        this.sixtyFour = loadImage(this.getClass().getResource("64.png").getPath().replace("%20", ""));
        this.oneTwentyEight = loadImage(this.getClass().getResource("128.png").getPath().replace("%20", ""));
        this.twoFiftySix = loadImage(this.getClass().getResource("256.png").getPath().replace("%20", ""));
        this.fiveTwelve = loadImage(this.getClass().getResource("512.png").getPath().replace("%20", ""));
        this.oneThousandTwentyFour = loadImage(this.getClass().getResource("1024.png").getPath().replace("%20", ""));
        this.twoThousandFortyEight = loadImage(this.getClass().getResource("2048.png").getPath().replace("%20", ""));
        this.fourThousandNinetySix = loadImage(this.getClass().getResource("4096.png").getPath().replace("%20", ""));
        // " "));

        // create attributes for data storage, eg board
        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2] = new Cell(i2, i);
                previousBoard[i][i2] = new Cell(i2, i);
            }
        }

        startTime = millis();
        gameOver = false;
        gameStarted = true;

        addRandomTile();
        addRandomTile();
        saveCurrentBoardState();
    }
    
    // Helper method to save the current board state for animation
    private void saveCurrentBoardState() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                previousBoard[i][j] = new Cell(j, i);
                previousBoard[i][j].setValue(board[i][j].getValue());
                previousBoard[i][j].setSourceX(board[i][j].getX());
                previousBoard[i][j].setSourceY(board[i][j].getY());
                previousBoard[i][j].setTargetX(board[i][j].getX());
                previousBoard[i][j].setTargetY(board[i][j].getY());
            }
        }
    }
    
    // Helper method to copy the board state
    private int[][] boardToArray(Cell[][] originalBoard) {
        int[][] newBoard = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                newBoard[i][j] = originalBoard[i][j].getValue();
            }
        }
        return newBoard;
    }

    // Helper method to compare if two boards (arrays) are equal
    private boolean boardsAreEqual(int[][] board1, int[][] board2) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board1[i][j] != board2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void keyReleased(KeyEvent e) {
        // Not used in this case
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == PConstants.LEFT && !isAnimating) {
            Cell current = board[e.getY()/App.CELLSIZE][e.getX()/App.CELLSIZE];
            current.place();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used in this case
    }

    private void moveLeft() {
        for (int y = 0; y < GRID_SIZE; y++) {
            mergeRow(board[y]);
        }
    }

    private void moveRight() {
        for (int y = 0; y < GRID_SIZE; y++) {
            reverseRow(board[y]);
            mergeRow(board[y]);
            reverseRow(board[y]);
        }
    }

    private void moveUp() {
        for (int x = 0; x < GRID_SIZE; x++) {
            mergeColumn(x);
        }
    }

    private void moveDown() {
        for (int x = 0; x < GRID_SIZE; x++) {
            reverseColumn(x);
            mergeColumn(x);
            reverseColumn(x);
        }
    }

    private void reverseRow(Cell[] row) {
        for (int i = 0; i < GRID_SIZE / 2; i++) {
            Cell temp = row[i];
            row[i] = row[GRID_SIZE - i - 1];
            row[GRID_SIZE - i - 1] = temp;
        }
    }

    private void mergeRow(Cell[] row) {
        // Step 1: Compact non-zero values to the left
        int[] values = new int[GRID_SIZE];
        int writeIndex = 0;
        
        for (int i = 0; i < GRID_SIZE; i++) {
            if (row[i].getValue() != 0) {
                values[writeIndex++] = row[i].getValue();
            }
        }
        
        // Step 2: Merge adjacent identical values (only one merge per number)
        int[] merged = new int[GRID_SIZE];
        int mergeIndex = 0;
        
        for (int i = 0; i < writeIndex; i++) {
            // If current value and next value are the same, merge them
            if (i + 1 < writeIndex && values[i] == values[i + 1]) {
                merged[mergeIndex++] = values[i] * 2;
                i++; // Skip the next value as it's been merged
            } else {
                // Otherwise, just copy the value
                merged[mergeIndex++] = values[i];
            }
        }
        
        // Step 3: Update the row with new values
        for (int i = 0; i < GRID_SIZE; i++) {
            if (i < mergeIndex) {
                row[i].setValue(merged[i]);
            } else {
                row[i].setValue(0);
            }
        }
    }

    private void mergeColumn(int col) {
        // Step 1: Compact non-zero values to the top
        int[] values = new int[GRID_SIZE];
        int writeIndex = 0;
        
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col].getValue() != 0) {
                values[writeIndex++] = board[i][col].getValue();
            }
        }
        
        // Step 2: Merge adjacent identical values (only one merge per number)
        int[] merged = new int[GRID_SIZE];
        int mergeIndex = 0;
        
        for (int i = 0; i < writeIndex; i++) {
            // If current value and next value are the same, merge them
            if (i + 1 < writeIndex && values[i] == values[i + 1]) {
                merged[mergeIndex++] = values[i] * 2;
                i++; // Skip the next value as it's been merged
            } else {
                // Otherwise, just copy the value
                merged[mergeIndex++] = values[i];
            }
        }
        
        // Step 3: Update the column with new values
        for (int i = 0; i < GRID_SIZE; i++) {
            if (i < mergeIndex) {
                board[i][col].setValue(merged[i]);
            } else {
                board[i][col].setValue(0);
            }
        }
    }

    private void reverseColumn(int col) {
        for (int i = 0; i < GRID_SIZE / 2; i++) {
            Cell temp = board[i][col];
            board[i][col] = board[GRID_SIZE - i - 1][col];
            board[GRID_SIZE - i - 1][col] = temp;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
    // Modified drawAnimatedBoard method to keep the background color consistent
    

    // Add this easing function for smoother animations
    private float easeCubic(float t) {
        return t < 0.5 ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
    }

    // Modify the updateCellTargets method to handle merges better
    private void updateCellTargets() {
        // First identify merges that happened
        Map<String, List<Cell>> mergedCells = new HashMap<>();
        
        // Process the board to identify merges
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int value = board[i][j].getValue();
                if (value != 0) {
                    String key = i + "," + j;
                    mergedCells.put(key, new ArrayList<>());
                }
            }
        }
        
        // For each cell in previous board, find its target position
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = previousBoard[i][j];
                if (cell.getValue() != 0) {
                    // Find the best matching position in the new board
                    int bestTargetY = -1;
                    int bestTargetX = -1;
                    int minDistance = Integer.MAX_VALUE;
                    
                    // Direction of movement based on last key press
                    boolean horizontal = (lastMoveDirection == PConstants.LEFT || lastMoveDirection == PConstants.RIGHT);
                    boolean increasing = (lastMoveDirection == PConstants.RIGHT || lastMoveDirection == PConstants.DOWN);
                    
                    for (int y = 0; y < GRID_SIZE; y++) {
                        for (int x = 0; x < GRID_SIZE; x++) {
                            if (board[y][x].getValue() == cell.getValue() || 
                                board[y][x].getValue() == cell.getValue() * 2) {
                                
                                // Calculate distance based on movement direction
                                int distance;
                                
                                if (horizontal) {
                                    // When moving horizontally, Y must match
                                    if (y != i) continue;
                                    
                                    // X distance considering direction
                                    if (increasing) {
                                        distance = x >= j ? x - j : GRID_SIZE + x - j;
                                    } else {
                                        distance = x <= j ? j - x : j + GRID_SIZE - x;
                                    }
                                } else {
                                    // When moving vertically, X must match
                                    if (x != j) continue;
                                    
                                    // Y distance considering direction
                                    if (increasing) {
                                        distance = y >= i ? y - i : GRID_SIZE + y - i;
                                    } else {
                                        distance = y <= i ? i - y : i + GRID_SIZE - y;
                                    }
                                }
                                
                                String key = y + "," + x;
                                if (distance < minDistance && mergedCells.containsKey(key) && 
                                    mergedCells.get(key).size() < (board[y][x].getValue() == cell.getValue() * 2 ? 2 : 1)) {
                                    minDistance = distance;
                                    bestTargetY = y;
                                    bestTargetX = x;
                                }
                            }
                        }
                    }
                    
                    if (bestTargetY != -1 && bestTargetX != -1) {
                        cell.setTargetY(bestTargetY);
                        cell.setTargetX(bestTargetX);
                        
                        String key = bestTargetY + "," + bestTargetX;
                        mergedCells.get(key).add(cell);
                    } else {
                        // Cell disappeared (merged with another)
                        cell.setValue(0);
                    }
                }
            }
        }
    }

    // Add this field to track the last direction of movement
    private int lastMoveDirection = PConstants.RIGHT; // Default direction

    // Update the keyPressed method to set the last direction
    @Override
    public void keyPressed(KeyEvent event) {
        // If animation is in progress, ignore input
        if (isAnimating) {
            return;
        }

        int[][] originalBoard = boardToArray(board);
        
        // Save the current state for animation
        saveCurrentBoardState();

        if (event.getKeyCode() == PConstants.LEFT) {
            lastMoveDirection = PConstants.LEFT;
            moveLeft();
        } else if (event.getKeyCode() == PConstants.RIGHT) {
            lastMoveDirection = PConstants.RIGHT;
            moveRight();
        } else if (event.getKeyCode() == PConstants.UP) {
            lastMoveDirection = PConstants.UP;
            moveUp();
        } else if (event.getKeyCode() == PConstants.DOWN) {
            lastMoveDirection = PConstants.DOWN;
            moveDown();
        } else if (event.getKey() == 'r' || event.getKey() == 'R') {
            if (gameOver) {
                restartGame();
            }
        }

        if (!boardsAreEqual(originalBoard, boardToArray(board))) {
            // Start animation
            isAnimating = true;
            currentAnimationFrame = 0;
            
            // Update target positions for animation
            updateCellTargets();
            
            // Will add a new tile after animation completes
            waitingForNewTile = true;
        }
        
        checkGameOver();
    }

    // Update the draw method to keep the background color consistent
    // Modified drawAnimatedBoard method to use the correct grid colors
    private void drawAnimatedBoard() {
        // Draw the background first
        background(BACKGROUND_COLOR);
        
        // Draw the grid background (empty cells)
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                // Draw empty cell background
                fill(GRID_COLOR);
                stroke(GRID_BORDER_COLOR);
                rect(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE);
            }
        }
        
        // Draw animated tiles from previous state
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Cell cell = previousBoard[i][j];
                if (cell.getValue() != 0) {
                    // Calculate current position based on animation progress
                    float progress = (float)currentAnimationFrame / ANIMATION_FRAMES;
                    
                    // Apply easing function for smoother animation
                    progress = easeCubic(progress);
                    
                    // Only animate in the specific direction of movement
                    float currentX, currentY;
                    
                    if (cell.getSourceY() == cell.getTargetY()) {
                        currentX = cell.getSourceX() + (cell.getTargetX() - cell.getSourceX()) * progress;
                        currentY = cell.getSourceY();
                    } 
                    else if (cell.getSourceX() == cell.getTargetX()) {
                        currentX = cell.getSourceX();
                        currentY = cell.getSourceY() + (cell.getTargetY() - cell.getSourceY()) * progress;
                    }
                    else {
                        currentX = cell.getTargetX();
                        currentY = cell.getTargetY();
                    }
                    
                    // Draw tile at interpolated position
                    cell.drawAnimated(this, currentX, currentY);
                }
            }
        }
    }

    // Update the draw method to use the correct colors
    @Override
    public void draw() {
        // Draw background with the correct 2048 color
        background(BACKGROUND_COLOR);  // Standard 2048 background color

        // Draw the grid and tiles
        this.textSize(40);
        this.strokeWeight(15);

        // Handle animation
        if (isAnimating) {
            drawAnimatedBoard();
            currentAnimationFrame++;

            // End of animation
            if (currentAnimationFrame >= ANIMATION_FRAMES) {
                isAnimating = false;

                // Add a new tile if the board changed
                if (waitingForNewTile) {
                    addRandomTile();
                    waitingForNewTile = false;
                }
            }
        } else {
            // Draw the current board state without animation
            // First draw the grid background for all cells
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    // Draw empty cell background
                    fill(GRID_COLOR);
                    stroke(GRID_BORDER_COLOR);
                    rect(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE);
                }
            }
            
            // Then draw the tiles (whether animated or static)
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    board[i][j].draw(this);
                }
            }
        }

        // Draw the timer in the top-right corner within a white rectangle
        if (gameStarted && !gameOver) {
            long elapsedTime = (millis() - startTime) / 1000;  // Time in seconds

            // White rectangle for the timer background
            fill(255); 
            noStroke();
            rect(WIDTH - 105, 5, 100, 40);  // Position the rectangle

            // Timer text in smaller font, centered inside the rectangle
            textSize(20);
            fill(0);  // Black text
            textAlign(CENTER, CENTER);
            text("Time: " + elapsedTime + "s", WIDTH - 55, 20);  // Adjusted position
        }

        // Display "GAME OVER" if the game is over
        if (gameOver) {
            background(250, 248, 239);  // Keep consistent background color
            textSize(60);
            textAlign(CENTER, CENTER);
            fill(0);
            text("GAME OVER", WIDTH / 2, HEIGHT / 2);
        }
    }

    private void addRandomTile() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (board[y][x].getValue() == 0) {
                    emptyCells.add(board[y][x]);
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            Cell randomCell = emptyCells.get(App.random.nextInt(emptyCells.size()));
            randomCell.place();
        }
    }

    private void checkGameOver() {
        gameOver = true;
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (board[y][x].getValue() == 0) {
                    gameOver = false;
                    break;
                }
                if (x < GRID_SIZE - 1 && board[y][x].getValue() == board[y][x + 1].getValue()) {
                    gameOver = false;
                    break;
                }
                if (y < GRID_SIZE - 1 && board[y][x].getValue() == board[y + 1][x].getValue()) {
                    gameOver = false;
                    break;
                }
            }
        }

        if (gameOver) {
            System.out.println("Game Over!");
        }
    }

    private void restartGame() {
        // Reset the game state
        gameOver = false;
        startTime = millis();  // Reset the timer
        isAnimating = false;
        this.board = new Cell[GRID_SIZE][GRID_SIZE];
        this.previousBoard = new Cell[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2] = new Cell(i2, i);
                previousBoard[i][i2] = new Cell(i2, i);
            }
        }

        addRandomTile();
        saveCurrentBoardState();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                GRID_SIZE = Integer.parseInt(args[0]);
                if (GRID_SIZE < 2) {
                    GRID_SIZE = 4;
                }
            } catch (NumberFormatException e) {
                GRID_SIZE = 4;
             }
        }

        WIDTH = GRID_SIZE * CELLSIZE;
        HEIGHT = GRID_SIZE * CELLSIZE;

        PApplet.main("TwentyFortyEight.App");
    }
}

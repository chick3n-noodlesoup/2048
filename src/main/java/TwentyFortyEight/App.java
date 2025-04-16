package TwentyFortyEight;
import TwentyFortyEight.AnimatedTile;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PConstants;
import processing.core.PFont;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

public class App extends PApplet {

    public static int GRID_SIZE = 4; // 4x4 grid
    public static final int CELLSIZE = 100; // Cell size in pixels
    public static final int CELL_BUFFER = 8; // Space between cells
    public static final int offsetY = 40;
    public static int WIDTH = GRID_SIZE * CELLSIZE;
    public static int HEIGHT = GRID_SIZE * CELLSIZE;
    public static final int FPS = 60;

    // Color constants
    public final int BACKGROUND_COLOR = color(155, 138, 122);  // Standard 2048 background
    public final int GRID_COLOR = color(189, 172, 152);       // Empty cell color
    public final int GRID_BORDER_COLOR = color(155, 138, 122); // Grid border color
        
    // Animation constants
    public static final int ANIMATION_FRAMES = 10; // Animation duration in frames
    private int currentAnimationFrame = 0;
    private boolean isAnimating = false;

    private Cell[][] board;
    private ArrayList<AnimatedTile> animatedTiles; // Store tiles being animated

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

    // Last direction of movement
    private int lastMoveDirection = PConstants.RIGHT; // Default direction

    public App() {
        this.board = new Cell[GRID_SIZE][GRID_SIZE];
        this.animatedTiles = new ArrayList<>();
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

        // Load images
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

        // Initialize the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Cell(j, i);
            }
        }

        startTime = millis();
        gameOver = false;
        gameStarted = true;

        // Add initial tiles
        addRandomTile();
        addRandomTile();
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
            int y = e.getY()/App.CELLSIZE;
            int x = e.getX()/App.CELLSIZE;
            
            // Check bounds to prevent array index out of bounds
            if (y >= 0 && y < GRID_SIZE && x >= 0 && x < GRID_SIZE) {
                Cell current = board[y][x];
                current.place();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used in this case
    }

    private void prepareAnimation() {
        // Clear previous animations
        animatedTiles.clear();
        
        // Create animated tiles for all non-zero cells
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (board[y][x].getValue() > 0) {
                    // Add to animation list
                    AnimatedTile tile = new AnimatedTile(
                        board[y][x].getValue(),
                        x, y,  // Current position (will be the target)
                        x, y   // Start with same source as target (we'll update this)
                    );
                    animatedTiles.add(tile);
                }
            }
        }
    }

    private void moveLeft() {
        // Prepare for animation tracking
        prepareAnimation();
        
        boolean[][] merged = new boolean[GRID_SIZE][GRID_SIZE]; // Track merged tiles
        
        // Process each row
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 1; x < GRID_SIZE; x++) {
                if (board[y][x].getValue() == 0) continue;
                
                int origX = x;
                int origY = y;
                int newX = x;
                
                // Try to move this tile as far left as possible
                while (newX > 0 && board[y][newX-1].getValue() == 0) {
                    newX--;
                }
                
                // Check if we can merge with the tile to our left
                if (newX > 0 && board[y][newX-1].getValue() == board[y][x].getValue() && !merged[y][newX-1]) {
                    // Merge with the tile to the left
                    board[y][newX-1].setValue(board[y][newX-1].getValue() * 2);
                    board[y][x].setValue(0);
                    merged[y][newX-1] = true;
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(newX-1);
                            tile.setTargetY(y);
                            tile.setMerged(true);
                        }
                    }
                } else if (newX != x) {
                    // Just move without merging
                    board[y][newX].setValue(board[y][x].getValue());
                    board[y][x].setValue(0);
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(newX);
                            tile.setTargetY(y);
                        }
                    }
                }
            }
        }
    }

    private void moveRight() {
        // Prepare for animation tracking
        prepareAnimation();
        
        boolean[][] merged = new boolean[GRID_SIZE][GRID_SIZE]; // Track merged tiles
        
        // Process each row from right to left
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = GRID_SIZE - 2; x >= 0; x--) {
                if (board[y][x].getValue() == 0) continue;
                
                int origX = x;
                int origY = y;
                int newX = x;
                
                // Try to move this tile as far right as possible
                while (newX < GRID_SIZE - 1 && board[y][newX+1].getValue() == 0) {
                    newX++;
                }
                
                // Check if we can merge with the tile to our right
                if (newX < GRID_SIZE - 1 && board[y][newX+1].getValue() == board[y][x].getValue() && !merged[y][newX+1]) {
                    // Merge with the tile to the right
                    board[y][newX+1].setValue(board[y][newX+1].getValue() * 2);
                    board[y][x].setValue(0);
                    merged[y][newX+1] = true;
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(newX+1);
                            tile.setTargetY(y);
                            tile.setMerged(true);
                        }
                    }
                } else if (newX != x) {
                    // Just move without merging
                    board[y][newX].setValue(board[y][x].getValue());
                    board[y][x].setValue(0);
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(newX);
                            tile.setTargetY(y);
                        }
                    }
                }
            }
        }
    }

    private void moveUp() {
        // Prepare for animation tracking
        prepareAnimation();
        
        boolean[][] merged = new boolean[GRID_SIZE][GRID_SIZE]; // Track merged tiles
        
        // Process each column from top to bottom
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 1; y < GRID_SIZE; y++) {
                if (board[y][x].getValue() == 0) continue;
                
                int origX = x;
                int origY = y;
                int newY = y;
                
                // Try to move this tile as far up as possible
                while (newY > 0 && board[newY-1][x].getValue() == 0) {
                    newY--;
                }
                
                // Check if we can merge with the tile above
                if (newY > 0 && board[newY-1][x].getValue() == board[y][x].getValue() && !merged[newY-1][x]) {
                    // Merge with the tile above
                    board[newY-1][x].setValue(board[newY-1][x].getValue() * 2);
                    board[y][x].setValue(0);
                    merged[newY-1][x] = true;
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(x);
                            tile.setTargetY(newY-1);
                            tile.setMerged(true);
                        }
                    }
                } else if (newY != y) {
                    // Just move without merging
                    board[newY][x].setValue(board[y][x].getValue());
                    board[y][x].setValue(0);
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(x);
                            tile.setTargetY(newY);
                        }
                    }
                }
            }
        }
    }

    private void moveDown() {
        // Prepare for animation tracking
        prepareAnimation();
        
        boolean[][] merged = new boolean[GRID_SIZE][GRID_SIZE]; // Track merged tiles
        
        // Process each column from bottom to top
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = GRID_SIZE - 2; y >= 0; y--) {
                if (board[y][x].getValue() == 0) continue;
                
                int origX = x;
                int origY = y;
                int newY = y;
                
                // Try to move this tile as far down as possible
                while (newY < GRID_SIZE - 1 && board[newY+1][x].getValue() == 0) {
                    newY++;
                }
                
                // Check if we can merge with the tile below
                if (newY < GRID_SIZE - 1 && board[newY+1][x].getValue() == board[y][x].getValue() && !merged[newY+1][x]) {
                    // Merge with the tile below
                    board[newY+1][x].setValue(board[newY+1][x].getValue() * 2);
                    board[y][x].setValue(0);
                    merged[newY+1][x] = true;
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(x);
                            tile.setTargetY(newY+1);
                            tile.setMerged(true);
                        }
                    }
                } else if (newY != y) {
                    // Just move without merging
                    board[newY][x].setValue(board[y][x].getValue());
                    board[y][x].setValue(0);
                    
                    // Update animation data
                    for (AnimatedTile tile : animatedTiles) {
                        if (tile.getSourceX() == origX && tile.getSourceY() == origY) {
                            tile.setTargetX(x);
                            tile.setTargetY(newY);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        // If animation is in progress, ignore input
        if (isAnimating || gameOver) {
            if (gameOver && (event.getKey() == 'r' || event.getKey() == 'R')) {
                restartGame();
            }
            return;
        }

        int[][] originalBoard = boardToArray(board);
        
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
            restartGame();
            return;
        }

        // Check if the board changed and start animation if it did
        if (!boardsAreEqual(originalBoard, boardToArray(board))) {
            isAnimating = true;
            currentAnimationFrame = 0;
            waitingForNewTile = true;
        }
        
        checkGameOver();
    }

    private void drawAnimatedBoard() {
        // Draw the background first
        background(BACKGROUND_COLOR);
        
        // Draw the grid background (empty cells)
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                // Draw empty cell background
                fill(GRID_COLOR);
                noStroke();
                rect(j * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER, 
                     i * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER + offsetY, 
                     CELLSIZE, CELLSIZE, 20);
            }
        }
        
        // Draw animated tiles
        for (AnimatedTile tile : animatedTiles) {
            // Calculate current position based on animation progress
            float progress = (float)currentAnimationFrame / ANIMATION_FRAMES;
            
            // Interpolate between source and target positions
            float currentX = tile.getSourceX() + (tile.getTargetX() - tile.getSourceX()) * progress;
            float currentY = tile.getSourceY() + (tile.getTargetY() - tile.getSourceY()) * progress;
            
            // Calculate position in pixels
            float px = currentX * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER;
            float py = currentY * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER + offsetY;
            
            // Draw tile at interpolated position
            noStroke();
            fill(189, 172, 151);  // Default cell color
            rect(px, py, CELLSIZE, CELLSIZE, 20);
            
            // Draw tile value
            if (tile.getValue() > 0) {
                tile.draw(this, px, py);
                
            }
        }
    }

    @Override
    public void draw() {
        // Draw background with the correct 2048 color
        background(BACKGROUND_COLOR);

        this.textSize(40);
        this.strokeWeight(15);

        // Handle animation
        if (isAnimating) {
            drawAnimatedBoard();
            currentAnimationFrame++;

            // End of animation
            if (currentAnimationFrame >= ANIMATION_FRAMES) {
                isAnimating = false;

                // Add a new tile if the board changed and we're waiting for one
                if (waitingForNewTile) {
                    addRandomTile();
                    waitingForNewTile = false;
                    checkGameOver();
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
                    rect(j * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER, 
                         i * (CELLSIZE + CELL_BUFFER) + CELL_BUFFER + offsetY, 
                         CELLSIZE, CELLSIZE, 20);
                }
            }
            
            // Then draw the tiles
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
            fill(155, 138, 122); 
            noStroke();
            rect(0, 0, WIDTH, offsetY);  // Position the rectangle

            // Timer text in smaller font, centered inside the rectangle
            textSize(20);
            fill(0);
            textAlign(CENTER, CENTER);
            text("TIME: " + elapsedTime + "s", WIDTH - 100, 20);  // Adjusted position
        }

        // Display "GAME OVER" if the game is over
        if (gameOver) {
            background(250, 248, 239);  // Keep consistent background color
            textSize(60);
            textAlign(CENTER, CENTER);
            fill(0);
            text("GAME OVER", WIDTH / 2, HEIGHT / 2);
            textSize(20);
            text("Press 'R' to restart", WIDTH / 2, HEIGHT / 2 + 40);
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
        // Check if there are any empty cells
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (board[y][x].getValue() == 0) {
                    // There's at least one empty cell, so the game is not over
                    gameOver = false;
                    return;
                }
            }
        }
        
        // No empty cells, check if there are any possible merges
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                // Check right
                if (x < GRID_SIZE - 1 && board[y][x].getValue() == board[y][x+1].getValue()) {
                    gameOver = false;
                    return;
                }
                // Check down
                if (y < GRID_SIZE - 1 && board[y][x].getValue() == board[y+1][x].getValue()) {
                    gameOver = false;
                    return;
                }
            }
        }
        
        // If we get here, the game is over
        gameOver = true;
        System.out.println("Game Over!");
    }

    private void restartGame() {
        // Reset the game state
        gameOver = false;
        startTime = millis();  // Reset the timer
        isAnimating = false;
        
        // Reset the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Cell(j, i);
            }
        }
        
        // Clear animation tiles
        animatedTiles.clear();

        // Add initial tiles
        addRandomTile();
        addRandomTile();
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

        WIDTH = GRID_SIZE * CELLSIZE + ((GRID_SIZE + 1) * CELL_BUFFER);
        HEIGHT = GRID_SIZE * CELLSIZE + ((GRID_SIZE + 1) * CELL_BUFFER) + offsetY;

        PApplet.main("TwentyFortyEight.App");
    }
}
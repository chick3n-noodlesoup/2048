package TwentyFortyEight;

import java.util.*;
import processing.core.PImage;

public class Cell {
    private int x;
    private int y;
    private int value;
    private HashMap<Integer, PImage> numberToImage;

    // Animation properties
    private int sourceX;
    private int sourceY;
    private int targetX;
    private int targetY;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = 0;
        this.sourceX = x;
        this.sourceY = y;
        this.targetX = x;
        this.targetY = y;
        this.numberToImage = new HashMap<>();
        
        // Initialize number to image mappings
        numberToImage.put(2, App.two);
        numberToImage.put(4, App.four);
        numberToImage.put(8, App.eight);
        numberToImage.put(16, App.sixteen);
        numberToImage.put(32, App.thirtyTwo);
        numberToImage.put(64, App.sixtyFour);
        numberToImage.put(128, App.oneTwentyEight);
        numberToImage.put(256, App.twoFiftySix);
        numberToImage.put(512, App.fiveTwelve);
        numberToImage.put(1024, App.oneThousandTwentyFour);
        numberToImage.put(2048, App.twoThousandFortyEight);
        numberToImage.put(4096, App.fourThousandNinetySix);
    }

    public void place() {
        if (this.value == 0) {
            this.value = (App.random.nextInt(2) + 1) * 2;
        }
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // Animation getters and setters
    public int getSourceX() {
        return this.sourceX;
    }

    public void setSourceX(int sourceX) {
        this.sourceX = sourceX;
    }

    public int getSourceY() {
        return this.sourceY;
    }

    public void setSourceY(int sourceY) {
        this.sourceY = sourceY;
    }

    public int getTargetX() {
        return this.targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return this.targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    /*
     * This draws the cell
     */
    public void draw(App app) {
        float px = x * App.CELLSIZE;
        float py = y * App.CELLSIZE;

        app.stroke(156, 139, 124);

        if (app.mouseX > px && app.mouseX < px + App.CELLSIZE &&
            app.mouseY > py && app.mouseY < py + App.CELLSIZE) {
            app.fill(232, 207, 184);
        } else {
            app.fill(189, 172, 151);
        }

        app.rect(px, py, App.CELLSIZE, App.CELLSIZE);

        if (this.value != 0) {
            app.image(numberToImage.get(this.value), px + 8, py + 8, App.CELLSIZE - 10, App.CELLSIZE - 10);
        }
    }

    /*
     * Draws the cell at the interpolated position during animation
     */
    public void drawAnimated(App app, float currentX, float currentY) {
        float px = currentX * App.CELLSIZE;
        float py = currentY * App.CELLSIZE;

        app.stroke(156, 139, 124);
    
        if (app.mouseX > px && app.mouseX < px + App.CELLSIZE &&
            app.mouseY > py && app.mouseY < py + App.CELLSIZE) {
            app.fill(232, 207, 184);  // Hover color
        } else {
            app.fill(189, 172, 151);  // Default cell color
        }

        app.rect(px, py, App.CELLSIZE, App.CELLSIZE);

        if (this.value != 0) {
            app.image(numberToImage.get(this.value), px + 8, py + 8, App.CELLSIZE - 10, App.CELLSIZE - 10);
        }
    }
}

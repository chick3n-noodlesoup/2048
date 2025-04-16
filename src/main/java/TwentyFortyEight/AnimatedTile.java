package TwentyFortyEight;
import java.util.*;
import processing.core.PImage;

public class AnimatedTile {
        private int value;
        private float sourceX, sourceY;
        private float targetX, targetY;
        private boolean merged;
        private HashMap<Integer, PImage> numberToImage;
        
        public AnimatedTile(int value, float targetX, float targetY, float sourceX, float sourceY) {
            this.value = value;
            this.targetX = targetX;
            this.targetY = targetY;
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.merged = false;

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
        
        public int getValue() {
            return value;
        }
        
        public float getSourceX() {
            return sourceX;
        }
        
        public float getSourceY() {
            return sourceY;
        }
        
        public float getTargetX() {
            return targetX;
        }
        
        public float getTargetY() {
            return targetY;
        }
        
        public void setTargetX(float targetX) {
            this.targetX = targetX;
        }
        
        public void setTargetY(float targetY) {
            this.targetY = targetY;
        }
        
        public boolean isMerged() {
            return merged;
        }
        
        public void setMerged(boolean merged) {
            this.merged = merged;
        }

        public void draw(App app, float px, float py) {
            app.noStroke();

            if (app.mouseX > px && app.mouseX < px + App.CELLSIZE &&
                app.mouseY > py && app.mouseY < py + App.CELLSIZE) {
                app.fill(232, 207, 184);
            } else {
                app.fill(189, 172, 151);
            }

            app.rect(px, py, App.CELLSIZE, App.CELLSIZE, 20);

            if (this.value != 0) {
                app.image(numberToImage.get(this.value), px, py, App.CELLSIZE, App.CELLSIZE);
            }
        }
    }
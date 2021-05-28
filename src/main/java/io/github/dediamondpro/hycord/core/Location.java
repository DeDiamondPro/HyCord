package io.github.dediamondpro.hycord.core;

public class Location {
    public int x;
    public int y;
    public int height;
    public int width;
    public int displayHeight;
    public int displayWidth;
    private boolean alignRight = false;
    private boolean alignBottem = false;

    public Location(int x, int y, int width, int height, int displayWidth, int displayHeight) {
        if (x > displayWidth / 2) {
            this.x = displayWidth - (x - this.width);
            alignRight = true;
        } else {
            this.x = x;
            alignRight = false;
        }
        if (y > displayHeight / 2) {
            this.y = displayHeight - (y + this.height);
            alignBottem = true;
        } else {
            this.y = y;
            alignBottem = false;
        }
        this.height = height;
        this.width = width;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    public int getXScaled(int displayWidth) {
        if (!alignRight) {
            return (int) Utils.map(this.x, 0, this.displayWidth, 0, displayWidth);
        }
        return displayWidth - ((int) Utils.map(this.x, 0, this.displayWidth, 0, displayWidth) + this.width);
    }

    public int getYScaled(int displayHeight) {
        if (!alignBottem) {
            return (int) Utils.map(this.y, 0, this.displayHeight, 0, displayHeight);
        }
        return displayHeight - ((int) Utils.map(this.y, 0, this.displayHeight, 0, displayHeight) + this.height);
    }

    public void set(int x, int y, int displayWidth, int displayHeight) {
        if (x > displayWidth / 2) {
            this.x = displayWidth - (x + this.width);
            alignRight = true;
        } else {
            this.x = x;
            alignRight = false;
        }
        if (y > displayHeight / 2) {
            this.y = displayHeight - (y + this.height);
            alignBottem = true;
        } else {
            this.y = y;
            alignBottem = false;
        }
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.width + "," + this.height + "," + displayWidth + "," + displayHeight + "," + alignRight + "," + alignBottem;
    }

    public Location(String str) {
        String[] split = str.split(",");
        this.x = Integer.parseInt(split[0]);
        this.y = Integer.parseInt(split[1]);
        this.width = Integer.parseInt(split[2]);
        this.height = Integer.parseInt(split[3]);
        this.displayWidth = Integer.parseInt(split[4]);
        this.displayHeight = Integer.parseInt(split[5]);
        this.alignRight = Boolean.parseBoolean(split[6]);
        this.alignBottem = Boolean.parseBoolean(split[7]);
    }
}

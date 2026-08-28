package com.carsim;

// (0,0) is bottom-left, (width-1, height-1) is top-right
public final class Field {

    private final int width;
    private final int height;

    public Field(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field width and height must be positive integers.");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean contains(Position position) {
        return position.x() >= 0 && position.x() < width
                && position.y() >= 0 && position.y() < height;
    }

    @Override
    public String toString() {
        return width + " x " + height;
    }
}

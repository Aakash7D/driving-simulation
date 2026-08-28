package com.carsim;

// record gives us equals/hashCode for free, which we need to check if
// two cars are on the same cell (collision detection uses this as a map key)
public record Position(int x, int y) {

    public Position move(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

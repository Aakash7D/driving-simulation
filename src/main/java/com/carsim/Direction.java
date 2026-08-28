package com.carsim;

public enum Direction {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    // clockwise: N -> E -> S -> W -> N
    public Direction turnRight() {
        switch (this) {
            case N: return E;
            case E: return S;
            case S: return W;
            case W: return N;
        }
        throw new IllegalStateException();
    }

    public Direction turnLeft() {
        switch (this) {
            case N: return W;
            case W: return S;
            case S: return E;
            case E: return N;
        }
        throw new IllegalStateException();
    }

    public static Direction fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        switch (code.trim().toUpperCase()) {
            case "N": return N;
            case "E": return E;
            case "S": return S;
            case "W": return W;
            default:
                throw new IllegalArgumentException(
                        "Invalid direction '" + code + "'. Expected one of N, E, S, W.");
        }
    }
}

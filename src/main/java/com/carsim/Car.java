package com.carsim;

public final class Car {

    private final String name;
    private final String commands;
    private Position position;
    private Direction direction;

    private boolean collided = false;
    private String collidedWithCarName;
    private int collisionStep;

    public Car(String name, Position startPosition, Direction startDirection, String commands) {
        this.name = name;
        this.position = startPosition;
        this.direction = startDirection;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public String getCommands() {
        return commands;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isCollided() {
        return collided;
    }

    public String getCollidedWithCarName() {
        return collidedWithCarName;
    }

    public int getCollisionStep() {
        return collisionStep;
    }

    public void markCollided(String otherCarName, int step) {
        if (collided) {
            return; // only record the first crash, don't overwrite it later
        }
        collided = true;
        collidedWithCarName = otherCarName;
        collisionStep = step;
    }

    public String describeInitial() {
        return String.format("%s, %s %s, %s", name, position, direction, commands);
    }

    public String describeResult() {
        if (collided) {
            return String.format("%s, collides with %s at %s at step %d",
                    name, collidedWithCarName, position, collisionStep);
        }
        return String.format("%s, %s %s", name, position, direction);
    }
}

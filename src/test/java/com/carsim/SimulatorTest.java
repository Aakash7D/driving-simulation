package com.carsim;

import java.util.List;

// no test framework here on purpose - didn't want to pull in a JUnit jar
// (binary) or wire up a build tool just to fetch one for a project this
// small. plain assertions + a tiny pass/fail counter does the job.
public final class SimulatorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        directionTurnsCycleCorrectly();
        fieldBoundaryIsExclusiveOfWidthAndHeight();
        forwardCommandMovesOneStepInFacingDirection();
        commandOffFieldBoundaryIsIgnored();
        singleCarMatchesWorkedExample();
        twoCarsCollideAtExpectedStepAndPosition();
        carsThatDoNotMeetDoNotCollide();
        shorterCommandListCarStaysPutButCanStillBeHit();
        carsStartingOnTheSameCellCollideImmediately();

        System.out.println();
        System.out.println("Tests passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void directionTurnsCycleCorrectly() {
        assertEquals("N turnRight -> E", Direction.E, Direction.N.turnRight());
        assertEquals("E turnRight -> S", Direction.S, Direction.E.turnRight());
        assertEquals("S turnRight -> W", Direction.W, Direction.S.turnRight());
        assertEquals("W turnRight -> N", Direction.N, Direction.W.turnRight());

        assertEquals("N turnLeft -> W", Direction.W, Direction.N.turnLeft());
        assertEquals("W turnLeft -> S", Direction.S, Direction.W.turnLeft());
        assertEquals("S turnLeft -> E", Direction.E, Direction.S.turnLeft());
        assertEquals("E turnLeft -> N", Direction.N, Direction.E.turnLeft());
    }

    private static void fieldBoundaryIsExclusiveOfWidthAndHeight() {
        Field field = new Field(10, 10);
        assertTrue("(0,0) is inside a 10x10 field", field.contains(new Position(0, 0)));
        assertTrue("(9,9) is inside a 10x10 field", field.contains(new Position(9, 9)));
        assertTrue("(10,9) is outside a 10x10 field", !field.contains(new Position(10, 9)));
        assertTrue("(9,10) is outside a 10x10 field", !field.contains(new Position(9, 10)));
        assertTrue("(-1,0) is outside a 10x10 field", !field.contains(new Position(-1, 0)));
    }

    private static void forwardCommandMovesOneStepInFacingDirection() {
        Field field = new Field(10, 10);
        Car car = new Car("A", new Position(1, 2), Direction.N, "F");
        new Simulator(field).run(List.of(car));
        assertEquals("Car moves north by one", new Position(1, 3), car.getPosition());
        assertEquals("Direction is unchanged by F", Direction.N, car.getDirection());
    }

    private static void commandOffFieldBoundaryIsIgnored() {
        Field field = new Field(10, 10);
        Car car = new Car("A", new Position(0, 0), Direction.S, "F");
        new Simulator(field).run(List.of(car));
        assertEquals("Car stays put when F would leave the field", new Position(0, 0), car.getPosition());
        assertTrue("Car did not collide", !car.isCollided());
    }

    private static void singleCarMatchesWorkedExample() {
        // scenario 1 from the assignment
        Field field = new Field(10, 10);
        Car car = new Car("A", new Position(1, 2), Direction.N, "FFRFFFFRRL");
        new Simulator(field).run(List.of(car));
        assertEquals("Final position matches worked example", new Position(5, 4), car.getPosition());
        assertEquals("Final direction matches worked example", Direction.S, car.getDirection());
        assertTrue("Car did not collide", !car.isCollided());
    }

    private static void twoCarsCollideAtExpectedStepAndPosition() {
        // scenario 2 from the assignment
        Field field = new Field(10, 10);
        Car a = new Car("A", new Position(1, 2), Direction.N, "FFRFFFFRRL");
        Car b = new Car("B", new Position(7, 8), Direction.W, "FFLFFFFFFF");
        new Simulator(field).run(List.of(a, b));

        assertTrue("A collided", a.isCollided());
        assertTrue("B collided", b.isCollided());
        assertEquals("A collided with B", "B", a.getCollidedWithCarName());
        assertEquals("B collided with A", "A", b.getCollidedWithCarName());
        assertEquals("Collision happened at step 7", 7, a.getCollisionStep());
        assertEquals("Collision happened at step 7", 7, b.getCollisionStep());
        assertEquals("Collision position", new Position(5, 4), a.getPosition());
        assertEquals("Collision position", new Position(5, 4), b.getPosition());
    }

    private static void carsThatDoNotMeetDoNotCollide() {
        Field field = new Field(10, 10);
        Car a = new Car("A", new Position(0, 0), Direction.N, "F");
        Car b = new Car("B", new Position(9, 9), Direction.S, "F");
        new Simulator(field).run(List.of(a, b));

        assertTrue("A did not collide", !a.isCollided());
        assertTrue("B did not collide", !b.isCollided());
        assertEquals("A ends at (0,1)", new Position(0, 1), a.getPosition());
        assertEquals("B ends at (9,8)", new Position(9, 8), b.getPosition());
    }

    private static void shorterCommandListCarStaysPutButCanStillBeHit() {
        Field field = new Field(10, 10);
        // A only has one command, parks at (0,1) and sits idle after that.
        // B has two commands and drives into A's resting spot on its 2nd move.
        Car parkedCar = new Car("A", new Position(0, 0), Direction.N, "F");
        Car movingCar = new Car("B", new Position(0, 3), Direction.S, "FF");
        new Simulator(field).run(List.of(parkedCar, movingCar));

        assertTrue("Parked car A collided once B reaches it", parkedCar.isCollided());
        assertTrue("Moving car B collided with parked A", movingCar.isCollided());
        assertEquals("Collision at step 2 (B's 2nd command)", 2, movingCar.getCollisionStep());
        assertEquals("Collision position is (0,1)", new Position(0, 1), parkedCar.getPosition());
    }

    private static void carsStartingOnTheSameCellCollideImmediately() {
        Field field = new Field(10, 10);
        Car a = new Car("A", new Position(3, 3), Direction.N, "F");
        Car b = new Car("B", new Position(3, 3), Direction.S, "F");
        new Simulator(field).run(List.of(a, b));

        assertTrue("A collided at step 0", a.isCollided());
        assertTrue("B collided at step 0", b.isCollided());
        assertEquals("Collision recorded at step 0", 0, a.getCollisionStep());
    }

    private static void assertEquals(String description, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            recordPass(description);
        } else {
            recordFail(description, "expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertTrue(String description, boolean condition) {
        if (condition) {
            recordPass(description);
        } else {
            recordFail(description, "expected condition to be true");
        }
    }

    private static void recordPass(String description) {
        passed++;
        System.out.println("PASS - " + description);
    }

    private static void recordFail(String description, String detail) {
        failed++;
        System.out.println("FAIL - " + description + " (" + detail + ")");
    }
}

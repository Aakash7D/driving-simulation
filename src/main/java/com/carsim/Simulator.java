package com.carsim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs every car's commands at the same time, one command ("step") at a
 * time, and checks for collisions after each step.
 *
 * A few things worth knowing about how this works:
 * - cars move in lockstep: at step N, every car that still has an N-th
 *   command executes it. shorter command lists just stop moving early,
 *   but the car stays on the field and can still get hit later.
 * - moving off the field just gets ignored, same as the spec says.
 * - once a car has crashed it freezes - it stops taking commands, but it
 *   can still be involved in a later pile-up if another car drives into
 *   its resting spot.
 * - we only check collisions by final position after each step. two cars
 *   swapping cells in the same step (driving through each other) isn't
 *   detected - not covered by the spec or the worked examples, so I left
 *   it out rather than guessing at behaviour.
 */
public final class Simulator {

    private final Field field;

    public Simulator(Field field) {
        this.field = field;
    }

    public void run(List<Car> cars) {
        if (cars.isEmpty()) {
            return;
        }

        detectCollisions(cars, 0); // catches cars that start on the same cell

        int maxSteps = cars.stream()
                .mapToInt(car -> car.getCommands().length())
                .max()
                .orElse(0);

        for (int step = 1; step <= maxSteps; step++) {
            int commandIndex = step - 1;
            for (Car car : cars) {
                if (car.isCollided()) continue;
                if (commandIndex >= car.getCommands().length()) continue;
                applyCommand(car, car.getCommands().charAt(commandIndex));
            }
            detectCollisions(cars, step);
        }
    }

    private void applyCommand(Car car, char command) {
        switch (command) {
            case 'L':
                car.setDirection(car.getDirection().turnLeft());
                break;
            case 'R':
                car.setDirection(car.getDirection().turnRight());
                break;
            case 'F':
                Position next = car.getPosition().move(car.getDirection());
                if (field.contains(next)) {
                    car.setPosition(next);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown command '" + command + "'");
        }
    }

    private void detectCollisions(List<Car> cars, int step) {
        Map<Position, List<Car>> carsByPosition = new LinkedHashMap<>();
        for (Car car : cars) {
            carsByPosition.computeIfAbsent(car.getPosition(), p -> new ArrayList<>()).add(car);
        }

        for (List<Car> group : carsByPosition.values()) {
            if (group.size() < 2) continue;
            for (Car car : group) {
                if (car.isCollided()) continue;
                Car other = group.stream().filter(c -> c != car).findFirst().orElseThrow();
                car.markCollided(other.getName(), step);
            }
        }
    }
}

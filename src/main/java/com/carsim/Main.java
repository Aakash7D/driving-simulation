package com.carsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {

    private static final String VALID_COMMAND_CHARS = "LRF";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Car Crash Java!");
            new Main(scanner).run();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("No more input received. Goodbye!");
        }
    }

    private final Scanner scanner;

    private Main(Scanner scanner) {
        this.scanner = scanner;
    }

    private void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            Field field = promptForField();
            List<Car> cars = new ArrayList<>();

            boolean simulationRan = false;
            while (!simulationRan) {
                printCarList(cars);
                int choice = promptMenu(
                        "Please choose from the following options:",
                        "[1] Add a car to field",
                        "[2] Run simulation");

                if (choice == 1) {
                    addCar(field, cars);
                } else {
                    if (cars.isEmpty()) {
                        System.out.println();
                        System.out.println("You need to add at least one car before running the simulation.");
                        continue;
                    }
                    printCarList(cars);
                    new Simulator(field).run(cars);
                    printResults(cars);
                    simulationRan = true;
                }
            }

            int choice = promptMenu(
                    "Please choose from the following options:",
                    "[1] Start over",
                    "[2] Exit");
            keepRunning = (choice == 1);
        }

        System.out.println();
        System.out.println("Thank you for running the simulation. Goodbye!");
    }

    private Field promptForField() {
        while (true) {
            System.out.println();
            System.out.println("Please enter the width and height of the simulation field in x y format:");
            String[] parts = readLine().trim().split("\\s+");
            try {
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Expected two numbers, e.g. \"10 10\".");
                }
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                Field field = new Field(width, height);
                System.out.println();
                System.out.println("You have created a field of " + field + ".");
                return field;
            } catch (NumberFormatException e) {
                printError("Width and height must be whole numbers, e.g. \"10 10\".");
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
    }

    private void addCar(Field field, List<Car> cars) {
        String name = promptCarName(cars);

        Position position = null;
        Direction direction = null;
        while (position == null) {
            System.out.println();
            System.out.println("Please enter initial position of car " + name + " in x y Direction format:");
            String[] parts = readLine().trim().split("\\s+");
            try {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Expected \"x y Direction\", e.g. \"1 2 N\".");
                }
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                Position candidate = new Position(x, y);
                if (!field.contains(candidate)) {
                    throw new IllegalArgumentException(
                            "Position " + candidate + " is outside the " + field + " field.");
                }
                direction = Direction.fromCode(parts[2]);
                position = candidate;
            } catch (NumberFormatException e) {
                printError("x and y must be whole numbers, e.g. \"1 2 N\".");
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }

        String commands = promptCommands(name);
        cars.add(new Car(name, position, direction, commands));
        printCarList(cars);
    }

    private String promptCarName(List<Car> cars) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the name of the car:");
            String name = readLine().trim();
            if (name.isEmpty()) {
                printError("Car name cannot be empty.");
                continue;
            }
            boolean duplicate = cars.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (duplicate) {
                printError("A car named \"" + name + "\" already exists. Please choose a unique name.");
                continue;
            }
            return name;
        }
    }

    private String promptCommands(String carName) {
        while (true) {
            System.out.println();
            System.out.println("Please enter the commands for car " + carName + ":");
            String commands = readLine().trim().toUpperCase(Locale.ROOT);
            if (commands.isEmpty()) {
                printError("Commands cannot be empty. Use any combination of L, R and F.");
                continue;
            }
            if (!isValidCommandString(commands)) {
                printError("Commands may only contain the letters L, R and F.");
                continue;
            }
            return commands;
        }
    }

    private boolean isValidCommandString(String commands) {
        for (int i = 0; i < commands.length(); i++) {
            if (VALID_COMMAND_CHARS.indexOf(commands.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    private void printCarList(List<Car> cars) {
        System.out.println();
        if (cars.isEmpty()) {
            System.out.println("Your current list of cars is empty.");
            return;
        }
        System.out.println("Your current list of cars are:");
        for (Car car : cars) {
            System.out.println("- " + car.describeInitial());
        }
    }

    private void printResults(List<Car> cars) {
        System.out.println();
        System.out.println("After simulation, the result is:");
        for (Car car : cars) {
            System.out.println("- " + car.describeResult());
        }
    }

    private int promptMenu(String title, String... options) {
        while (true) {
            System.out.println();
            System.out.println(title);
            for (String option : options) {
                System.out.println(option);
            }
            System.out.println();
            String line = readLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= options.length) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the error below
            }
            printError("Please enter one of the numbers shown above.");
        }
    }

    private void printError(String message) {
        System.out.println("Error: " + message);
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException("No more input");
        }
        return scanner.nextLine();
    }
}

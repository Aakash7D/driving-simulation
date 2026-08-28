# Driving Simulation

CLI driving simulation - set up a rectangular field, place cars on it with
a list of movement commands, run it, and see where everyone ends up (or
who crashed into whom, and when).

## Requirements

Just a JDK on PATH (`java`, `javac`). Built and tested against JDK 21, no
other dependencies - no build tool, no third-party libraries.

## Running it

```bash
./start.sh
```

Compiles fresh into a temp directory and launches the CLI. Every run is a
brand new JVM process, so nothing carries over between runs - add cars, run
a simulation, exit, and the next run starts with a blank field again.

### Tests

```bash
./run_tests.sh
```

Compiles and runs the test suite (see below for why it's not JUnit).

## Using it

The flow:

1. Enter field width and height, e.g. `10 10`.
2. Menu: add a car, or run the simulation.
   - Adding a car asks for a unique name, its starting position + direction
     (`x y D`, e.g. `1 2 N`), and its command string (`L`/`R`/`F`, e.g.
     `FFRFFFFRRL`).
   - Add as many cars as you want before running.
3. After running, you get each car's final state:
   - `A, (5,4) S` - car A ended at (5,4) facing S.
   - `A, collides with B at (5,4) at step 7` - A crashed into B at (5,4)
     while executing its 7th command.
4. Then choose to start over or exit.

Bad input (out-of-range positions, unknown directions, malformed commands,
duplicate names, non-numeric menu choices) gets rejected with a message and
re-prompted - nothing crashes the session.

## How it's structured

- `Direction` - the four compass directions, movement vectors, turning
- `Position` - an immutable (x, y) point
- `Field` - the boundary and containment check
- `Car` - a car's state and collision info
- `Simulator` - runs everyone's commands and detects collisions
- `Main` - the CLI: prompts, validation, menu flow

`Simulator` has no I/O in it at all - it just operates on `Field`/`Car`
objects, which is what makes it easy to unit test without simulating
keyboard input.

### How the simulation runs

Cars move simultaneously, one command at a time. At "step N", every car
that still has an N-th command executes it; a car with a shorter command
list just stops moving once it's out of commands but stays parked on the
field. After each step, if two or more cars land on the same cell, they're
all marked collided, recording who they hit, where, and at what step.

I checked this against both worked examples in the problem statement by
hand before writing any code - traced car A's and car B's commands step by
step and confirmed they both land on (5,4) at step 7, matching the example
exactly. `SimulatorTest` has this as an automated check too.

## Assumptions / deviations

The spec doesn't fully define collision behaviour beyond the one worked
example, so here's what I decided and why:

- Collisions are checked after each step completes, by final position only.
  I'm not detecting two cars swapping cells mid-step (driving through each
  other) - not described in the spec, not exercised by either example, so I
  left it out rather than guess at intended behaviour.
- A collided car freezes - stops taking further commands, stays put. It can
  still get hit again later by a different car (a pile-up), but its own
  recorded collision doesn't change once set.
- Cars can have different-length command lists. A car that runs out just
  stops moving, doesn't disappear, and can still be hit later.
- Two cars starting on the same cell collide immediately, at "step 0",
  before any commands run.
- Car names must be unique (case-insensitive) - otherwise "which A collided
  with B" doesn't mean anything if two cars share a name.
- The example transcript has "heigh" instead of "height" in one spot - used
  the correct spelling in the actual prompt.
- Went with CLI over a browser UI since the worked examples are clearly a
  CLI transcript - matching that directly felt like the more faithful
  interpretation.

## Testing

`SimulatorTest` covers: direction turning both ways, field boundary edges,
a forward move getting blocked by the boundary, both worked examples
reproduced exactly (single car final state, and the two-car collision at
(5,4) step 7), cars that never meet not falsely colliding, a parked car
getting hit later by one still moving, and two cars starting on the same
cell.

No JUnit - the brief says no binaries in the submission, and a JUnit jar is
a binary. Pulling it in "properly" means wiring up Gradle/Maven just to
fetch it over the network, which felt like unnecessary weight for a problem
this size. So `SimulatorTest` is just plain Java with a tiny hand-rolled
assert/pass/fail counter - `javac`/`java` is all you need to run it.

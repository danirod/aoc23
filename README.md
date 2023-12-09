# aoc23

This year it is time for Java. They will hate you for using Java instead of something more cool, but I find it acceptable.

Not recording nor broadcasting my run this year. Go find someone else to backseat.

## Instructions

I don't have a fancy build system this year. Every day is a class with a main.

You can run this right clicking on your IDE of choice. Here is how I do it on a terminal:

    mvn exec:java -Dexec.mainClass="es.danirod.aoc.aoc23.DayXX"

To assist on the task, I made a script called `bin/problem` which accepts the day as a parameter and calls the proper main method.

## Tests?

Not usually, but some methods have been tested just for my calm. Use `mvn test` to run them or anyhow you use your IDE.

## Verification

I made a little verification script in case I refactor the project.

The `control` file is a static text file with all my solutions as copied and pasted from the AoC website.

Then `bin/control` will call `bin/problem` for every DayXX class found in the project. If I refactor and add a custom main method, this script should be modified so that it still runs every problem in order.

Finally, `bin/assert` makes a call to `comm` in order to compare the output of bin/control and control. It must match. If you see a diff, then it is because something is broken.

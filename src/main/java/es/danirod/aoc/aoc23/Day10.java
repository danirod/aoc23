package es.danirod.aoc.aoc23;


import es.danirod.aoc.aoc23.support.GridFile;
import es.danirod.aoc.aoc23.support.Point;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day10 {

    private GridFile grid;
    
    private PipeMap pipes;

    public Day10(String path) throws IOException {
        this.grid = new GridFile(AocUtils.fileAsLines(path));
        this.pipes = new PipeMap();
    }

    public long solution1() {
        Map<Point, Integer> distances = new HashMap<>();
        Deque<Point> queue = new ArrayDeque<>();
        Point start = pipes.start();
        int max = 0;
        
        // Starting point is always zero.
        distances.put(start, 0);
        
        // Then we start exploring.
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Point thisPoint = queue.pop();
            int thisDistance = distances.get(thisPoint);

            var links = pipes.biLink(thisPoint);
            for (var link : links) {
                if (!distances.containsKey(link)) {
                    // A new point to check.
                    var linkDistance = thisDistance + 1;
                    distances.put(link, linkDistance);
                    queue.add(link);
                    
                    // Update maximum
                    if (linkDistance > max)
                        max = linkDistance;
                }
            }
        }
        
        return max;
    }
    
    public long solution2() {
        var encloser = new MapEncloser();
        return encloser.area();
    }

    public static void main(String[] args) throws IOException {
        var problem = new Day10("inputs/10");
        System.out.println("10.1: " + problem.solution1());
        System.out.println("10.2: " + problem.solution2());
    }
        
    class PipeMap {
        
        public Point start() {
            return grid.cells()
                    .filter(c -> c.value() == 'S')
                    .map(c -> new Point(c.x(), c.y()))
                    .findFirst()
                    .get();
        }
        
        public char startTile() {
            Point s = start();
            Set<Point> me = connectToMe(s);
            if (me.size() != 2) {
                throw new RuntimeException("Sorry");
            }
            
            if (me.contains(s.north()) && me.contains(s.south())) {
                return '|';
            }
            if (me.contains(s.west()) && me.contains(s.east())) {
                return '-';
            }
            if (me.contains(s.north()) && me.contains(s.east())) {
                return 'L';
            }
            if (me.contains(s.north()) && me.contains(s.west())) {
                return 'J';
            }
            if (me.contains(s.west()) && me.contains(s.south())) {
                return '7';
            }
            if (me.contains(s.east()) && me.contains(s.south())) {
                return 'F';
            }
            throw new RuntimeException("WTF");
        }
        
        public Set<Point> connections(Point p) {
            Set<Point> points = new HashSet<>();
            var value = grid.at(p);
            switch (value) {
                case '|':
                    points.add(p.north());
                    points.add(p.south());
                    break;
                case '-':
                    points.add(p.west());
                    points.add(p.east());
                    break;
                case 'L':
                    points.add(p.north());
                    points.add(p.east());
                    break;
                case 'J':
                    points.add(p.north());
                    points.add(p.west());
                    break;
                case '7':
                    points.add(p.west());
                    points.add(p.south());
                    break;
                case 'F':
                    points.add(p.east());
                    points.add(p.south());
                    break;
            }
            points.removeIf(this::notRange);
            return points;
        }
                
        private boolean notRange(Point px) {
            return px.x() < 0 || px.y() < 0 || px.x() >= grid.cols() || px.y() >= grid.lines();
        }
        
        public Set<Point> connectToMe(Point p) {
            Set<Point> incoming = new HashSet<>();
            
            Point[] next = new Point[]{
                p.north(),
                p.east(),
                p.south(),
                p.west(),
            };
            for (Point n : next) {
                if (notRange(n))
                    continue;
                var links = connections(n);
                if (links.contains(p)) {
                    incoming.add(n);
                }
            }
            return incoming;
        }
        
        public Set<Point> biLink(Point p) {
            if (grid.at(p) == 'S') {
                return connectToMe(p);
            } else {
                var con = connectToMe(p);
                con.retainAll(connections(p));
                return con;
            }
        }
    }

    class MapCleaner {
        
        public char[][] clean() {
            Set<Point> points = loopPoints();
            char[][] map = new char[grid.lines()][grid.cols()];
            for (int y = 0; y < grid.lines(); y++) {
                Arrays.fill(map[y], '.');
            }
            points.forEach(p -> {
                map[p.y()][p.x()] = grid.at(p);
            });
            
            var start = pipes.start();
            map[start.y()][start.x()] = pipes.startTile();
            
            return map;
        }
        
        private Set<Point> loopPoints() {
            Set<Point> points = new HashSet<>();
            Deque<Point> queue = new ArrayDeque<>();
            Point start = pipes.start();

            // Then we start exploring.
            points.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                Point thisPoint = queue.pop();

                var links = pipes.biLink(thisPoint);
                for (var link : links) {
                    if (!points.contains(link)) {
                        // A new point to check.
                        points.add(link);
                        queue.add(link);
                    }
                }
            }
            
            return points;
        }   
    }
    
    class MapEncloser {
        
        private char[][] reference;
        
        private char[][] mutated;
        
        public MapEncloser() {
            reference = new MapCleaner().clean();
            mutated = new char[grid.lines()][grid.cols()];
            for (int y = 0; y < reference.length; y++)
                for (int x = 0; x < reference[y].length; x++)
                    mutated[y][x] = reference[y][x];
            enclose();
        }
        
        public int area() {
            int total = 0;
            for (int y = 0; y < mutated.length; y++) {
                for (int x = 0; x < mutated[y].length; x++) {
                    if (mutated[y][x] == 'I') {
                        total++;
                    }
                }
            }
            return total;
        }
        
        private void enclose() {
            for (int y = 0; y < mutated.length; y++) {
                for (int x = 0; x < mutated[y].length; x++) {
                    if (mutated[y][x] == '.') {
                        mutated[y][x] = process(x, y);
                    }
                }
            }
        }
        
        private char process(int x, int y) {
            int top = horizontalWalls(x, y, true);
            int bottom = horizontalWalls(x, y, false);
            /*int left = verticalWalls(x, y, true);
            int right = verticalWalls(x, y, false);*/
            if (top % 2 == 0 && bottom % 2 == 0 /*&& left % 2 == 0 && right % 2 == 0*/)
                return '0';
            return 'I';
        }
        
        private int horizontalWalls(int x, int y, boolean up) {
            int walls = 0;
            int step = up ? -1 : 1;
            for (int j = y; j >= 0 && j < reference.length; j += step) {
                switch (reference[j][x]) {
                    case '.':
                        // No wall.
                        break;
                    case '-':
                        // Horizontal wall.
                        walls++;
                        break;
                    case '|':
                        // Alone vertical wall without a corner? You can still
                        // sneak through this, as per one of the examples.
                        // Nothing to do, acts as no wall.
                        break;
                    case 'L':
                    case 'J':
                    case 'F':
                    case '7':
                        // These ones are interesting, we should find them
                        // first as a corner, then we should find a second
                        // corner if we continue following.
                        char oneEdge = reference[j][x];
                        
                        // Interrupt this loop to advance the j until we find
                        // the other edge. I use the j variable in order to
                        // skip the iterations of the outer for-loop of this
                        // function.
                        do {
                            j += step;
                        } while (j >= 0 && j < reference.length && reference[j][x] == '|');
                        
                        // So after this do-while, j points to the other corner.
                        char otherEdge = reference[j][x];
                        
                        // In the following case, you should be able to sneak
                        // through the pipes because it forms an aisle:
                        // L-----J
                        // F-----7
                        //
                        // But, I believe that if the edges were like this,
                        // I believe that you should not be able to walk over
                        // and thus should count as a wall.
                        // L-----7
                        if ((oneEdge == 'L' && otherEdge == '7') ||
                                (oneEdge == '7' && otherEdge == 'L') ||
                                (oneEdge == 'J' && otherEdge == 'F') ||
                                (oneEdge == 'F' && otherEdge == 'J')) {
                            walls++;
                        }
                        break;
                }
            }
            return walls;
        }
        
        private int verticalWalls(int x, int y, boolean left) {
            int walls = 0;
            int step = left ? -1 : 1;
            for (int i = x; left ? i >= 0 : i < reference[y].length; i += step) {
                switch (reference[y][i]) {
                    case '.':
                        // No wall.
                        break;
                    case '|':
                        // Vertical wall.
                        walls++;
                        break;
                    case '-':
                    case 'L':
                    case 'J':
                    case 'F':
                    case '7':
                        // Unfortunately, you can squeeze through this.
                        break;
                }
            }
            return walls;
        }
    }
    
    private static void printGrid(char[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            printGridLine(grid[y]);
        }
    }
    
    private static void printGrid(GridFile file) {
        for (int y = 0; y < file.lines(); y++) {
            printGridLine(file.row(y));
        }
    }
    
    private static void printGridLine(char[] line) {
        var spaced = Arrays.toString(line).replaceAll("[\\[\\],\\s]", "");
        System.out.println(spaced.replaceAll("L", "└")
                .replaceAll("\\.", " ")
                .replaceAll("J", "┘")
                .replaceAll("F", "┌")
                .replaceAll("-", "─")
                .replaceAll("\\|", "│")
                .replaceAll("7", "┐"));
    }
}

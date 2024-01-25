package es.danirod.aoc.aoc23.support;

public record Point(int x, int y) {

    public Point north() {
        return new Point(x, y - 1);
    }
    
    public Point south() {
        return new Point(x, y + 1);
    }
    
    public Point west() {
        return new Point(x - 1, y);
    }
    
    public Point east() {
        return new Point(x + 1, y);
    }
    
    public int taxicab(Point another) {
        return Math.abs(x - another.x) + Math.abs(y - another.y);
    }
}

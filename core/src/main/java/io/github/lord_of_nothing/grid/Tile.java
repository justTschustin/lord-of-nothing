package io.github.lord_of_nothing.grid;

public class Tile {
    public static final int BASE_TILE_SIZE = 32;

    private final int x;
    private final int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

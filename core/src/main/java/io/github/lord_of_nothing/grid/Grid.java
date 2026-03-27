package io.github.lord_of_nothing.grid;

public class Grid {
    private static final int width = 32;
    private static final int height = 18;
    private int hoveredX = -1;
    private int hoveredY = -1;
    private final Tile[][] tiles;

    public Grid() {
        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    public void setHovered(int x, int y) {
        this.hoveredX = x;
        this.hoveredY = y;
    }
    public Tile getTile(int x, int y) {
        if (!isInside(x, y)) {
            return null;
        }
        return tiles[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public int getHoveredX() { return hoveredX; }
    public int getHoveredY() { return hoveredY; }

}

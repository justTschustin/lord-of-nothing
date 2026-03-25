package io.github.lord_of_nothing.grid;

public class Grid {
    private static final int width = 16;
    private static final int height = 9;

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

    public Tile getTile(int x, int y) {
        if (!isInside(x, y)) {
            return null;
        }
        return tiles[x][y];
    }

    public void toggleTile(int x, int y) {
        if (!isInside(x, y)) {
            return;
        }
        tiles[x][y].setClicked(!tiles[x][y].isClicked());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

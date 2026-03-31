package io.github.lord_of_nothing.grid;

import io.github.lord_of_nothing.buildings.Building;

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

    /**
     * Places any buildng on set coordinates.
     */
    public void setBuilding(int x, int y, io.github.lord_of_nothing.buildings.Building building) {
        if (isInside(x, y)) {
            tiles[x][y].setBuilding(building);
        }
    }

    /**
     * <summary>Checks if a building of given dimensions can be placed at the target coordinates.</summary>
     * @param x Start X-coordinate. @param y Start Y-coordinate. @param w Width. @param h Height.
     */
    public boolean canPlace(int x, int y, int w, int h) {
        for (int ix = x; ix < x + w; ix++) {
            for (int iy = y; iy < y + h; iy++) {
                if (!isInside(ix, iy) || getTile(ix, iy).hasBuilding()) {return false;}
            }
        }
        return true;
    }

    /**
     * <summary>Places a building across multiple tiles and sets the root reference for rendering.</summary>
     */
    public void placeBuilding(int x, int y, Building b) {
        b.setRootPosition(x, y);
        for (int ix = x; ix < x + b.getWidth(); ix++) {
            for (int iy = y; iy < y + b.getHeight(); iy++) {
                tiles[ix][iy].setBuilding(b);
            }
        }
    }
}

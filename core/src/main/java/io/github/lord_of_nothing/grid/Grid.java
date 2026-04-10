package io.github.lord_of_nothing.grid;

import io.github.lord_of_nothing.buildings.Building;

/**
 * Represents the tile grid and placement logic for buildings.
 */
public class Grid {
    private static final int width = 27;
    private static final int height = 22;
    private int hoveredX = -1;
    private int hoveredY = -1;
    private final Tile[][] tiles;

    /**
     * Creates the grid and initializes all tiles.
     */
    public Grid() {
        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
    }

    /**
     * Returns whether the given coordinates are inside the grid.
     *
     * @param x tile x coordinate
     * @param y tile y coordinate
     * @return {@code true} if inside grid bounds
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Updates the currently hovered tile coordinate.
     *
     * @param x hovered tile x coordinate
     * @param y hovered tile y coordinate
     */
    public void setHovered(int x, int y) {
        this.hoveredX = x;
        this.hoveredY = y;
    }

    /**
     * Returns a tile at the given coordinate.
     *
     * @param x tile x coordinate
     * @param y tile y coordinate
     * @return tile instance or {@code null} when outside bounds
     */
    public Tile getTile(int x, int y) {
        if (!isInside(x, y)) {
            return null;
        }
        return tiles[x][y];
    }

    /**
     * Returns the grid width in tiles.
     *
     * @return width in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the grid height in tiles.
     *
     * @return height in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns hovered tile x coordinate.
     *
     * @return hovered x coordinate or {@code -1}
     */
    public int getHoveredX() { return hoveredX; }

    /**
     * Returns hovered tile y coordinate.
     *
     * @return hovered y coordinate or {@code -1}
     */
    public int getHoveredY() { return hoveredY; }

    /**
     * Places a building reference on a single tile.
     *
     * @param x tile x coordinate
     * @param y tile y coordinate
     * @param building building to place
     */
    public void setBuilding(int x, int y, Building building) {
        if (isInside(x, y)) {
            tiles[x][y].setBuilding(building);
        }
    }

    /**
     * Checks if a building of given dimensions can be placed at the target coordinates.
     * @param x Start X-coordinate
     * @param y Start Y-coordinate
     * @param w Width.
     * @param h Height.
     * @return {@code true} if all covered tiles are valid and empty
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
     * Places a building across multiple tiles and sets the root reference for rendering.
     *
     * @param x anchor tile x coordinate
     * @param y anchor tile y coordinate
     * @param b building to place
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

package io.github.lord_of_nothing.grid;

import io.github.lord_of_nothing.buildings.Building;

/**
 * Represents a single cell within the game grid, capable of holding a building instance.
 */
public class Tile {
    public static final int BASE_TILE_SIZE = 32;
    private TileType type = TileType.GRASS;
    private final int x;
    private final int y;
    private Building building = null;

    /**
     * Initializes a new tile at the specified grid coordinates.
     * @param x The horizontal index of the tile in the grid.
     * @param y The vertical index of the tile in the grid.
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if the tile is occupied by a building by verifying the building reference.
     * @return True if a building instance is present, false if the tile is empty.
     */
    public boolean hasBuilding() {
        return building != null;
    }

    /**
     * Gets the building currently placed on this tile.
     * @return The building instance or null if unoccupied.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets a building on this tile, effectively occupying it.
     * @param building The building instance to be placed on this tile.
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    public TileType getType() { return type; }
    /**
     * Changes the terrain type of this tile.
     */
    public void setType(TileType type) { this.type = type; }
}

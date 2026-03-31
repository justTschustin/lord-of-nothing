package io.github.lord_of_nothing.grid;

import io.github.lord_of_nothing.buildings.Building;

/**
 * <summary>Represents a single cell within the game grid, capable of holding a building instance.</summary>
 */
public class Tile {
    public static final int BASE_TILE_SIZE = 32;
    private TileType type = TileType.GRASS;
    private boolean hasBuilding = false;
    private final int x;
    private final int y;
    private Building building = null;

    /**
     * <summary>Initializes a new tile at the specified grid coordinates.</summary>
     * @param x The horizontal index of the tile in the grid.
     * @param y The vertical index of the tile in the grid.
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * <summary>Checks if the tile is occupied by a building by verifying the building reference.</summary>
     * @return True if a building instance is present, false if the tile is empty.
     */
    public boolean hasBuilding() {
        return building != null;
    }

    /**
     * <summary>Gets the building currently placed on this tile.</summary>
     * @return The building instance or null if unoccupied.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * <summary>Sets a building on this tile, effectively occupying it.</summary>
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
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

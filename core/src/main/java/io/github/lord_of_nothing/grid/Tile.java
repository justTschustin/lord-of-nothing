package io.github.lord_of_nothing.grid;

public class Tile {
    private io.github.lord_of_nothing.buildings.Building building;
    /**
     * Gibt das Gebäude auf diesem Feld zurück oder null, falls das Feld leer ist.
     */
    public static final int BASE_TILE_SIZE = 32;
    private TileType type = TileType.GRASS;
    private boolean hasBuilding = false;
    private final int x;
    private final int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean hasHouse() { return hasBuilding; }
    public void setHas(boolean hasHouse) { this.hasBuilding = hasHouse; }

    /**
Returns the building on this tile, or null if there is none.     */
    public io.github.lord_of_nothing.buildings.Building getBuilding() { return building; }
    /**
Places a building on this tile.
     * @param building The building to place.*/
    public void setBuilding(io.github.lord_of_nothing.buildings.Building building) { this.building = building; }
    /**
Checks if this tile has a building.
     */
    public boolean hasBuilding() { return building != null; }
    /**
     * Returns the terrain type of this tile.
     */
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

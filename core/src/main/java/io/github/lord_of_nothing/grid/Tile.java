package io.github.lord_of_nothing.grid;

public class Tile {
    private io.github.lord_of_nothing.buildings.Building building;
    /**
     * Gibt das Gebäude auf diesem Feld zurück oder null, falls das Feld leer ist.
     */
    public static final int BASE_TILE_SIZE = 32;
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
     * Gibt das Gebäude auf diesem Feld zurück oder null, falls das Feld leer ist.
     */
    public io.github.lord_of_nothing.buildings.Building getBuilding() { return building; }
    /**
     * Platziert ein beliebiges Gebäude auf dem Tile.
     */
    public void setBuilding(io.github.lord_of_nothing.buildings.Building building) { this.building = building; }
    /**
     * Prüft, ob das Feld mit einem Gebäude belegt ist.
     */
    public boolean hasBuilding() { return building != null; }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines the requirements for a building, via mapping.
 */
public abstract class Building {
    private final int width;
    private final int height;
    private int anchorX, anchorY;
    private int level = 1;
    private int citizenCapacity = 0;
    private int maxWorkers = 0;
    private int currentWorkers = 0;

    /**
     * Returns the unique key used to identify the building type, e.g., for texture lookups.
     *
     * @return string key identifying the building type.
     */
    public abstract String getBuildingTypeKey();

    protected final Map<ResourceType, Integer> costs = new EnumMap<>(ResourceType.class);

    public Map<ResourceType, Integer> getCosts() {
        return costs;
    }

    /**
     * Initializes a building with a specific footprint size on the game grid.
     *
     * @param width  The number of tiles the building occupies horizontally.
     * @param height The number of tiles the building occupies vertically.
     */
    public Building(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRootPosition(int x, int y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    /**
     * Identifies if the given coordinates represent the primary anchor point used for rendering multi-tile structures.
     * This check prevents the renderer from drawing a large building multiple times when iterating over its occupied tiles.
     * It returns true only for the specific origin coordinate assigned during the placement process on the grid.
     * <param name="x">The grid X-coordinate to check <param name="y">The grid Y-coordinate to check.
     */
    public boolean isAnchorPoint(int x, int y) {
        return this.anchorX == x && this.anchorY == y;
    }

    /**
     * Returns the current progression level of the building.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the current citizen capacity of this building. Only relevant for residential houses.
     */
    public int getCitizenCapacity() {
        return citizenCapacity;
    }

    protected void setCitizenCapacity(int cap) {
        this.citizenCapacity = cap;
    }

    /**
     * Returns the worker capacity of this building.
     */
    public int getMaxWorkers() {
        return maxWorkers;
    }

    protected void setMaxWorkers(int max) {
        this.maxWorkers = max;
    }

    /**
     * Returns the current amount of workers in this building.
     */
    public int getCurrentWorkers() {
        return currentWorkers;
    }

    /**
     * Increases the amount of workers in this building by 1, if space is available.
     */
    public void addWorker() {
        if (currentWorkers < maxWorkers) currentWorkers++;
    }
    /**
     * Decreases the amount of workers in this building by 1, if there are any.
     */
    public void removeWorker() {
        if (currentWorkers > 0) currentWorkers--;
    }
}

package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Base class for placeable buildings with footprint and resource costs.
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
     * @return string key identifying the building type.
     */
    public abstract String getBuildingTypeKey();

    protected final Map<ResourceType, Integer> costs = new EnumMap<>(ResourceType.class);

    /**
     * Returns the resource costs required to place this building.
     *
     * @return cost map by resource type
     */
    public Map<ResourceType, Integer> getCosts() {
        return costs;
    }

    /**
     * Initializes a building with a specific footprint size on the game grid.
     * @param width The number of tiles the building occupies horizontally.
     * @param height The number of tiles the building occupies vertically.
     */
    public Building(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns building width in tiles.
     *
     * @return width in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns building height in tiles.
     *
     * @return height in tiles
     */
    public int getHeight() {
        return height;
    }

    /**
     * Stores the anchor coordinate used to render multi-tile buildings once.
     *
     * @param x anchor tile x coordinate
     * @param y anchor tile y coordinate
     */
    public void setRootPosition(int x, int y) {
        this.anchorX = x;
        this.anchorY = y;
    }

    /**
     * Returns whether the given tile coordinate equals this building's anchor point.
     *
     * @param x tile x coordinate to check
     * @param y tile y coordinate to check
     * @return {@code true} if the coordinate is the building anchor
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
        if (currentWorkers < maxWorkers) {
            currentWorkers++;
        }
    }
    /**
     * Decreases the amount of workers in this building by 1, if there are any.
     */
    public void removeWorker() {
        if (currentWorkers > 0){ currentWorkers--;}
    }
}

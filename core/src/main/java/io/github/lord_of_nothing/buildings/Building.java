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
    private int rootX, rootY;

    /**
     * <summary>Returns the unique key used to identify the building type, e.g., for texture lookups.</summary>
     * @return A string key identifying the building type.
     */
    public abstract String getBuildingTypeKey();

    protected final Map<ResourceType, Integer> costs = new EnumMap<>(ResourceType.class);

    public Map<ResourceType, Integer> getCosts() {
        return costs;
    }

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
        this.rootX = x;
        this.rootY = y;
    }

    public boolean isRoot(int x, int y) {
        return this.rootX == x && this.rootY == y;
    }

}

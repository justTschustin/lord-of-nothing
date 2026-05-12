package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Represents a standard 1x1 residential building with wood costs.
 */
public class House extends Building {

    /**
     * Initializes the house with 1x1 dimensions and sets the resource costs.
     */
    public House() {
        super(1, 1); // Explicitly call the Building constructor
        costs.put(ResourceType.WOOD, 20);
        this.setCitizenCapacity(5);
    }

    /**
     * Returns the texture key for this building type.
     *
     * @return building type key
     */
    @Override
    public String getBuildingTypeKey() {
        return "house";
    }
}

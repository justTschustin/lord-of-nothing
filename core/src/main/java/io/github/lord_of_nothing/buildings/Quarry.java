package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Initializes a large-scale stone extraction site with a 2x2 footprint on the grid and costs of 30 wood.
 */
public class Quarry extends Building {
    public Quarry() {
        super(2, 2);
        costs.put(ResourceType.WOOD, 30);
        this.setMaxWorkers(4);
    }

    /**
     * Implementation of the type key for quarries.
     */
    @Override
    public String getBuildingTypeKey() {
        return "quarry";
    }
}

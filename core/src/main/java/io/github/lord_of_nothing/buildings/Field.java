package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Represents a basic 1x1 agricultural field used for food production.
 * Requires minimal wood resources for initial construction compared to larger structures.
 */
public class Field extends Building {
    public Field() {
        super(1, 1);
        costs.put(ResourceType.WOOD, 10);
        this.setMaxWorkers(2);
        this.setProduction(ResourceType.FOOD, 4);

    }

    @Override
    public String getBuildingTypeKey() { return "field"; }
}

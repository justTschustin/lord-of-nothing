package io.github.lord_of_nothing.buildings;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * <summary>Represents a military structure that converts available citizens into soldiers.</summary>
 * <remarks>Occupies a 2x2 area and can house up to 10 soldiers.</remarks>
 */
public class Barrack extends Building {
    public Barrack() {
        super(2, 2);
        costs.put(ResourceType.WOOD, 50);
        costs.put(ResourceType.STONE, 40);
        this.setMaxWorkers(10);
    }

    @Override
    public String getBuildingTypeKey() { return "barrack"; }
}

package io.github.lord_of_nothing.buildings;
import io.github.lord_of_nothing.resources.ResourceType;

/**
 * 2x1 sawmill building template.
 */
public class Sawmill extends Building {
    /**
     * Initializes a sawmill with a 2x1 footprint and costs of 20 wood.
     */
    public Sawmill() {
        super(2, 1);
        costs.put(ResourceType.WOOD, 40);
        this.setMaxWorkers(2);
        this.setProduction(ResourceType.WOOD, 1);
    }

    /**
     * Implementation of the type key for sawmills.
     *
     * @return building type key
     */
    @Override
    public String getBuildingTypeKey() {
        return "sawmill";
    }
}

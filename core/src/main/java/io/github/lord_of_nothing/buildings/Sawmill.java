package io.github.lord_of_nothing.buildings;
import io.github.lord_of_nothing.resources.ResourceType;

public class Sawmill extends Building {
    /**
     * <summary>Initializes a sawmill with a 2x1 footprint and costs of 20 wood.</summary>
     */
    public Sawmill() {
        super(2, 1);
        costs.put(ResourceType.WOOD, 20);
    }

    @Override
    public String getTexturePath() { return "buildings/sawmill.png"; }
}

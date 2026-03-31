package io.github.lord_of_nothing.buildings;
import io.github.lord_of_nothing.resources.ResourceType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Defines the requirements for a building, via mapping.
 */
public abstract class Building {
    protected final Map<ResourceType, Integer> costs = new EnumMap<>(ResourceType.class);

    public Map<ResourceType, Integer> getCosts() {
        return costs;
    }

    public abstract String getTexturePath();
}

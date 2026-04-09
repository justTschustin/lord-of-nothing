package io.github.lord_of_nothing.resources;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks resource amounts and applies resource transactions.
 */
public class ResourceManager {
    private final Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);

    /**
     * Creates a resource manager with all resource values initialized to zero.
     */
    public ResourceManager() {
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    /**
     * Returns the current amount for a resource type.
     *
     * @param type resource type
     * @return current amount
     */
    public int getAmount(ResourceType type) { return resources.getOrDefault(type, 0); }

    /**
     * Adds an amount to a resource type.
     *
     * @param type resource type
     * @param amount amount to add, may be negative
     */
    public void add(ResourceType type, int amount) {
        resources.put(type, getAmount(type) + amount);
    }

    /**
     * Returns whether the current amount meets or exceeds a required value.
     *
     * @param type resource type
     * @param amount required amount
     * @return {@code true} if enough resources are available
     */
    public boolean hasEnough(ResourceType type, int amount) {
        return getAmount(type) >= amount;
    }

    /**
     * Consumes a resource amount if enough units are available.
     *
     * @param type resource type
     * @param amount amount to consume
     * @return {@code true} if consumption succeeded
     */
    public boolean tryConsume(ResourceType type, int amount) {
        if (hasEnough(type, amount)) {
            add(type, -amount);
            return true;
        }
        return false;
    }
}

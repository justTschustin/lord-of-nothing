package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Read-only access to gameplay resource values.
 */
public interface ResourceStateView {
    int getResourceAmount(ResourceType type);

    boolean hasEnoughResources(ResourceType type, int amount);
}


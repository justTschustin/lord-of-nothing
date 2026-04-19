package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Read/write access to gameplay resource values.
 */
public interface ResourceStateMutator extends ResourceStateView {
    void addResource(ResourceType type, int amount);

    boolean tryConsumeResource(ResourceType type, int amount);
}


package io.github.lord_of_nothing.hud;

import io.github.lord_of_nothing.buildings.Building;

/**
 * State manager for the building inspection panel on the right side of the screen
 */
public class TileInspectorBar {
    private Building selectedBuilding = null;
    private int selectedGridX = -1;
    private int selectedGridY = -1;

    /**
     * Sets the specified building as the active selection for the information panel.
     * @param b The building instance to be displayed and inspected.
     * @param gridX Building's x position
     * @param gridY Building's y position
     */
    public void select(Building b, int gridX, int gridY) {
        this.selectedBuilding = b;
        this.selectedGridX = gridX;
        this.selectedGridY = gridY;
    }
    /**
     * Provides access to the currently selected building (& its position) for rendering and inspection purposes.
     * @return The building instance currently being inspected, or null if the panel is closed.
     * @return the x/y position of said building
     */
    public Building getSelected() { return selectedBuilding; }
    public int getSelectedGridX() { return selectedGridX; }
    public int getSelectedGridY() { return selectedGridY; }

    /**
     * Clears the current building selection and effectively hides the information panel
     */
    public void close() {
        this.selectedBuilding = null;
        this.selectedGridX = -1;
        this.selectedGridY = -1;
    }

    /**
     * Determines whether the information panel is currently active and holding a building reference.
     * @return True if a building is selected for inspection, false otherwise.
     */
    public boolean isOpen() { return selectedBuilding != null; }
}

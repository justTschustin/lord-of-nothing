package io.github.lord_of_nothing.hud;
import io.github.lord_of_nothing.buildings.Building;

/**
 * <summary>State manager for the building inspection panel on the right side of the screen.</summary>
 */
public class InfoSidebar {
    private Building selectedBuilding = null;

    /**
     Sets the specified building as the active selection for the information panel.
     * @param b The building instance to be displayed and inspected.
     */
    public void select(Building b) { this.selectedBuilding = b; }
    /**
     * Provides access to the currently selected building for rendering and inspection purposes.
     * @return The building instance currently being inspected, or null if the panel is closed.
     */
    public Building getSelected() {
        return selectedBuilding;
    }

    /**
     * <summary>Clears the current building selection and effectively hides the information panel.</summary>
     */
    public void close() { this.selectedBuilding = null; }

    /**
     Determines whether the information panel is currently active and holding a building reference.
     * @return True if a building is selected for inspection, false otherwise.
     */
    public boolean isOpen() { return selectedBuilding != null; }
}

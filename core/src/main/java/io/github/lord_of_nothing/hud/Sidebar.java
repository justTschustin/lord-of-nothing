package io.github.lord_of_nothing.hud;

import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.House;
import io.github.lord_of_nothing.buildings.Field;
import io.github.lord_of_nothing.buildings.Sawmill;
import io.github.lord_of_nothing.buildings.Quarry;


import java.util.ArrayList;
import java.util.List;

/**
 * Manages a dynamic list of available buildings and calculates their UI slot positions.
 */
public class Sidebar {
    private final List<Building> templates = new ArrayList<>();
    public static final int SLOT_SIZE = 60;
    public static final int PADDING = 20;

    public Sidebar() {
        templates.add(new House());
        templates.add(new Field());
        templates.add(new Sawmill());
        templates.add(new Quarry());
    }

    public List<Building> getTemplates() { return templates; }


    /**
     * Calculates the building template at the given coordinates using the exact same spacing as the renderer.
     * <remarks>Iterates through all templates to find a vertical match within the sidebar's screen area.</remarks>
     */
    public Building getBuildingAt(float x, float y) {
        if (x > io.github.lord_of_nothing.GameWindow.SIDEBAR_WIDTH) {return null;}

        int startY = com.badlogic.gdx.Gdx.graphics.getHeight() - io.github.lord_of_nothing.GameWindow.TOP_BAR_HEIGHT;

        for (int i = 0; i < templates.size(); i++) {
            float slotY = startY - (i + 1) * (SLOT_SIZE + PADDING);

            if (y >= slotY && y <= slotY + SLOT_SIZE) {
                return templates.get(i);
            }
        }
        return null;
    }
}

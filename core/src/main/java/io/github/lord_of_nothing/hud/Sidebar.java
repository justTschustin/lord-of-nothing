package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.House;
import io.github.lord_of_nothing.buildings.Sawmill;

import java.util.ArrayList;
import java.util.List;

/**
 * <summary>Manages a dynamic list of available buildings and calculates their UI slot positions.</summary>
 */
public class Sidebar {
    private final List<Building> templates = new ArrayList<>();
    public static final int SLOT_SIZE = 60;
    public static final int PADDING = 20;

    public Sidebar() {
        templates.add(new House());
        templates.add(new Sawmill());
    }

    public List<Building> getTemplates() { return templates; }

    public Building getBuildingAt(float x, float y) {
        if (x > GameWindow.SIDEBAR_WIDTH) return null;
        int startY = Gdx.graphics.getHeight() - GameWindow.TOP_BAR_HEIGHT;
        for (int i = 0; i < templates.size(); i++) {
            float slotY = startY - (i + 1) * (SLOT_SIZE + PADDING);
            if (y >= slotY && y <= slotY + SLOT_SIZE) return templates.get(i);
        }
        return null;
    }
}

package io.github.lord_of_nothing.select;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.GameSpeedChangedEvent;
import io.github.lord_of_nothing.settings.GameSettings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dropdown selector for simulation speed.
 */
public class GameSpeedSelector extends DropDownSelect {
    private final EventBus eventBus;

    /**
     * Creates a dropdown for selecting simulation speed.
     *
     * @param x selector x position
     * @param y selector y position
     * @param width selector width
     * @param height selector height
     * @param eventBus event bus used for UI registration and change events
     */
    public GameSpeedSelector(
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus
    ) {
        super(createOptions(), 0, x, y, width, height, eventBus);
        this.eventBus = eventBus;
    }

    /**
     * Syncs the current selection from persisted settings.
     *
     * @param settings loaded game settings
     */
    public void syncFromSettings(GameSettings settings) {
        if (settings == null) {
            return;
        }

        String expectedValue = String.valueOf(settings.gameSpeed);
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            if (expectedValue.equals(options.get(label))) {
                setSelectedIndex(i);
                return;
            }
        }

        setSelectedIndex(0);
    }

    /**
     * Applies option selection and emits a speed-change event when an item is picked.
     */
    @Override
    public void onClick() {
        if (!enabled) {
            return;
        }

        if (hoveredOptionIndex >= 0) {
            selectedIndex = hoveredOptionIndex;
            open = false;
            eventBus.publish(new GameSpeedChangedEvent(Integer.parseInt(getSelectedValue())));
            return;
        }

        open = !open;
    }

    /**
     * Creates the static speed options shown in the dropdown.
     *
     * @return ordered map of labels to speed multipliers
     */
    private static Map<String, String> createOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("1x", "1");
        options.put("2x", "2");
        options.put("4x", "4");
        return options;
    }
}



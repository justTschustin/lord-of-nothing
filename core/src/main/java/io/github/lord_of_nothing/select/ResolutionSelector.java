package io.github.lord_of_nothing.select;

import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ResolutionChangedEvent;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.ResolutionSettings;

public class ResolutionSelector extends DropDownSelect {
    protected EventBus eventBus;

    public ResolutionSelector(
        int selectedIndex,
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus
    ) {
        super(
            ResolutionSettings.getResolutionsStrings(),
            selectedIndex,
            x,
            y,
            width,
            height,
            eventBus
        );
        this.eventBus = eventBus;
    }

    public void syncFromSettings(GameSettings settings) {
        if (settings == null) {
            return;
        }

        String expectedLabel = settings.windowedWidth + "x" + settings.windowedHeight;
        for (int i = 0; i < labels.size(); i++) {
            if (expectedLabel.equals(labels.get(i))) {
                setSelectedIndex(i);
                return;
            }
        }

        String widthValue = String.valueOf(settings.windowedWidth);
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            if (widthValue.equals(options.get(label))) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public void onClick() {
        if (!enabled) return;

        // If an option row was clicked, select it
        if (hoveredOptionIndex >= 0) {
            selectedIndex = hoveredOptionIndex;
            open = false;
            eventBus.publish(new ResolutionChangedEvent(this.getSelectedValue()));
            return;
        }

        // Otherwise toggle the dropdown
        open = !open;
    }
}

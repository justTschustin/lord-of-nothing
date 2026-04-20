package io.github.lord_of_nothing.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.ui.UiElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic dropdown UI element with a header row and optional expanded options list.
 */
public class DropDownSelect implements UiElement {
    protected static final float OPTION_HEIGHT = 36f;
    private static final int MAX_VISIBLE_OPTIONS = 6;
    private static final Texture WHITE_PIXEL = createWhitePixel();
    private static final GlyphLayout GLYPH_LAYOUT = new GlyphLayout();

    protected final Rectangle bounds;
    protected final LinkedHashMap<String, String> options;
    protected final ArrayList<String> labels = new ArrayList<>();

    private final BitmapFont font = new BitmapFont();
    protected boolean enabled = true;
    protected boolean open = false;
    protected int hoveredOptionIndex = -1;
    protected int selectedIndex = -1;
    private int scrollOffset;

    /**
     * Creates a dropdown and optionally registers it as clickable UI.
     *
     * @param options label-to-value mapping in display order
     * @param selectedIndex initially selected row index
     * @param x dropdown x position
     * @param y dropdown y position
     * @param width dropdown width
     * @param height dropdown header height
     * @param eventBus event bus used for UI registration
     */
    public DropDownSelect(
        Map<String, String> options,
        int selectedIndex,
        float x,
        float y,
        float width,
        float height,
        EventBus eventBus
    ) {
        this.bounds = new Rectangle(x, y, width, height);
        this.options = new LinkedHashMap<>(options);
        this.labels.addAll(this.options.keySet());
        this.selectedIndex = selectedIndex;

        if (eventBus != null) {
            eventBus.publish(new UiElementCreatedEvent(this));
        }
    }

    /**
     * Returns the currently selected label.
     *
     * @return selected label or {@code null} if selection is invalid
     */
    public String getSelectedLabel() {
        if (selectedIndex < 0 || selectedIndex >= labels.size()) {
            return null;
        }
        return labels.get(selectedIndex);
    }

    /**
     * Returns the value of the currently selected label.
     *
     * @return selected value or {@code null} if selection is invalid
     */
    public String getSelectedValue() {
        String label = getSelectedLabel();
        return label == null ? null : options.get(label);
    }

    /**
     * Sets the selected option by index when the index is valid.
     *
     * @param selectedIndex option index to select
     */
    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= labels.size()) {
            return;
        }
        this.selectedIndex = selectedIndex;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public void setBounds(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }

    /**
     * Indicates whether the options list is currently expanded.
     *
     * @return {@code true} when expanded
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Scrolls the expanded options list by mouse-wheel amount.
     *
     * @param amountY wheel delta where positive values scroll downward in the list
     */
    public void scrollBy(float amountY) {
        if (!open || labels.size() <= MAX_VISIBLE_OPTIONS) {
            return;
        }

        int maxOffset = Math.max(0, labels.size() - getVisibleOptionCount());
        int nextOffset = scrollOffset + Math.round(amountY);
        scrollOffset = Math.max(0, Math.min(maxOffset, nextOffset));
    }

    @Override
    public boolean contains(float x, float y) {
        if (!enabled) {
            return false;
        }

        hoveredOptionIndex = -1;

        // Header click
        if (bounds.contains(x, y)) {
            return true;
        }

        // Expanded options
        if (!open) {
            return false;
        }

        int visibleCount = getVisibleOptionCount();
        for (int row = 0; row < visibleCount; row++) {
            float optionY = bounds.y - ((row + 1) * OPTION_HEIGHT);
            if (
                x >= bounds.x && x <= bounds.x + bounds.width &&
                    y >= optionY && y <= optionY + OPTION_HEIGHT
            ) {
                hoveredOptionIndex = scrollOffset + row;
                return true;
            }
        }

        return false;
    }

    /**
     * Triggers when option is selected
     */
    @Override
    public void onClick() {
        if (!enabled) {
            return;
        }

        // If an option row was clicked, select it
        if (hoveredOptionIndex >= 0) {
            selectedIndex = hoveredOptionIndex;
            open = false;
            return;
        }

        // Otherwise toggle the dropdown
        open = !open;
        if (open) {
            ensureSelectionIsVisible();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Color previous = batch.getColor();

        batch.setColor(0.22f, 0.22f, 0.22f, enabled ? 0.95f : 0.5f);
        batch.draw(WHITE_PIXEL, bounds.x, bounds.y, bounds.width, bounds.height);

        String selectedLabel = getSelectedLabel();
        if (selectedLabel != null) {
            GLYPH_LAYOUT.setText(font, selectedLabel);
            float textX = bounds.x + 10f;
            float textY = bounds.y + (bounds.height + GLYPH_LAYOUT.height) / 2f;

            batch.setColor(Color.WHITE);
            font.draw(batch, GLYPH_LAYOUT, textX, textY);
        }

        if (!open) {
            batch.setColor(previous);
            return;
        }

        int visibleCount = getVisibleOptionCount();
        for (int row = 0; row < visibleCount; row++) {
            int optionIndex = scrollOffset + row;
            if (optionIndex < 0 || optionIndex >= labels.size()) {
                continue;
            }

            float optionY = bounds.y - ((row + 1) * OPTION_HEIGHT);
            String label = labels.get(optionIndex);

            batch.setColor(optionIndex == hoveredOptionIndex ? 0.32f : 0.18f, 0.32f, 0.32f, 0.95f);
            batch.draw(WHITE_PIXEL, bounds.x, optionY, bounds.width, OPTION_HEIGHT);

            GLYPH_LAYOUT.setText(font, label);
            float textX = bounds.x + 10f;
            float textY = optionY + (OPTION_HEIGHT + GLYPH_LAYOUT.height) / 2f;

            batch.setColor(Color.WHITE);
            font.draw(batch, GLYPH_LAYOUT, textX, textY);
        }

        // Restore the incoming batch tint so other UI elements keep their own colors.
        batch.setColor(previous);
    }

    private int getVisibleOptionCount() {
        return Math.min(MAX_VISIBLE_OPTIONS, labels.size());
    }

    private void ensureSelectionIsVisible() {
        int visibleCount = getVisibleOptionCount();
        int maxOffset = Math.max(0, labels.size() - visibleCount);

        if (selectedIndex >= 0 && selectedIndex < labels.size()) {
            if (selectedIndex < scrollOffset) {
                scrollOffset = selectedIndex;
            } else if (selectedIndex >= scrollOffset + visibleCount) {
                scrollOffset = selectedIndex - visibleCount + 1;
            }
        }

        scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            open = false;
            hoveredOptionIndex = -1;
        }
    }

    private static Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}


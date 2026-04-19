package io.github.lord_of_nothing.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Contract for interactive UI elements that can be rendered and clicked.
 */
public interface UiElement {
    /**
     * Checks whether the element contains the given world coordinates.
     *
     * @param x x coordinate in world space
     * @param y y coordinate in world space
     * @return {@code true} if the element contains the coordinate
     */
    boolean contains(float x, float y);

    /**
     * Executes the element's click action.
     */
    void onClick();

    /**
     * Draws the element.
     *
     * @param batch sprite batch used for rendering
     */
    void render(SpriteBatch batch);

    /**
     * Indicates whether the element currently accepts interaction.
     *
     * @return {@code true} if the element is enabled
     */
    boolean isEnabled();

    /**
     * Sets whether the element accepts interaction.
     *
     * @param enabled enabled state to apply
     */
    void setEnabled(boolean enabled);
}

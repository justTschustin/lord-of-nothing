package io.github.lord_of_nothing.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface UiElement {
    boolean contains(float x, float y);
    void onClick();
    void render(SpriteBatch batch);
    boolean isEnabled();
    void setEnabled(boolean enabled);
}

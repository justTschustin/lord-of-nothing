package io.github.lord_of_nothing.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.MusicVolumeChangedEvent;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * A volume slider control for audio settings
 */
public class VolumeSlider implements UiElement {
    private float x, y, width, height;
    private float volume = 0.7f;
    private final EventBus eventBus;
    private final BitmapFont font = new BitmapFont();
    private boolean isDragging = false;
    private boolean enabled = true;

    public VolumeSlider(float x, float y, float width, float height, EventBus eventBus) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.eventBus = eventBus;
    }

    @Override
    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    @Override
    public void onClick() {
        // Not used for slider
    }

    @Override
    public void render(SpriteBatch batch) {
        // Batch has to end shortly for ShapeRenderer to be able to draw
        batch.end();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Draw slider background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(x, y, width, height);

        // Draw filled portion (volume level)
        shapeRenderer.setColor(0.4f, 0.8f, 0.4f, 1f);
        shapeRenderer.rect(x, y, width * volume, height);

        shapeRenderer.end();

        // Start Batch again
        batch.begin();

        // Draw text label
        font.setColor(Color.WHITE);
        font.draw(batch, "Volume: " + (int)(volume * 100) + "%", x, y + height / 2f + 5f);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void handleDrag(float mouseX, float mouseY, boolean isPressed) {
        if (isPressed && contains(mouseX, mouseY)) {
            isDragging = true;
        }

        if (!isPressed) {
            isDragging = false;
        }

        if (isDragging && enabled) {
            volume = Math.max(0, Math.min(1, (mouseX - x) / width));
            eventBus.publish(new MusicVolumeChangedEvent(volume));
        }
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void syncFromSettings(GameSettings gameSettings) {
        this.volume = gameSettings.masterVolume;
    }

    public float getHeight() {
        return height;
    }

    public void dispose() {
        font.dispose();
    }
}

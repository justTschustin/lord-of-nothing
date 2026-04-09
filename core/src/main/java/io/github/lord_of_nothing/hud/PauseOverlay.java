package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.BackToMainMenuButton;
import io.github.lord_of_nothing.button.ExitButton;
import io.github.lord_of_nothing.button.SettingsButton;
import io.github.lord_of_nothing.button.ResumeButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;

/**
 * Renders a pause overlay with resume, settings, menu, and exit actions.
 */
public class PauseOverlay {
    private static final float BUTTON_WIDTH = 150f;
    private static final float BUTTON_HEIGHT = 40f;
    private static final float BUTTON_GAP = 12f;

    private ResumeButton resumeButton;
    private SettingsButton settingsButton;
    private BackToMainMenuButton backToMainMenuButton;
    private ExitButton exitButton;
    private boolean shouldRegisterUiElements = true;

    /**
     * Enables or disables overlay button interaction.
     *
     * @param active whether overlay interaction should be active
     */
    public void setActive(boolean active) {
        if (resumeButton != null) {
            resumeButton.setEnabled(active);
        }
        if (settingsButton != null) {
            settingsButton.setEnabled(active);
        }
        if (exitButton != null) {
            exitButton.setEnabled(active);
        }
        if (backToMainMenuButton != null) {
            backToMainMenuButton.setEnabled(active);
        }
        if (!active) {
            shouldRegisterUiElements = true;
        }
    }

    /**
     * Renders the pause overlay and buttons.
     *
     * @param shapeRenderer shape renderer used for overlay background
     * @param batch sprite batch used for buttons
     * @param eventBus event bus used for UI element registration
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, EventBus eventBus) {
        setButtonLayout(eventBus);
        registerUiElementsIfNeeded(eventBus);
        resumeButton.setEnabled(true);
        settingsButton.setEnabled(true);
        backToMainMenuButton.setEnabled(true);
        exitButton.setEnabled(true);

        renderOverlay(shapeRenderer);

        batch.begin();
        resumeButton.render(batch);
        settingsButton.render(batch);
        backToMainMenuButton.render(batch);
        exitButton.render(batch);
        batch.end();
    }

    /**
     * Creates buttons when needed and updates their layout.
     *
     * @param eventBus event bus used when lazily creating buttons
     */
    private void setButtonLayout(EventBus eventBus) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float resumeY = centerY + BUTTON_HEIGHT + BUTTON_GAP;
        float settingsY = centerY;
        float mainMenuY = centerY - BUTTON_HEIGHT - BUTTON_GAP;
        float exitY = mainMenuY - BUTTON_HEIGHT - BUTTON_GAP;

        if (resumeButton == null) {
            resumeButton = new ResumeButton(x, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            resumeButton.setBounds(x, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        if (settingsButton == null) {
            settingsButton = new SettingsButton(x, settingsY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            settingsButton.setBounds(x, settingsY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        if (backToMainMenuButton == null) {
            backToMainMenuButton = new BackToMainMenuButton(x, mainMenuY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            backToMainMenuButton.setBounds(x, mainMenuY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        if (exitButton == null) {
            exitButton = new ExitButton(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        } else {
            exitButton.setBounds(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    /**
     * Registers overlay buttons once per overlay activation.
     *
     * @param eventBus event bus used for UI registration
     */
    private void registerUiElementsIfNeeded(EventBus eventBus) {
        if (!shouldRegisterUiElements) {
            return;
        }

        if (resumeButton != null) {
            eventBus.publish(new UiElementCreatedEvent(resumeButton));
        }
        if (settingsButton != null) {
            eventBus.publish(new UiElementCreatedEvent(settingsButton));
        }
        if (backToMainMenuButton != null) {
            eventBus.publish(new UiElementCreatedEvent(backToMainMenuButton));
        }
        if (exitButton != null) {
            eventBus.publish(new UiElementCreatedEvent(exitButton));
        }
        shouldRegisterUiElements = false;
    }

    /**
     * Forces overlay buttons to be re-registered on next render pass.
     */
    public void invalidateUiElements() {
        shouldRegisterUiElements = true;
    }

    /**
     * Draws the dimmed background overlay.
     *
     * @param shapeRenderer shape renderer used for overlay fill
     */
    private void renderOverlay(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}

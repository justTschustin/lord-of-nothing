package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.button.GameSpeedButton;
import io.github.lord_of_nothing.button.PauseButton;
import io.github.lord_of_nothing.button.TimeProgressionToggleButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.game.ResourceStateView;
import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Renders the top resource bar and its pause controls.
 */
public class TopBarRenderer {
    private static final int[] SUPPORTED_SPEEDS = {1, 2, 4};
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final PauseOverlay pauseOverlay = new PauseOverlay();
    private PauseButton pauseButton;
    private TimeProgressionToggleButton timeProgressionToggleButton;
    private final GameSpeedButton[] speedButtons = new GameSpeedButton[SUPPORTED_SPEEDS.length];

    private static final float PAUSE_BUTTON_WIDTH = 70f;
    private static final float PAUSE_BUTTON_HEIGHT = 24f;
    private static final float PAUSE_BUTTON_MARGIN = 10f;
    private static final float TIME_TOGGLE_BUTTON_WIDTH = 44f;
    private static final float TIME_TOGGLE_BUTTON_HEIGHT = 24f;
    private static final float SPEED_BUTTON_WIDTH = 44f;
    private static final float SPEED_BUTTON_HEIGHT = 24f;
    private static final float BUTTON_GAP = 6f;

    /**
     * Registers top-bar UI elements.
     *
     * @param window game window layout context
     * @param eventBus event bus used for UI element registration
     */
    public void registerUiElements(GameWindow window, EventBus eventBus) {
        setTopBarControlVariables(window, eventBus);
        if (timeProgressionToggleButton != null) {
            eventBus.publish(new UiElementCreatedEvent(timeProgressionToggleButton));
        }
        for (GameSpeedButton speedButton : speedButtons) {
            if (speedButton != null) {
                eventBus.publish(new UiElementCreatedEvent(speedButton));
            }
        }
        if (pauseButton != null) {
            eventBus.publish(new UiElementCreatedEvent(pauseButton));
        }
    }

    /**
     * Renders the top bar, resource counters, and optional pause overlay.
     *
     * @param shapeRenderer shape renderer for bar background
     * @param batch sprite batch for text and buttons
     * @param window game window layout context
     * @param resources current resource state
     * @param currentIngameDay current in-game day
     * @param currentIngameHour current in-game hour
     * @param eventBus event bus used for pause overlay UI registration
     * @param paused whether gameplay is currently paused
     * @param currentGameSpeed active simulation speed multiplier
     * @param timeProgressionPaused whether in-game time progression is paused
     * @param savingInProgress whether autosave is currently writing to disk
     */
    public void render(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        GameWindow window,
        ResourceStateView resources,
        int currentIngameDay,
        int currentIngameHour,
        EventBus eventBus,
        boolean paused,
        int currentGameSpeed,
        boolean timeProgressionPaused,
        boolean savingInProgress
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, window.getTopBarY(), Gdx.graphics.getWidth(), GameWindow.TOP_BAR_HEIGHT);
        shapeRenderer.end();

        setTopBarControlVariables(window, eventBus);
        pauseButton.setEnabled(!paused);
        updateControlButtonState(currentGameSpeed, !paused, timeProgressionPaused);
        pauseOverlay.setActive(paused);

        batch.begin();
        float y = window.getTopBarY() + 25;
        float x = 10;
        float gap = 30;
        String[] labels = {
            "WOOD: " + resources.getResourceAmount(ResourceType.WOOD),
            "STONE: " + resources.getResourceAmount(ResourceType.STONE),
            "FOOD: " + resources.getResourceAmount(ResourceType.FOOD),
            "CITIZENS: " + resources.getResourceAmount(ResourceType.CITIZENS_TOTAL),
            "CAPACITY: " + resources.getResourceAmount(ResourceType.CITIZENS_CAPACITY),
            "SOLDIERS: " + resources.getResourceAmount(ResourceType.SOLDIERS),
            "Day: " + currentIngameDay + " Time: " + currentIngameHour + ":00"
        };
        for (String label : labels) {
            font.draw(batch, label, x, y);
            glyphLayout.setText(font, label);
            x += glyphLayout.width + gap;
        }

        if (savingInProgress) {
            String savingText = "saving...";
            glyphLayout.setText(font, savingText);
            float controlsLeftX = pauseButton.getX();
            if (speedButtons[0] != null) {
                controlsLeftX = speedButtons[0].getX();
            }
            if (timeProgressionToggleButton != null) {
                controlsLeftX = timeProgressionToggleButton.getX();
            }
            float savingX = controlsLeftX - glyphLayout.width - 18f;
            font.draw(batch, savingText, Math.max(10f, savingX), y);
        }

        if (timeProgressionToggleButton != null) {
            timeProgressionToggleButton.render(batch);
        }
        for (GameSpeedButton speedButton : speedButtons) {
            if (speedButton != null) {
                speedButton.render(batch);
            }
        }
        pauseButton.render(batch);
        batch.end();

        if (paused) {
            pauseOverlay.render(shapeRenderer, batch, eventBus);
        }
    }

    /**
     * Marks pause-overlay UI elements for re-registration on next render.
     */
    public void invalidatePauseOverlayUiElements() {
        pauseOverlay.invalidateUiElements();
    }

    /**
     * Creates or repositions the pause button based on current window size.
     *
     * @param window game window layout context
     * @param eventBus event bus used when creating the button
     */
    private void setTopBarControlVariables(GameWindow window, EventBus eventBus) {
        float pauseX = Gdx.graphics.getWidth() - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        float y = window.getTopBarY() + (GameWindow.TOP_BAR_HEIGHT - PAUSE_BUTTON_HEIGHT) / 2f;

        if (pauseButton == null) {
            pauseButton = new PauseButton(pauseX, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, eventBus);
        } else {
            pauseButton.setBounds(pauseX, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
        }

        float speedY = window.getTopBarY() + (GameWindow.TOP_BAR_HEIGHT - SPEED_BUTTON_HEIGHT) / 2f;
        float timeButtonY = window.getTopBarY() + (GameWindow.TOP_BAR_HEIGHT - TIME_TOGGLE_BUTTON_HEIGHT) / 2f;
        float speedGroupLeftX = pauseX
            - BUTTON_GAP
            - (speedButtons.length * SPEED_BUTTON_WIDTH)
            - ((speedButtons.length - 1) * BUTTON_GAP);
        for (int i = speedButtons.length - 1; i >= 0; i--) {
            float buttonX = speedGroupLeftX + (i * (SPEED_BUTTON_WIDTH + BUTTON_GAP));

            if (speedButtons[i] == null) {
                speedButtons[i] = new GameSpeedButton(
                    buttonX,
                    speedY,
                    SPEED_BUTTON_WIDTH,
                    SPEED_BUTTON_HEIGHT,
                    SUPPORTED_SPEEDS[i],
                    eventBus
                );
            } else {
                speedButtons[i].setBounds(buttonX, speedY, SPEED_BUTTON_WIDTH, SPEED_BUTTON_HEIGHT);
            }
        }

        float timeButtonX = speedGroupLeftX - BUTTON_GAP - TIME_TOGGLE_BUTTON_WIDTH;

        if (timeProgressionToggleButton == null) {
            timeProgressionToggleButton = new TimeProgressionToggleButton(
                timeButtonX,
                timeButtonY,
                TIME_TOGGLE_BUTTON_WIDTH,
                TIME_TOGGLE_BUTTON_HEIGHT,
                eventBus
            );
        } else {
            timeProgressionToggleButton.setBounds(
                timeButtonX,
                timeButtonY,
                TIME_TOGGLE_BUTTON_WIDTH,
                TIME_TOGGLE_BUTTON_HEIGHT
            );
        }
    }

    private void updateControlButtonState(int currentGameSpeed, boolean enabled, boolean timeProgressionPaused) {
        if (timeProgressionToggleButton != null) {
            timeProgressionToggleButton.setEnabled(enabled);
            timeProgressionToggleButton.setSelected(timeProgressionPaused);
        }

        for (GameSpeedButton speedButton : speedButtons) {
            if (speedButton == null) {
                continue;
            }
            speedButton.setEnabled(enabled);
            speedButton.setSelected(!timeProgressionPaused && speedButton.getGameSpeed() == currentGameSpeed);
        }
    }

    /**
     * Disposes renderer-owned font resources.
     */
    public void dispose() { font.dispose(); }
}

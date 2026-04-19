package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.Button;
import io.github.lord_of_nothing.button.ExitButton;
import io.github.lord_of_nothing.button.SettingsButton;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.LoadGameEvent;
import io.github.lord_of_nothing.events.NewGameEvent;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders and manages the main menu UI.
 */
public class MainMenu {
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final List<Button> actionButtons = new ArrayList<>();
    private final SettingsButton settingsButton;
    private final ExitButton exitButton;

    /**
     * Creates the main menu and its default buttons.
     *
     * @param eventBus event bus used to wire button actions
     * @param hasSaveFile whether a loadable save file exists
     */
    public MainMenu(EventBus eventBus, boolean hasSaveFile) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;

        if (hasSaveFile) {
            actionButtons.add(new TextButton(
                x,
                centerY + BUTTON_HEIGHT + BUTTON_GAP,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                eventBus,
                "Load Game",
                () -> eventBus.publish(new LoadGameEvent()),
                true
            ));
        }

        actionButtons.add(new TextButton(
            x,
            centerY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "New Game",
            () -> eventBus.publish(new NewGameEvent()),
            true
        ));

        settingsButton = new SettingsButton(x, centerY - BUTTON_HEIGHT - BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        exitButton = new ExitButton(x, centerY - 2f * (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        setButtonLayout();
    }

    /**
     * Registers menu buttons as interactive UI elements.
     *
     * @param eventBus event bus used for publishing UI element creation
     */
    public void registerUiElements(EventBus eventBus) {
        setButtonLayout();
        for (Button actionButton : actionButtons) {
            eventBus.publish(new UiElementCreatedEvent(actionButton));
        }
        eventBus.publish(new UiElementCreatedEvent(settingsButton));
        eventBus.publish(new UiElementCreatedEvent(exitButton));
    }

    /**
     * Renders the menu background and all buttons.
     *
     * @param shapeRenderer shape renderer used for background
     * @param batch sprite batch used for text and buttons
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        setButtonLayout();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Lord of Nothing", Gdx.graphics.getWidth() / 2f - 47f, Gdx.graphics.getHeight() / 2f + 140f);
        for (Button actionButton : actionButtons) {
            actionButton.render(batch);
        }
        settingsButton.render(batch);
        exitButton.render(batch);
        batch.end();
    }

    /**
     * Disposes menu-owned rendering resources.
     */
    public void dispose() {
        font.dispose();

    }

    /**
     * Recalculates button layout based on the current window size.
     */
    private void setButtonLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;

        for (int i = 0; i < actionButtons.size(); i++) {
            float y = centerY + (actionButtons.size() - i - 1f) * (BUTTON_HEIGHT + BUTTON_GAP);
            actionButtons.get(i).setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        float settingsY = centerY - BUTTON_HEIGHT - BUTTON_GAP;
        float exitY = centerY - 2f * (BUTTON_HEIGHT + BUTTON_GAP);
        settingsButton.setBounds(x, settingsY, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.setBounds(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
}

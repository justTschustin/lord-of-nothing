package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
 * Renders and manages the main menu UI & background.
 */
public class MainMenu {
    private static final float BUTTON_WIDTH = 275f;
    private static final float BUTTON_HEIGHT = 75f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final List<Button> actionButtons = new ArrayList<>();
    private final SettingsButton settingsButton;
    private final ExitButton exitButton;
    private final Texture titleText;
    private final BackgroundManager backgroundManager;

    /**
     * Creates the main menu and its default buttons.
     *
     * @param eventBus event bus used to wire button actions
     * @param hasSaveFile whether a loadable save file exists
     */
    public MainMenu(EventBus eventBus, boolean hasSaveFile) {
        backgroundManager = new BackgroundManager(
            "menu/main-menu-background.png",
            "menu/main-menu-clouds.png",
            "menu/main-menu-background-blurred.png",
            "menu/main-menu-clouds-blurred.png"
        );
        titleText = new Texture("menu/title-text.png");

        float x = getPositionX();
        float baseY = getBaseY();

        if (hasSaveFile) {
            actionButtons.add(new TextButton(
                x,
                baseY + BUTTON_HEIGHT + BUTTON_GAP,
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
            baseY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "New Game",
            () -> eventBus.publish(new NewGameEvent()),
            true
        ));

        settingsButton = new SettingsButton(x, baseY - BUTTON_HEIGHT - BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        exitButton = new ExitButton(x, baseY - 2f * (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
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

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        batch.begin();

        backgroundManager.update(Gdx.graphics.getDeltaTime());
        backgroundManager.render(batch, screenW, screenH, false);

        // Title
        float aspect = 470f / 90f;
        float titleW = BUTTON_WIDTH + 250;
        float titleH = titleW / aspect;
        float titleX = getPositionX() + (BUTTON_WIDTH - titleW) / 2f;
        float titleY = getBaseY() + actionButtons.size() * (BUTTON_HEIGHT + BUTTON_GAP) + 20f;

        // Title shadow
        float shadowOffset = 3f;
        batch.setColor(0f, 0f, 0f, 0.5f);
        batch.draw(titleText, titleX + shadowOffset, titleY - shadowOffset, titleW, titleH);

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(titleText, titleX, titleY, titleW, titleH);

        // Buttons
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
        titleText.dispose();
        backgroundManager.dispose();
    }

    /**
     * Recalculates button layout based on the current window size.
     */
    private void setButtonLayout() {
        float x = getPositionX();
        float baseY = getBaseY();

        for (int i = 0; i < actionButtons.size(); i++) {
            float y = baseY + (actionButtons.size() - i - 1f) * (BUTTON_HEIGHT + BUTTON_GAP);
            actionButtons.get(i).setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        settingsButton.setBounds(x, baseY - BUTTON_HEIGHT - BUTTON_GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.setBounds(x, baseY - 2f * (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    /**
     * Returns x position for left edge of button column & aligns buttons
     */
    private float getPositionX() {
        float positionStart = Gdx.graphics.getWidth() * 0.57f;
        float menuWidth = Gdx.graphics.getWidth() * 0.33f;
        return positionStart + (menuWidth - BUTTON_WIDTH) / 2f;
    }

    /**
     * Returns y position for bottom edge of topmost button
     */
    private float getBaseY() {
        return Gdx.graphics.getHeight() / 2f - 40f;
    }

    public BackgroundManager getBackgroundManager() {
        return backgroundManager;
    }
}

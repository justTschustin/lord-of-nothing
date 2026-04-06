package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.ExitButton;
import io.github.lord_of_nothing.button.SettingsButton;
import io.github.lord_of_nothing.button.StartGameButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;

public class MainMenu {
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final StartGameButton startGameButton;
    private final SettingsButton settingsButton;
    private final ExitButton exitButton;

    public MainMenu(EventBus eventBus) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float startY = centerY + BUTTON_HEIGHT + BUTTON_GAP;
        float settingsY = centerY;
        float exitY = settingsY - BUTTON_HEIGHT - BUTTON_GAP;

        startGameButton = new StartGameButton(x, startY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        settingsButton = new SettingsButton(x, settingsY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        exitButton = new ExitButton(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        setButtonLayout();
    }

    public void registerUiElements(EventBus eventBus) {
        setButtonLayout();
        eventBus.publish(new UiElementCreatedEvent(startGameButton));
        eventBus.publish(new UiElementCreatedEvent(settingsButton));
        eventBus.publish(new UiElementCreatedEvent(exitButton));
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        setButtonLayout();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Lord of Nothing", Gdx.graphics.getWidth() / 2f - 90f, Gdx.graphics.getHeight() / 2f + 110f);
        startGameButton.render(batch);
        settingsButton.render(batch);
        exitButton.render(batch);
        batch.end();
    }

    public void dispose() {
        font.dispose();

    }

    private void setButtonLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float startY = centerY + BUTTON_HEIGHT + BUTTON_GAP;
        float settingsY = centerY;
        float exitY = settingsY - BUTTON_HEIGHT - BUTTON_GAP;

        startGameButton.setBounds(x, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setBounds(x, settingsY, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.setBounds(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
}

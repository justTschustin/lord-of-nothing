package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.ExitButton;
import io.github.lord_of_nothing.button.StartGameButton;
import io.github.lord_of_nothing.events.EventBus;

public class MainMenu {
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final StartGameButton startGameButton;
    private final ExitButton exitButton;

    public MainMenu(EventBus eventBus) {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float startY = centerY + BUTTON_GAP / 2f;
        float exitY = startY - BUTTON_HEIGHT - BUTTON_GAP;

        startGameButton = new StartGameButton(x, startY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
        exitButton = new ExitButton(x, exitY, BUTTON_WIDTH, BUTTON_HEIGHT, eventBus);
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Lord of Nothing", Gdx.graphics.getWidth() / 2f - 90f, Gdx.graphics.getHeight() / 2f + 110f);
        startGameButton.render(batch);
        exitButton.render(batch);
        batch.end();
    }

    public void dispose() {
        font.dispose();

    }
}

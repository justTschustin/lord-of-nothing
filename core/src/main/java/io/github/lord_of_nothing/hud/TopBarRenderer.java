package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Übernimmt die visuelle Darstellung der Ressourcenleiste am oberen Bildschirmrand.
 * Zeichnet den Hintergrund der Bar und die aktuellen Werte aus dem ResourceManager.
 */
public class TopBarRenderer {
    private final BitmapFont font = new BitmapFont();

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, GameWindow window, ResourceManager resourceManager) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, window.getTopBarY(), Gdx.graphics.getWidth(), GameWindow.TOP_BAR_HEIGHT);
        shapeRenderer.end();

        batch.begin();
        int spacing = 150;
        int i = 0;
        for (ResourceType type : ResourceType.values()) {
            font.draw(batch, type.name() + ": " + resourceManager.getAmount(type), 20 + (i * spacing), window.getTopBarY() + 25);
            i++;
        }
        batch.end();
    }

    public void dispose() { font.dispose(); }
}

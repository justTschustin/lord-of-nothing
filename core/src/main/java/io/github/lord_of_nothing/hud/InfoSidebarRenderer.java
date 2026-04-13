package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

import java.util.Map;

/**
 * <summary>Visualizes building details such as the sprite, type name, and level within the info panel bounds.</summary>
 * <remarks>Uses a BitmapFont for text rendering and coordinates from GameWindow for relative positioning.</remarks>
 */
public class InfoSidebarRenderer {
    private final BitmapFont font = new BitmapFont();

    /**
     * <summary>Visualizes building details and worker management by accessing the global resource manager.</summary>
     * @param rm The resource manager used to display the current pool of available citizens.
     */
    public void render(ShapeRenderer sr, SpriteBatch batch, GameWindow window, InfoSidebar state, Map<String, Texture> textures, ResourceManager rm) {
        if (!state.isOpen()) {return;}

        float x = window.getRightMarginX();
        float yBase = window.getInfoPanelY();
        float w = window.getRightMarginWidth();
        float h = window.getInfoPanelHeight();

        // 1. Background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        sr.rect(x, yBase, w, h);
        sr.end();

        Building b = state.getSelected();
        batch.begin();

        // 2. Sprite (centered in top part of panel)
        float spriteSize = w * 0.5f;
        float spriteX = x + (w - spriteSize) / 2f;
        float spriteY = yBase + h - spriteSize - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteSize, spriteSize);

        // 3. Text (Name + Level)
        font.setColor(Color.WHITE);
        String infoText = b.getBuildingTypeKey().toUpperCase() + " LVL " + b.getLevel();
        font.draw(batch, infoText, x + 20, spriteY - 20);

        // 4. Workers & Available pool
        if (b.getMaxWorkers() > 0) {
            String workerInfo = "Workers: " + b.getCurrentWorkers() + "/" + b.getMaxWorkers();
            String availInfo = "Available: " + rm.getAmount(ResourceType.CITIZENS_AVAILABLE);

            font.draw(batch, workerInfo + "  (" + availInfo + ")", x + 20, yBase + 100);
            font.draw(batch, "[+] Add        [-] Remove", x + 20, yBase + 70);
        }
        batch.end();
    }

    public void dispose() { font.dispose(); }
}

package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.button.DeleteBuildingButton;
import java.util.Map;

/**
 * Visualizes building details such as the sprite, type name, and level within the info panel bounds
 * Uses a BitmapFont for text rendering and coordinates from GameWindow for relative positioning
 */
public class TileInspectorRenderer {
    private final BitmapFont font = new BitmapFont();
    private DeleteBuildingButton deleteButton;

    public void render(
        ShapeRenderer sr,
        SpriteBatch batch,
        GameWindow window,
        TileInspectorBar state,
        Map<String, Texture> textures,
        Runnable onDelete
    ) {
        if (!state.isOpen()) {return;}

        float x = window.getRightMarginX();
        float y = window.getInfoPanelY();
        float w = window.getRightMarginWidth();
        float h = window.getInfoPanelHeight();

        // 1. Background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        sr.rect(x, y, w, h);
        sr.end();

        Building b = state.getSelected();
        batch.begin();

        // 2. Sprite (centered in top part of panel)
        float spriteSize = w * 0.5f;
        float spriteX = x + (w - spriteSize) / 2f;
        float spriteY = y + h - spriteSize - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteSize, spriteSize);

        // 3. Text (Name + Level)
        font.setColor(Color.WHITE);
        String infoText = b.getBuildingTypeKey().toUpperCase() + " LVL " + b.getLevel();
        font.draw(batch, infoText, x + 20, spriteY - 20);

        batch.end();

        // 4. Delete Button
        float btnH = 30f;
        float btnY = y + 50;
        float btnX = x + 10;
        float btnW = w - 20;

        if (deleteButton == null) {
            deleteButton = new DeleteBuildingButton(btnX, btnY, btnW, btnH, onDelete);
        } else {
            deleteButton.setBounds(btnX, btnY, btnW, btnH);
        }
        deleteButton.render(sr, batch);
    }

    /**
     * Forwards a world-coordinate click to the delete button.
     * Called by GridInputHandler.touchDown() before other click logic.
     * @return true if the delete button consumed the click
     */
    public boolean handleInput(float worldX, float worldY) {
        if (deleteButton == null) return false;
        return deleteButton.handleClick(worldX, worldY);
    }

    public void dispose() {
        font.dispose();
        if (deleteButton != null) deleteButton.dispose();
    }
}


package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.button.DeleteBuildingButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.game.ResourceStateView;
import io.github.lord_of_nothing.resources.ResourceType;
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
        EventBus eventBus,
        ResourceStateView resources,
        Runnable onDelete
    ) {
        float panelX = window.getRightMarginX();
        float panelY = window.getInfoPanelY();
        float panelW = window.getRightMarginWidth();
        float panelH = window.getInfoPanelHeight();
        float btnX = panelX + 10;
        float btnY = panelY + 50;
        float btnW = panelW - 20;
        float btnH = 30f;

        // Creates button once
        if (deleteButton == null) {
            deleteButton = new DeleteBuildingButton(btnX, btnY, btnW, btnH, eventBus, onDelete);
        } else {
            deleteButton.setBounds(btnX, btnY, btnW, btnH);
        }

        deleteButton.setEnabled(state.isOpen());

        if (!state.isOpen()) { return; }

        // 1. Background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        Building b = state.getSelected();
        batch.begin();

        // 2. Sprite (centered in top part of panel)
        float spriteSize = panelW * 0.5f;
        float spriteX = panelX + (panelW - spriteSize) / 2f;
        float spriteY = panelY + panelH - spriteSize - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteSize, spriteSize);

        // 3. Name + Level
        font.setColor(Color.WHITE);
        String infoText = b.getBuildingTypeKey().toUpperCase() + " LVL " + b.getLevel();
        font.draw(batch, infoText, spriteX + 20, spriteY - 20);

        // 4. Worker assignment (only for buildings that accept workers)
        if (b.getMaxWorkers() > 0) {
            int available = resources.getResourceAmount(ResourceType.CITIZENS_AVAILABLE);
            String workerInfo = "Workers: " + b.getCurrentWorkers() + "/" + b.getMaxWorkers()
                + "  (Available: " + available + ")";
            font.draw(batch, workerInfo, panelX + 10, panelY + 135);
            font.draw(batch, "[+] Add        [-] Remove", panelX + 10, panelY + 115);
        }

        batch.end();

        // 5. Delete Button
        deleteButton.render(sr, batch);
    }

    public void dispose() {
        font.dispose();
        if (deleteButton != null) { deleteButton.dispose(); }
    }
}


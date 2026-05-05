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
    private Texture bgTexture, cornerTexture, edgeTexture;

    /**
     * Renders the building inspector panel using an ornate frame and tiled background.
     * Replaces the simple shape-based background with decorative assets to match the overall HUD style.
     */
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

        if (deleteButton == null) {
            deleteButton = new DeleteBuildingButton(btnX, btnY, btnW, btnH, eventBus, onDelete);
        } else {
            deleteButton.setBounds(btnX, btnY, btnW, btnH);
        }

        deleteButton.setEnabled(state.isOpen());
        if (!state.isOpen()) { return; }

        // 1. Draw Ornate Frame
        batch.begin();
        renderDecoratedFrameAt(batch, panelX, panelY, panelW, panelH, 30f);
        batch.end();

        Building b = state.getSelected();
        boolean isPlaced = state.getSelectedGridX() != -1;

        batch.begin();
        // 2. Sprite
        float spriteH = panelW * 0.4f;
        float spriteW = spriteH * ((float) b.getWidth() / b.getHeight());
        float spriteX = panelX + (panelW - spriteW) / 2f;
        float spriteY = panelY + panelH - spriteH - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteW, spriteH);

        // 3. Name + Level
        font.setColor(Color.WHITE);
        font.draw(batch, b.getBuildingTypeKey().toUpperCase() + " LVL " + b.getLevel(),
            panelX + 10, spriteY - 20, panelW - 20,
            com.badlogic.gdx.utils.Align.center, true
        );

        // 4. Content (Workers or Preview)
        if (isPlaced && b.getMaxWorkers() > 0) {
            int available = resources.getResourceAmount(ResourceType.CITIZENS_AVAILABLE);
            String workerInfo = "Workers: " + b.getCurrentWorkers() + "/" + b.getMaxWorkers()
                + "  (Available: " + available + ")";
            font.draw(batch, workerInfo, panelX + 10, panelY + 135);
            font.draw(batch, "[+] Add        [-] Remove", panelX + 10, panelY + 115);
        }
        batch.end();

        if (isPlaced) {
            deleteButton.render(sr, batch);
        }
    }

    /**
     * Internal helper to draw the tiled background and mirrored ornate edges/corners.
     */
    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y, float w, float h, float cSize) {
        float eH = 8f;
        batch.draw(bgTexture, x, y, w, h, 0, 0, (int)w, (int)h, false, false);

        int edgeTexH = edgeTexture.getHeight();
        batch.draw(edgeTexture, x + cSize, y + h - eH, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexH, false, false);
        batch.draw(edgeTexture, x + cSize, y, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexH, false, true);
        batch.draw(edgeTexture, x, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int)(h - 2 * cSize), false, false);
        batch.draw(edgeTexture, x + w - eH, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int)(h - 2 * cSize), true, false);

        int sW = cornerTexture.getWidth(), sH = cornerTexture.getHeight();
        batch.draw(cornerTexture, x, y + h - cSize, cSize, cSize, 0, 0, sW, sH, false, false);
        batch.draw(cornerTexture, x + w - cSize, y + h - cSize, cSize, cSize, 0, 0, sW, sH, true, false);
        batch.draw(cornerTexture, x + w - cSize, y, cSize, cSize, 0, 0, sW, sH, true, true);
        batch.draw(cornerTexture, x, y, cSize, cSize, 0, 0, sW, sH, false, true);
    }

    /**
     * Initializes the decorative textures and configures tiling modes for the inspector panel.
     * Setting the wrap mode to Repeat ensures the background texture tiles seamlessly across the panel's area.
     */
    public void loadAssets(Texture bg, Texture corner, Texture edge) {
        this.bgTexture = bg;
        this.cornerTexture = corner;
        this.edgeTexture = edge;
        this.bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.edgeTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }
    public void dispose() {
        font.dispose();
        if (deleteButton != null) { deleteButton.dispose(); }
    }
}


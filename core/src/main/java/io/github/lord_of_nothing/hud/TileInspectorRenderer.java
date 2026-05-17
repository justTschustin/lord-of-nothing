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
import io.github.lord_of_nothing.game.HungerLevel;
import io.github.lord_of_nothing.game.HungerMechanic;
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
        Runnable onDelete,
        boolean paused,
        HungerMechanic hunger
    ) {
        float panelX = window.getRightMarginX();
        float panelY = window.getInfoPanelY();
        float panelW = window.getRightMarginWidth();
        float panelH = window.getInfoPanelHeight();
        float btnX = panelX + 10;
        float btnY = panelY + 50;
        float btnW = panelW - 20;
        float btnH = 30f;

        float dimAlpha = paused ? 0.4f : 1.0f;

        boolean isPlaced = state.isOpen() && state.getSelectedGridX() != -1;
        if (deleteButton == null) {
            deleteButton = new DeleteBuildingButton(btnX, btnY, btnW, btnH, eventBus, onDelete);
        } else {
            deleteButton.setBounds(btnX, btnY, btnW, btnH);
        }

        deleteButton.setEnabled(isPlaced);

        // 1. Always draw the panel background and frame to cover the gap
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f * dimAlpha);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();
        batch.begin();
        batch.setColor(1f, 1f, 1f, dimAlpha);
        renderDecoratedFrameAt(batch, panelX, panelY, panelW, panelH, 30f);
        batch.setColor(Color.WHITE);
        batch.end();

        if (!state.isOpen()) {
            if (hunger != null) {
                renderHungerBar(sr, batch, panelX, panelY, panelW, panelH, hunger, dimAlpha);
            }
            return;
        }

        Building b = state.getSelected();

        batch.begin();
        batch.setColor(1f, 1f, 1f, dimAlpha);
        // 2. Sprite
        float spriteH = panelW * 0.4f;
        float spriteW = spriteH * ((float) b.getWidth() / b.getHeight());
        float spriteX = panelX + (panelW - spriteW) / 2f;
        float spriteY = panelY + panelH - spriteH - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteW, spriteH);

        font.setColor(1f, 1f, 1f, dimAlpha);
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
        } else if (!isPlaced) {
            renderBuildingPreview(batch, b, panelX, spriteY - 60, dimAlpha);
        }
        batch.setColor(Color.WHITE);
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

    /**
     * <summary>Renders the building preview stats including costs, housing, workers, and yield.</summary>
     * @param batch The sprite batch used for drawing text. @param b The building template to inspect.
     * @param panelX The horizontal start position of the panel. @param startY The vertical starting position for the text.
     */
    private void renderBuildingPreview(SpriteBatch batch, Building b, float panelX, float startY, float dimAlpha) {
        float statsY = startY;

        font.setColor(1f, 1f, 0f, dimAlpha);
        font.draw(batch, "CONSTRUCTION COSTS:", panelX + 10, statsY);
        statsY -= 20;

        font.setColor(1f, 1f, 1f, dimAlpha);
        for (java.util.Map.Entry<io.github.lord_of_nothing.resources.ResourceType, Integer> entry : b.getCosts().entrySet()) {
            font.draw(batch, "- " + entry.getKey().name() + ": " + entry.getValue(), panelX + 20, statsY);
            statsY -= 15;
        }

        statsY -= 10;
        if (b.getCitizenCapacity() > 0) {
            font.draw(batch, "HOUSING: + " + b.getCitizenCapacity() + " Citizens", panelX + 10, statsY);
            statsY -= 20;
        }

        if (b.getMaxWorkers() > 0) {
            font.draw(batch, "MAX WORKERS: " + b.getMaxWorkers(), panelX + 10, statsY);
            statsY -= 20;
        }

        if (b.getProductionType() != null) {
            String yieldText = "YIELD: " + b.getProductionPerWorker() + " " + b.getProductionType().name() + "/Hour per worker";
            font.draw(batch, yieldText, panelX + 10, statsY);
        }
    }
    /**
     * Draws the hunger bar with a red-to-white gradient and tier info when no building is selected.
     */
    private void renderHungerBar(ShapeRenderer sr, SpriteBatch batch,
                                  float panelX, float panelY, float panelW, float panelH,
                                  HungerMechanic hunger, float dimAlpha) {
        float level = hunger.getHungerLevel();
        HungerLevel tier = hunger.getCurrentTier();

        float padding = 14f;
        float barW = panelW - 2 * padding;
        float barH = 20f;
        // Place the bar near the top of the panel
        float barX = panelX + padding;
        float barY = panelY + panelH - 70f;
        float fillW = barW * level;

        // --- Background bar (dark) ---
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.05f, 0.05f, 0.9f * dimAlpha);
        sr.rect(barX, barY, barW, barH);

        if (fillW > 0f) {
            // Gradient fill: left = pure red, right = lerp(red → white) at fill endpoint
            // c1=BL, c2=BR, c3=TR, c4=TL
            Color left = new Color(0.9f, 0.05f, 0.05f, dimAlpha);
            Color right = new Color(1f, level, level, dimAlpha); // lerp red→white
            sr.rect(barX, barY, fillW, barH, left, right, right, left);
        }

        // Thin border around full bar area
        sr.setColor(0.5f, 0.3f, 0.3f, dimAlpha);
        sr.rect(barX, barY, barW, 1f);           // bottom
        sr.rect(barX, barY + barH - 1f, barW, 1f); // top
        sr.rect(barX, barY, 1f, barH);           // left
        sr.rect(barX + barW - 1f, barY, 1f, barH); // right
        sr.end();

        // --- Labels ---
        batch.begin();
        font.setColor(1f, 1f, 1f, dimAlpha);

        // Title
        font.draw(batch, "HUNGER",
            panelX, barY + barH + 22f,
            panelW, com.badlogic.gdx.utils.Align.center, false);

        // Percentage
        String pctText = Math.round(level * 100) + " %";
        font.draw(batch, pctText,
            panelX, barY - 4f,
            panelW, com.badlogic.gdx.utils.Align.center, false);

        // Tier name (colour-coded)
        switch (tier) {
            case STARVING:  font.setColor(1f, 0.15f, 0.15f, dimAlpha); break;
            case HUNGRY:    font.setColor(1f, 0.6f,  0.1f,  dimAlpha); break;
            case SATISFIED: font.setColor(0.8f, 1f,  0.4f,  dimAlpha); break;
            case WELL_FED:  font.setColor(0.4f, 1f,  0.4f,  dimAlpha); break;
        }
        font.draw(batch, tier.getDisplayName(),
            panelX, barY - 22f,
            panelW, com.badlogic.gdx.utils.Align.center, false);

        // Effect list
        font.setColor(0.85f, 0.85f, 0.85f, dimAlpha);
        float effectY = barY - 44f;
        for (String effect : tier.getEffects()) {
            font.draw(batch, effect,
                panelX, effectY,
                panelW, com.badlogic.gdx.utils.Align.center, false);
            effectY -= 16f;
        }

        batch.end();
    }

    public void dispose() {
        font.dispose();
        if (deleteButton != null) { deleteButton.dispose(); }
    }
}


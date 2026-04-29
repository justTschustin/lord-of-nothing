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

        boolean isPlaced = state.isOpen() && state.getSelectedGridX() != -1;

        if (deleteButton == null) {
            deleteButton = new DeleteBuildingButton(btnX, btnY, btnW, btnH, eventBus, onDelete);
        } else {
            deleteButton.setBounds(btnX, btnY, btnW, btnH);
        }

        deleteButton.setEnabled(isPlaced);

        if (!state.isOpen()) { return; }

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        Building b = state.getSelected();
        batch.begin();

        float spriteH = panelW * 0.4f;
        float spriteW = spriteH * ((float) b.getWidth() / b.getHeight());
        float spriteX = panelX + (panelW - spriteW) / 2f;
        float spriteY = panelY + panelH - spriteH - 20;
        batch.draw(textures.get(b.getBuildingTypeKey()), spriteX, spriteY, spriteW, spriteH);

        font.setColor(Color.WHITE);
        font.draw(batch,b.getBuildingTypeKey().toUpperCase() + " LVL " + b.getLevel(),
            panelX + 10, spriteY - 20, panelW - 20,
            com.badlogic.gdx.utils.Align.center, true
        );

        if (isPlaced && b.getMaxWorkers() > 0) {
            int available = resources.getResourceAmount(ResourceType.CITIZENS_AVAILABLE);
            String workerInfo = "Workers: " + b.getCurrentWorkers() + "/" + b.getMaxWorkers()
                + "  (Available: " + available + ")";
            font.draw(batch, workerInfo, panelX + 10, panelY + 135);
            font.draw(batch, "[+] Add        [-] Remove", panelX + 10, panelY + 115);
        }  else if (!isPlaced) {
            renderBuildingPreview(batch, b, panelX, spriteY - 60);
        }
        batch.end();

        if (isPlaced) {
            deleteButton.render(sr, batch);
        }
    }

    /**
     * <summary>Renders the building preview stats including costs, housing, workers, and yield.</summary>
     * @param batch The sprite batch used for drawing text. @param b The building template to inspect.
     * @param panelX The horizontal start position of the panel. @param startY The vertical starting position for the text.
     */
    private void renderBuildingPreview(SpriteBatch batch, Building b, float panelX, float startY) {
        float statsY = startY;

        font.setColor(Color.YELLOW);
        font.draw(batch, "CONSTRUCTION COSTS:", panelX + 10, statsY);
        statsY -= 20;

        font.setColor(Color.WHITE);
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
    public void dispose() {
        font.dispose();
        if (deleteButton != null) { deleteButton.dispose(); }
    }
}


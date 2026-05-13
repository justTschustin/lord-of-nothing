package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import java.util.Map;

/**
 * Renders the game grid, terrain, buildings, and the interactive sidebar UI.
 */
public class GridRenderer {

    private final BitmapFont overlayFont = createOverlayFont();

    /**
     * Main render loop coordinating terrain, grid shapes, buildings, and sidebar UI.
     *
     * @param shapeRenderer geometry renderer for non-textured shapes
     * @param batch sprite renderer for textured content
     * @param grid grid model
     * @param window window layout context
     * @param buildingTextures texture map keyed by building type
     * @param grassTex grass texture used for terrain
     * @param pendingBuilding building selected for grid placement
     */
    public void render(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        Grid grid,
        GameWindow window,
        Map<String, Texture> buildingTextures,
        Texture grassTex,
        Building pendingBuilding
    ) {
        renderBackground(batch, grid, window, grassTex);
        renderGridShapes(shapeRenderer, grid, window);
        renderBuildings(batch, grid, window, buildingTextures);
        renderWorkerOverlays(batch, grid, window);
        renderPreview(batch, grid, window, buildingTextures, pendingBuilding);
    }

    /**
     * Renders a semi-transparent preview of a building on hovered tile
     *
     * @param batch sprite batch used for rendering
     * @param grid grid model
     * @param window window layout context
     * @param buildingTextures texture map keyed by building type
     * @param pendingBuilding building selected for placement (null if none)
     */
    private void renderPreview(
        SpriteBatch batch,
        Grid grid,
        GameWindow window,
        Map<String, Texture> buildingTextures,
        Building pendingBuilding
    ) {
        if (pendingBuilding == null) { return; }
        int hx = grid.getHoveredX();
        int hy = grid.getHoveredY();
        if (hx == -1) { return; }

        Texture tex = buildingTextures.get(pendingBuilding.getBuildingTypeKey());
        if (tex == null) { return; }

        boolean valid = grid.canPlace(hx, hy, pendingBuilding.getWidth(), pendingBuilding.getHeight());

        batch.begin();
        if (valid) {
            batch.setColor(1f, 1f, 1f, 0.5f); // white (semi-transparent)
        } else {
            batch.setColor(1f, 0.2f, 0.2f, 0.5f); // red (semi-transparent)
        }
        batch.draw(
            tex,
            window.getOffsetX() + hx * window.getTileSize(),
            window.getOffsetY() + hy * window.getTileSize(),
            window.getTileSize() * pendingBuilding.getWidth(),
            window.getTileSize() * pendingBuilding.getHeight()
        );
        batch.setColor(Color.WHITE);
        batch.end();
    }

    /**
     * Generic building renderer that draws buildings at their root tile using their specified dimensions.
     *
     * @param batch sprite batch used for rendering
     * @param grid grid model
     * @param window window layout context
     * @param buildingTextures texture map keyed by building type
     */
    private void renderBuildings(
        SpriteBatch batch,
        Grid grid,
        GameWindow window,
        Map<String, Texture> buildingTextures
    ) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile.hasBuilding() && tile.getBuilding().isAnchorPoint(x, y)) {
                    Building b = tile.getBuilding();
                    Texture tex = buildingTextures.get(b.getBuildingTypeKey());
                    if (tex != null) {
                        batch.draw(
                            tex,
                            window.getOffsetX() + x * window.getTileSize(),
                            window.getOffsetY() + y * window.getTileSize(),
                            window.getTileSize() * b.getWidth(),
                            window.getTileSize() * b.getHeight()
                        );
                    }
                }
            }
        }
        batch.end();
    }

    /**
     * Draws the tiled grass background under the buildable area.
     *
     * @param batch sprite batch used for rendering
     * @param grid grid model
     * @param window window layout context
     * @param grassTex grass texture
     */
    private void renderBackground(SpriteBatch batch, Grid grid, GameWindow window, Texture grassTex) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                batch.draw(grassTex, window.getOffsetX() + x * window.getTileSize(), window.getOffsetY() + y * window.getTileSize(), window.getTileSize(), window.getTileSize());
            }
        }
        batch.end();
    }

    /**
     * Draws a small worker-count badge (currentWorkers/maxWorkers) in the bottom-right
     * corner of every building that has assigned workers.
     */
    private void renderWorkerOverlays(SpriteBatch batch, Grid grid, GameWindow window) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (!tile.hasBuilding()) continue;
                Building b = tile.getBuilding();
                if (!b.isAnchorPoint(x, y) || b.getMaxWorkers() <= 0) continue;
                float bx = window.getOffsetX() + x * window.getTileSize();
                float by = window.getOffsetY() + y * window.getTileSize();
                float bw = window.getTileSize() * b.getWidth();
                overlayFont.draw(batch, String.valueOf(b.getCurrentWorkers()), bx + bw - 14f, by + 14f);
            }
        }
        batch.end();
    }

    private static BitmapFont createOverlayFont() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/Fredoka-variable-font.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 16;
        p.color = Color.WHITE;
        p.borderWidth = 0.5f;
        BitmapFont f = gen.generateFont(p);
        gen.dispose();
        return f;
    }

    public void dispose() {
        overlayFont.dispose();
    }

    /**
     * Draws the hovered tile highlight.
     *
     * @param shapeRenderer shape renderer used for hover highlight
     * @param grid grid model
     * @param window window layout context
     */
    private void renderGridShapes(ShapeRenderer shapeRenderer, Grid grid, GameWindow window) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (grid.getHoveredX() != -1) {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.4f);
            shapeRenderer.rect(window.getOffsetX() + grid.getHoveredX() * window.getTileSize(), window.getOffsetY() + grid.getHoveredY() * window.getTileSize(), window.getTileSize(), window.getTileSize());
        }
        shapeRenderer.end();
    }
}

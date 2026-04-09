package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;
import java.util.Map;

/**
 * Renders the game grid, terrain, buildings, and the interactive sidebar UI.
 */
public class GridRenderer {

    /**
     * Main render loop coordinating terrain, grid shapes, buildings, and sidebar UI.
     *
     * @param shapeRenderer geometry renderer for non-textured shapes
     * @param batch sprite renderer for textured content
     * @param grid grid model
     * @param window window layout context
     * @param buildingTextures texture map keyed by building type
     * @param grassTex grass texture used for terrain
     * @param pendingBuilding currently selected building template
     * @param rm current resource state
     */
    public void render(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        Grid grid,
        GameWindow window,
        Map<String, Texture> buildingTextures,
        Texture grassTex,
        Building pendingBuilding,
        ResourceManager rm
    ) {
        renderBackground(batch, grid, window, grassTex);
        renderGridShapes(shapeRenderer, grid, window);
        renderBuildings(batch, grid, window, buildingTextures);
    }


    /**
     * Renders a sidebar icon with affordability tinting.
     *
     * @param batch sprite batch used for rendering
     * @param tex icon texture
     * @param x icon x coordinate
     * @param y icon y coordinate
     * @param rm current resource state
     * @param b building template whose costs are evaluated
     */
    private void renderSidebarIcon(SpriteBatch batch, Texture tex, int x, int y, ResourceManager rm, Building b) {
        boolean canAfford = true;
        for (Map.Entry<ResourceType, Integer> cost : b.getCosts().entrySet()) {
            if (!rm.hasEnough(cost.getKey(), cost.getValue())) {canAfford = false;}
        }

        if (!canAfford) {batch.setColor(Color.RED);}
        if (tex != null) {batch.draw(tex, x, y, 40, 40);
            batch.setColor(Color.WHITE);}
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
                        batch.draw(tex,
                            window.getOffsetX() + x * window.getTileSize(),
                            window.getOffsetY() + y * window.getTileSize(),
                            window.getTileSize() * b.getWidth(),
                            window.getTileSize() * b.getHeight());
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

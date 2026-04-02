package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.House;
import io.github.lord_of_nothing.buildings.Sawmill;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;
import java.util.Map;

/**
 * <summary>Renders the game grid, terrain, buildings, and the interactive sidebar UI.</summary>
 */
public class GridRenderer {

    /**
     * <summary>Main render loop coordinating terrain, grid shapes, buildings, and sidebar UI.</summary>
     * @param shapeRenderer Geometry renderer. @param batch Sprite renderer. @param grid Grid data. @param window Layout context.
     * @param buildingTextures Map of textures for buildings. @param grassTex Terrain texture. @param pendingBuilding Currently selected building. @param rm Resource state.
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Grid grid, GameWindow window, Map<String, Texture> buildingTextures, Texture grassTex, Building pendingBuilding, ResourceManager rm) {
        renderBackground(batch, grid, window, grassTex);
        renderGridShapes(shapeRenderer, grid, window);
        renderBuildings(batch, grid, window, buildingTextures);
    }


    /**
     * <summary>Helper to render a single sidebar icon with resource-based color tinting.</summary>
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
     * <summary>Generic building renderer that draws buildings at their root tile using their specified dimensions.</summary>
     */
    private void renderBuildings(SpriteBatch batch, Grid grid, GameWindow window, Map<String, Texture> buildingTextures) {
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

    private void renderBackground(SpriteBatch batch, Grid grid, GameWindow window, Texture grassTex) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                batch.draw(grassTex, window.getOffsetX() + x * window.getTileSize(), window.getOffsetY() + y * window.getTileSize(), window.getTileSize(), window.getTileSize());
            }
        }
        batch.end();
    }

    private void renderGridShapes(ShapeRenderer shapeRenderer, Grid grid, GameWindow window) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (grid.getHoveredX() != -1) {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.4f);
            shapeRenderer.rect(window.getOffsetX() + grid.getHoveredX() * window.getTileSize(), window.getOffsetY() + grid.getHoveredY() * window.getTileSize(), window.getTileSize(), window.getTileSize());
        }
        shapeRenderer.end();
    }
}

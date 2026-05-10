package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.game.ResourceStateView;

import java.util.Map;

/**
 * Renders sidebar slots and building icons.
 */
public class SidebarRenderer {
    private Texture bgTexture, cornerTexture, edgeTexture;
    /**
     * Draws the sidebar background and building entries.
     *
     * @param sr shape renderer used for sidebar geometry
     * @param batch sprite batch used for icon rendering
     * @param sidebar sidebar model with templates
     * @param textures texture map keyed by building type
     * @param pending currently selected building template
     * @param resources resource state used to tint unaffordable entries
     */
    public void render(ShapeRenderer sr, SpriteBatch batch, Sidebar sidebar, Map<String, Texture> textures, Building pending, ResourceStateView resources) {
        int width = io.github.lord_of_nothing.GameWindow.SIDEBAR_WIDTH;
        int height = Gdx.graphics.getHeight() - io.github.lord_of_nothing.GameWindow.TOP_BAR_HEIGHT;
        int startY = Gdx.graphics.getHeight() - io.github.lord_of_nothing.GameWindow.TOP_BAR_HEIGHT;

        // 1. Dekorativen Rahmen zeichnen (Zuerst, als Hintergrund)
        batch.begin();
        if (bgTexture != null) {
            renderDecoratedFrameAt(batch, 0, 0, width, height, 30f);
        }
        batch.end();

        // 2. Highlights (ShapeRenderer)
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < sidebar.getTemplates().size(); i++) {
            Building b = sidebar.getTemplates().get(i);
            float slotY = startY - (i + 1) * (Sidebar.SLOT_SIZE + Sidebar.PADDING);

            if (pending != null && pending.getBuildingTypeKey().equals(b.getBuildingTypeKey())) {
                sr.setColor(Color.GOLD);
                float iconH = 40f;
                float iconW = iconH * ((float) b.getWidth() / b.getHeight());
                float iconX = (io.github.lord_of_nothing.GameWindow.SIDEBAR_WIDTH - iconW) / 2f;
                sr.rect(iconX - 5, slotY - 5, iconW + 10, iconH + 10);
            }
        }
        sr.end();

        // 3. Icons zeichnen
        batch.begin();
        for (int i = 0; i < sidebar.getTemplates().size(); i++) {
            Building b = sidebar.getTemplates().get(i);
            float slotY = startY - (i + 1) * (Sidebar.SLOT_SIZE + Sidebar.PADDING);

            boolean canAfford = b.getCosts().entrySet().stream()
                .allMatch(e -> resources.hasEnoughResources(e.getKey(), e.getValue()));

            batch.setColor(canAfford ? Color.WHITE : Color.RED);
            Texture tex = textures.get(b.getBuildingTypeKey());
            if (tex != null) {
                float iconH = 40f;
                float iconW = iconH * ((float) b.getWidth() / b.getHeight());
                float iconX = (io.github.lord_of_nothing.GameWindow.SIDEBAR_WIDTH - iconW) / 2f;
                batch.draw(tex, iconX, slotY, iconW, iconH);
            }
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    /**
     * Loads the decorative assets and configures tiling modes for the sidebar frame.
     * Setting the wrap mode to Repeat allows the ornate textures to scale vertically without distortion.
     */
    public void loadAssets(Texture bg, Texture corner, Texture edge) {
        this.bgTexture = bg;
        this.cornerTexture = corner;
        this.edgeTexture = edge;
        this.bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.edgeTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    /**
     * Generic ornate frame renderer adapted for the sidebar's vertical dimensions.
     */
    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y, float w, float h, float cSize) {
        float eH = 8f;
        batch.draw(bgTexture, x, y, w, h, 0, 0, (int)w, (int)h, false, false);

        // Edges
        batch.draw(edgeTexture, x + cSize, y + h - eH, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexture.getHeight(), false, false); // Top
        batch.draw(edgeTexture, x + cSize, y, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexture.getHeight(), false, true); // Bottom
        batch.draw(edgeTexture, x, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexture.getHeight(), (int)(h - 2 * cSize), false, false); // Left
        batch.draw(edgeTexture, x + w - eH, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexture.getHeight(), (int)(h - 2 * cSize), true, false); // Right

        // Corners
        int sW = cornerTexture.getWidth();
        int sH = cornerTexture.getHeight();
        batch.draw(cornerTexture, x, y + h - cSize, cSize, cSize, 0, 0, sW, sH, false, false); // TL
        batch.draw(cornerTexture, x + w - cSize, y + h - cSize, cSize, cSize, 0, 0, sW, sH, true, false); // TR
        batch.draw(cornerTexture, x + w - cSize, y, cSize, cSize, 0, 0, sW, sH, true, true); // BR
        batch.draw(cornerTexture, x, y, cSize, cSize, 0, 0, sW, sH, false, true); // BL
    }
}

package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.game.ResourceStateView;
import io.github.lord_of_nothing.resources.ResourceType;

import java.util.Map;

/**
 * Renders sidebar slots and building icons.
 */
public class SidebarRenderer {
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
    public void render(
        ShapeRenderer sr,
        SpriteBatch batch,
        Sidebar sidebar,
        Map<String, Texture> textures,
        Building pending,
        ResourceStateView resources
    ) {
        int startY = Gdx.graphics.getHeight() - GameWindow.TOP_BAR_HEIGHT;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.DARK_GRAY);
        sr.rect(0, 0, GameWindow.SIDEBAR_WIDTH, startY);

        for (int i = 0; i < sidebar.getTemplates().size(); i++) {
            Building b = sidebar.getTemplates().get(i);
            float slotY = startY - (i + 1) * (Sidebar.SLOT_SIZE + Sidebar.PADDING);

            if (pending != null && pending.getBuildingTypeKey().equals(b.getBuildingTypeKey())) {
                sr.setColor(Color.GOLD);
                float iconH = 40f;
                float iconW = iconH * ((float) b.getWidth() / b.getHeight());
                float iconX = (GameWindow.SIDEBAR_WIDTH - iconW) / 2f;
                sr.rect(iconX - 5, slotY - 5, iconW + 10, iconH + 10);
            }
        }
        sr.end();

        batch.begin();
        for (int i = 0; i < sidebar.getTemplates().size(); i++) {
            Building b = sidebar.getTemplates().get(i);
            float slotY = startY - (i + 1) * (Sidebar.SLOT_SIZE + Sidebar.PADDING);

            boolean canAfford = true;
            for (Map.Entry<ResourceType, Integer> entry : b.getCosts().entrySet()) {
                if (!resources.hasEnoughResources(entry.getKey(), entry.getValue())) {canAfford = false;}
            }

            batch.setColor(canAfford ? Color.WHITE : Color.RED);
            Texture tex = textures.get(b.getBuildingTypeKey());
            if (tex != null) {
                float iconH = 40f;
                float iconW = iconH * ((float) b.getWidth() / b.getHeight());
                float iconX = (GameWindow.SIDEBAR_WIDTH - iconW) / 2f;
                batch.draw(tex, iconX, slotY, iconW, iconH);
            }
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }
}

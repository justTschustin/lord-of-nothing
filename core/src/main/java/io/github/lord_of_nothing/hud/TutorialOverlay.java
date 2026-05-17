package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.events.CloseTutorialEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.ui.UiElement;

/**
 * Modal tutorial overlay that displays long, wrapped and scrollable text inside a bordered
 * decorative frame using the shared HUD assets.
 */
public class TutorialOverlay implements UiElement {
    private static final float PANEL_W = 720f;
    private static final float PANEL_H = 420f;
    private static final float PADDING = 18f;

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout measure = new GlyphLayout();
    private TextButton closeButton;
    private Texture bg, corner, edge;
    private boolean visible = false;
    private final String text = "\nWelcome to Lord of Nothing!\n\n" +
        "This tutorial explains basic controls and concepts.\n" +
        "Each day at 6, villagers will come to your village - provided you have enough housing capacity.\n" +
        "Place buildings by selecting them from the sidebar on the left and left-clicking an empty tile on the grid.\n" +
        "Villagers can be assigned to most buildings to either generate resources or get more soldiers. To quick-assign villagers, right-click the placed building when you have unassigned villagers.\n" +
        "Beware how you assign your villagers, because every now and then bandits will try to raid your village!\n" +
        "\n" +
        "Good luck, Lord!";
    private float scroll = 0f; // how far content is scrolled downward (positive = content moves up)
    private boolean enabled = true;

    /**
     * Creates overlay. Use {@link #loadAssets} before rendering.
     */
    public TutorialOverlay() {
    }

    public void loadAssets(Texture bg, Texture corner, Texture edge) {
        this.bg = bg;
        this.corner = corner;
        this.edge = edge;
        this.bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.edge.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public TextButton getCloseButton() { return closeButton; }

    public void show(EventBus eventBus) {
        this.visible = true;
        // Create close button lazily and register UI elements so input and scrolling work
        float panelX = (Gdx.graphics.getWidth() - PANEL_W) / 2f;
        float panelY = (Gdx.graphics.getHeight() - PANEL_H) / 2f;
        if (closeButton == null) {
            closeButton = new TextButton(panelX + (PANEL_W - 160f) / 2f, panelY + 12f, 160f, 36f, null,
                "Close", () -> eventBus.publish(new CloseTutorialEvent()), false);
        }
        // Register both overlay and close button so input handler can route scroll and clicks
        eventBus.publish(new UiElementCreatedEvent(this));
        eventBus.publish(new UiElementCreatedEvent(closeButton));
        closeButton.setEnabled(true);
    }

    public void hide() {
        this.visible = false;
        if (closeButton != null) { closeButton.setEnabled(false); }
        this.scroll = 0f;
    }

    public boolean isVisible() { return visible; }

    @Override
    public boolean contains(float x, float y) {
        if (!visible) { return false; }
        float panelX = (Gdx.graphics.getWidth() - PANEL_W) / 2f;
        float panelY = (Gdx.graphics.getHeight() - PANEL_H) / 2f;
        return x >= panelX && x <= panelX + PANEL_W && y >= panelY && y <= panelY + PANEL_H;
    }

    @Override
    public void onClick() {
        // Exclusive clicks are routed to the close button via GridInputHandler's exclusiveUiElement
        // The overlay itself consumes clicks but performs no action here.
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible) { return; }
        float panelX = (Gdx.graphics.getWidth() - PANEL_W) / 2f;
        float panelY = (Gdx.graphics.getHeight() - PANEL_H) / 2f;

        // Dim background
        // We draw the dimming with ShapeRenderer from the caller (Main) before calling this render.

        batch.begin();
        // Draw decorated frame
        renderDecoratedFrameAt(batch, panelX, panelY);

        // Title
        font.setColor(Color.GOLD);
        measure.setText(font, "TUTORIAL");
        float titleY = panelY + PANEL_H - 24f;
        font.draw(batch, "TUTORIAL", panelX + PANEL_W / 2f - measure.width / 2f, titleY);

        // Content area
        float contentX = panelX + PADDING;
        float contentW = PANEL_W - 2 * PADDING;
        float contentTop = titleY - 18f;
        float contentBottom = panelY + 60f; // leave room for close button
        float contentH = contentTop - contentBottom;

        // Prepare wrapped layout to measure height
        GlyphLayout layout = new GlyphLayout(font, text, Color.WHITE, contentW, Align.center, true);
        float contentHeight = layout.height;

        // Clamp scrolling
        float maxScroll = Math.max(0f, contentHeight - contentH);
        if (scroll < 0f) { scroll = 0f; }
        if (scroll > maxScroll) { scroll = maxScroll; }

        // Draw text starting from top, offset by scroll moving it upward.
        float drawY = contentTop + scroll;
        font.setColor(Color.WHITE);

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(
            Math.round(contentX),
            Math.round(contentBottom),
            Math.round(contentW),
            Math.round(contentH)
        );
        font.draw(batch, text, contentX, drawY, contentW, Align.center, true);
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        // Close button
        if (closeButton != null) {
            closeButton.setBounds(panelX + (PANEL_W - 160f) / 2f, panelY + 12f, 160f, 36f);
            closeButton.render(batch);
        }
        batch.end();
    }

    @Override
    public boolean isEnabled() { return enabled && visible; }

    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public boolean onScroll(float x, float y, float amountY) {
        if (!visible) { return false; }
        // Only consume scroll when cursor is inside the panel
        if (!contains(x, y)) { return false; }
        // amountY positive scrolls downward (per UiElement contract) -> increase scroll
        this.scroll += amountY * 18f;
        return true;
    }

    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y) {
        float w = PANEL_W;
        float h = PANEL_H;
        float cSize = 28f;
        float eH = 8f;
        batch.draw(bg, x, y, w, h, 0, 0, (int)w, (int)h, false, false);

        int edgeTexH = edge.getHeight();
        batch.draw(edge, x + cSize, y + h - eH, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexH, false, false);
        batch.draw(edge, x + cSize, y, w - 2 * cSize, eH, 0, 0, (int)(w - 2 * cSize), edgeTexH, false, true);
        batch.draw(edge, x, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int)(h - 2 * cSize), false, false);
        batch.draw(edge, x + w - eH, y + cSize, eH, h - 2 * cSize, 0, 0, edgeTexH, (int)(h - 2 * cSize), true, false);

        int sW = corner.getWidth(), sH = corner.getHeight();
        batch.draw(corner, x, y + h - cSize, cSize, cSize, 0, 0, sW, sH, false, false);
        batch.draw(corner, x + w - cSize, y + h - cSize, cSize, cSize, 0, 0, sW, sH, true, false);
        batch.draw(corner, x + w - cSize, y, cSize, cSize, 0, 0, sW, sH, true, true);
        batch.draw(corner, x, y, cSize, cSize, 0, 0, sW, sH, false, true);
    }

    public void dispose() {
        font.dispose();
    }
}




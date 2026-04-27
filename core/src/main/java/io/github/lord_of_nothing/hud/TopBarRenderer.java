package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.button.PauseButton;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.game.GameStateHandler;
import io.github.lord_of_nothing.game.ResourceStateView;
import io.github.lord_of_nothing.resources.ResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders the top resource bar and its pause controls.
 */
public class TopBarRenderer {
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final PauseOverlay pauseOverlay = new PauseOverlay();
    private PauseButton pauseButton;
    private static final float ICON_SIZE = 28f;
    private static final float TEXT_OFFSET = 35f;
    private static final float PAUSE_BUTTON_WIDTH = 70f;
    private static final float PAUSE_BUTTON_HEIGHT = 24f;
    private static final float PAUSE_BUTTON_MARGIN = 10f;
    private Texture bgTexture;
    private Texture cornerTexture;
    private Texture edgeTexture;
    private Map<ResourceType, Texture> resourceIcons = new HashMap<>();
    private Texture woodIcon;
    private Texture stoneIcon;
    private Texture foodIcon;
    private Texture citizenIcon;
    private Texture soldierIcon;
    private Texture capacityIcon;
    private Texture dayIcon;
    private Texture hourIcon;

    /**
     * Registers top-bar UI elements.
     *
     * @param window game window layout context
     * @param eventBus event bus used for UI element registration
     */
    public void registerUiElements(GameWindow window, EventBus eventBus) {
        setPauseButtonVariables(window, eventBus);
        if (pauseButton != null) {
            eventBus.publish(new UiElementCreatedEvent(pauseButton));
        }
    }

    /**
     * Loads the graphical assets required for the ornate top bar background and corners.
     * @param bg The repeatable background texture. @param corner The ornate corner texture to be mirrored.
     */
    public void loadAssets(Texture bg, Texture corner, Texture edge, Texture wood, Texture stone, Texture food, Texture citizen, Texture soldier, Texture capacity, Texture day, Texture hour) {
        this.bgTexture = bg;
        this.cornerTexture = corner;
        this.edgeTexture = edge;
        this.woodIcon = wood;
        this.stoneIcon = stone;
        this.foodIcon = food;
        this.citizenIcon = citizen;
        this.soldierIcon = soldier;
        this.capacityIcon = capacity;
        this.dayIcon = day;
        this.hourIcon = hour;
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        edgeTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }
    /**
     * Renders the top bar, resource counters, and optional pause overlay.
     *
     * @param shapeRenderer shape renderer for bar background
     * @param batch sprite batch for text and buttons
     * @param window game window layout context
     * @param resources current resource state
     * @param currentIngameDay current in-game day
     * @param currentIngameHour current in-game hour
     * @param eventBus event bus used for pause overlay UI registration
     * @param paused whether gameplay is currently paused
     */
    public void render(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        GameWindow window,
        ResourceStateView resources,
        int currentIngameDay,
        int currentIngameHour,
        EventBus eventBus,
        boolean paused
    ) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, window.getTopBarY(), Gdx.graphics.getWidth(), GameWindow.TOP_BAR_HEIGHT);
        shapeRenderer.end();

        setPauseButtonVariables(window, eventBus);
        pauseButton.setEnabled(!paused);
        pauseOverlay.setActive(paused);

        batch.begin();
        renderDecoratedFrame(batch, window, bgTexture, cornerTexture, edgeTexture);
        float y = window.getTopBarY() + 10;
        float x = 10;
        float gap = 30;
        drawResourceGroup(batch, ResourceType.WOOD, resources.getResourceAmount(ResourceType.WOOD), 40, y);
        drawResourceGroup(batch, ResourceType.STONE, resources.getResourceAmount(ResourceType.STONE), 140, y);
        drawResourceGroup(batch, ResourceType.FOOD, resources.getResourceAmount(ResourceType.FOOD), 240, y);
        drawResourceGroup(batch, ResourceType.CITIZENS_TOTAL, resources.getResourceAmount(ResourceType.CITIZENS_TOTAL), 340, y);
        drawResourceGroup(batch, ResourceType.SOLDIERS, resources.getResourceAmount(ResourceType.SOLDIERS), 460, y);
        drawResourceGroup(batch, ResourceType.CITIZENS_CAPACITY, resources.getResourceAmount(ResourceType.CITIZENS_CAPACITY), 580, y);
        float rightEdgeX = Gdx.graphics.getWidth() - 250;

        if (dayIcon != null) batch.draw(dayIcon, rightEdgeX, y - 5, 20, 20);
        font.draw(batch, String.valueOf(currentIngameDay), rightEdgeX + 25, y + 12);

        if (hourIcon != null) batch.draw(hourIcon, rightEdgeX + 70, y - 5, 20, 20);
        font.draw(batch, currentIngameHour + ":00", rightEdgeX + 95, y + 12);
        pauseButton.render(batch);

        if (resources instanceof GameStateHandler) {
            GameStateHandler gsh = (GameStateHandler) resources;
            float ty = window.getTopBarY() + 10;
            renderResourceTooltip(batch, window, ResourceType.WOOD, 40, ty, gsh.getHourlyIncome(ResourceType.WOOD));
            renderResourceTooltip(batch, window, ResourceType.STONE, 140, ty, gsh.getHourlyIncome(ResourceType.STONE));
            renderResourceTooltip(batch, window, ResourceType.FOOD, 240, ty, gsh.getHourlyIncome(ResourceType.FOOD));
        }
        batch.end();

        if (paused) {
            pauseOverlay.render(shapeRenderer, batch, eventBus);
        }
    }

    /**
     * Assigns a texture to a specific resource type for HUD display.
     * <param name="type">The resource type.</param> <param name="tex">The icon texture.</param>
     */
    public void setResourceIcon(ResourceType type, Texture tex) {
        resourceIcons.put(type, tex);
    }
    /**
     * Sets the specific icons used for the in-game calendar and clock display.
     * <param name="day">Icon for the current day.</param> <param name="clock">Icon for the current hour.</param>
     */
    public void setTimeIcons(Texture day, Texture clock) {
        this.dayIcon = day;
        this.hourIcon = clock;
    }
    /**
     * Renders a resource icon and its value with increased dimensions for better visibility.
     * <remarks>Adjusts the vertical alignment to keep the larger icons centered within the 40px bar height.</remarks>
     */
    private void drawResourceGroup(SpriteBatch batch, ResourceType type, int amount, float x, float y) {
        Texture icon = resourceIcons.get(type);
        if (icon != null) {
            batch.draw(icon, x, y - 8, ICON_SIZE, ICON_SIZE);
        }
        font.draw(batch, String.valueOf(amount), x + TEXT_OFFSET, y + 12);
    }

    /**
     * Marks pause-overlay UI elements for re-registration on next render.
     */
    public void invalidatePauseOverlayUiElements() {
        pauseOverlay.invalidateUiElements();
    }

    /**
     * Creates or repositions the pause button based on current window size.
     *
     * @param window game window layout context
     * @param eventBus event bus used when creating the button
     */
    private void setPauseButtonVariables(GameWindow window, EventBus eventBus) {
        float x = Gdx.graphics.getWidth() - PAUSE_BUTTON_WIDTH - PAUSE_BUTTON_MARGIN;
        float y = window.getTopBarY() + (GameWindow.TOP_BAR_HEIGHT - PAUSE_BUTTON_HEIGHT) / 2f;

        if (pauseButton == null) {
            pauseButton = new PauseButton(x, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT, eventBus);
            return;
        }

        pauseButton.setBounds(x, y, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    }

    /**
     * Renders a full frame by tiling background and edge textures between the four static corners.
     * <remarks>Tiles textures without stretching them across the whole top bar.</remarks>
     */
    private void renderDecoratedFrame(SpriteBatch batch, GameWindow window, Texture bg, Texture corner, Texture edge) {
        float width = Gdx.graphics.getWidth();
        float height = GameWindow.TOP_BAR_HEIGHT;
        float y = window.getTopBarY();
        float cSize = height; // Ecken so groß wie die Bar
        float eHeight = 8f;   // Höhe der Kanten-Textur auf dem Bildschirm

        // 1. Hintergrund gekachelt, aber nicht rangezoomt.
        // Ein kompletter 64x64-Tile wird auf die Höhe der Topbar skaliert.
        float bgScale = height / bg.getHeight();
        int bgSrcWidth = Math.round(width / bgScale);
        int bgSrcHeight = bg.getHeight();
        batch.draw(bg, 0, y, width, height, 0, 0, bgSrcWidth, bgSrcHeight, false, false);

        // 2. Horizontale Kanten gekachelt statt gestreckt.
        float edgeWidth = width - (2 * cSize);
        float edgeScale = eHeight / edge.getHeight();
        int edgeSrcWidth = Math.round(edgeWidth / edgeScale);
        int edgeSrcHeight = edge.getHeight();

        // Obere Kante
        batch.draw(edge, cSize, y + height - eHeight, edgeWidth, eHeight, 0, 0, edgeSrcWidth, edgeSrcHeight, false, false);

        // Untere Kante gespiegelt
        batch.draw(edge, cSize, y, edgeWidth, eHeight, 0, 0, edgeSrcWidth, edgeSrcHeight, false, true);

        // 3. Ecken bleiben statisch/skaliert und liegen über den Kanten.
        batch.draw(corner, 0, y, cSize, cSize); // TL
        batch.draw(corner, width - cSize, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), true, false); // TR
        batch.draw(corner, 0, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), false, true); // BL
        batch.draw(corner, width - cSize, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), true, true); // BR
    }
    /**
     * <summary>Checks if the mouse is hovering over a resource slot and renders a decorative tooltip with income details.</summary>
     * <remarks>Reuses the ornate frame rendering logic to maintain UI consistency across the HUD.</remarks>
     */
    private void renderResourceTooltip(SpriteBatch batch, GameWindow window, ResourceType type, float x, float y, int income) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY(); // Flip Y for screen coords

        // Check if mouse is within the slot (roughly 100px width per resource)
        if (mx >= x && mx <= x + 100 && my >= y - 10 && my <= y + 30) {
            float ttW = 180, ttH = 70;
            float ttX = mx - ttW / 2;
            float ttY = y - ttH - 10;

            // Draw Ornate Frame (reuse your logic)
            renderDecoratedFrameAt(batch, ttX, ttY, ttW, ttH, 20f);

            font.setColor(Color.YELLOW);
            font.draw(batch, type.name(), ttX + 20, ttY + ttH - 15);
            font.setColor(Color.WHITE);
            font.draw(batch, "Income: +" + income + "/h", ttX + 20, ttY + ttH - 40);
        }
    }

    /**
        * <summary>Renders an ornate frame with correctly tiled background and edges by mapping screen coordinates to texture wrap units.</summary>
        * <remarks>Ensures a 1:1 pixel ratio for textures to prevent stretching, utilizing TextureWrap.Repeat for seamless tiling.</remarks>
        */
    private void renderDecoratedFrameAt(SpriteBatch batch, float x, float y, float w, float h, float cSize) {
        float eH = 8f; // Die gewünschte Dicke des Rahmens auf dem Bildschirm

        // 1. Tiled Background
        // Wir nutzen die Ziel-Dimensionen (w, h) direkt als Quell-Dimensionen (srcWidth, srcHeight).
        // Dadurch weiß OpenGL: "Nimm so viele Textur-Pixel wie der Bereich auf dem Screen groß ist".
        batch.draw(bgTexture, x, y, w, h, 0, 0, (int)w, (int)h, false, false);

        // 2. Tiled Edges (Kanten)
        float hEdgeW = w - (2 * cSize);
        float vEdgeH = h - (2 * cSize);

        // Horizontale Kanten (Kacheln nur in der Breite, Höhe 'eH' bleibt fix)
        // srcHeight muss der echten Texturhöhe entsprechen, damit die Profillinie nicht gestreckt wird!
        int edgeTexH = edgeTexture.getHeight();
        int edgeTexW = edgeTexture.getWidth();

        // Oben
        batch.draw(edgeTexture, x + cSize, y + h - eH, hEdgeW, eH, 0, 0, (int)hEdgeW, edgeTexH, false, false);
        // Unten (Gespiegelt)
        batch.draw(edgeTexture, x + cSize, y, hEdgeW, eH, 0, 0, (int)hEdgeW, edgeTexH, false, true);

        // Vertikale Kanten (Wir kacheln in der Höhe 'vEdgeH', Breite 'eH' bleibt fix)
        // Links
        batch.draw(edgeTexture, x, y + cSize, eH, vEdgeH, 0, 0, edgeTexH, (int)vEdgeH, false, false);
        // Rechts (Gespiegelt)
        batch.draw(edgeTexture, x + w - eH, y + cSize, eH, vEdgeH, 0, 0, edgeTexH, (int)vEdgeH, true, false);

        // 3. Static Corners (Ecken werden skaliert, nicht gekachelt)
        int sW = cornerTexture.getWidth();
        int sH = cornerTexture.getHeight();
        batch.draw(cornerTexture, x, y + h - cSize, cSize, cSize, 0, 0, sW, sH, false, false); // TL
        batch.draw(cornerTexture, x + w - cSize, y + h - cSize, cSize, cSize, 0, 0, sW, sH, true, false); // TR
        batch.draw(cornerTexture, x + w - cSize, y, cSize, cSize, 0, 0, sW, sH, true, true); // BR
        batch.draw(cornerTexture, x, y, cSize, cSize, 0, 0, sW, sH, false, true); // BL
    }
    /**
     * Disposes renderer-owned font resources.
     */
    public void dispose() { font.dispose(); }
}

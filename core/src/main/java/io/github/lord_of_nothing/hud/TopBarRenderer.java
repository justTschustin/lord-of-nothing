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
        font.draw(batch, currentIngameHour + ":00", rightEdgeX + 95, y + 12);        pauseButton.render(batch);
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
     * Renders a full frame by stretching edge textures between the four static corners.
     * <remarks>Calculates the remaining width between corners to prevent overlapping and visual artifacts.</remarks>
     */
    private void renderDecoratedFrame(SpriteBatch batch, GameWindow window, Texture bg, Texture corner, Texture edge) {
        float width = Gdx.graphics.getWidth();
        float height = GameWindow.TOP_BAR_HEIGHT;
        float y = window.getTopBarY();
        float cSize = height; // Ecken so groß wie die Bar
        float eHeight = 8f;   // Höhe deiner Kanten-Textur (anpassen!)

        // 1. Hintergrund
        batch.draw(bg, 0, y, width, height);

        // 2. Horizontale Kanten (Oben & Unten)
        // Die Breite ist: Gesamtbreite minus 2x Eckengröße
        float edgeWidth = width - (2 * cSize);

        // Obere Kante
        batch.draw(edge, cSize, y + height - eHeight, edgeWidth, eHeight);
        // Untere Kante (Gespiegelt, damit Schatten/Glanz korrekt sind)
        batch.draw(edge, cSize, y, edgeWidth, eHeight, 0, 0, edge.getWidth(), edge.getHeight(), false, true);

        // 3. Ecken (wie zuvor, liegen über den Kanten)
        batch.draw(corner, 0, y, cSize, cSize); // TL
        batch.draw(corner, width - cSize, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), true, false); // TR
        batch.draw(corner, 0, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), false, true); // BL
        batch.draw(corner, width - cSize, y, cSize, cSize, 0, 0, corner.getWidth(), corner.getHeight(), true, true); // BR
    }

    /**
     * Disposes renderer-owned font resources.
     */
    public void dispose() { font.dispose(); }
}

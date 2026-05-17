package io.github.lord_of_nothing.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Animated banner that overlays grid during raid warnings and attacks, and outcome text
 */
public class RaidBanner {

    // Tunable constants for animation effects
    /** Seconds for the expand-from-line animation */
    private static final float EXPAND_DURATION = 0.25f;
    /** Seconds for the collapse-to-line animation */
    private static final float COLLAPSE_DURATION = 0.25f;
    /** Minimum drawn height in pixels during expand/collapse */
    private static final float MIN_HEIGHT = 2f;

    public enum BannerType { WARNING, ATTACK }

    private enum Phase { IDLE, EXPANDING, VISIBLE, COLLAPSING }

    private final Texture warningTexture;
    private final Texture attackTexture;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private Phase phase = Phase.IDLE;
    private BannerType currentType = BannerType.WARNING;
    private String currentMessage = "";
    private float timer = 0f;
    private Runnable onDismissed;
    private String floatingText = null;
    private float floatingTimer = 0f;

    public static final float FLOATING_DURATION = 6.0f;


    /**
     * Creates and loads banners
     */
    public RaidBanner() {
        warningTexture = new Texture(Gdx.files.internal("hud/raid-prep-banner.png"));
        attackTexture  = new Texture(Gdx.files.internal("hud/raid-banner.png"));
        font = new BitmapFont();
        font.getData().setScale(1.5f);
    }

    /**
     * Starts the banner animation
     *
     * @param type       WARNING or ATTACK
     * @param message    text to draw on the banner
     * @param onDismissed called when the animation fully collapses
     */
    public void show(BannerType type, String message, Runnable onDismissed) {
        this.currentType   = type;
        this.currentMessage = message == null ? "" : message;
        this.onDismissed   = onDismissed;
        this.timer = 0f;
        this.phase = Phase.EXPANDING;
    }

    /** @return true while the banner is in any active phase */
    public boolean isActive() {
        return phase != Phase.IDLE;
    }

    /**
     * Upon player click: skips HOLD_DURATION and begins immediate collapse of the banner
     */
    public void dismiss() {
        if (phase == Phase.VISIBLE) {
            phase = Phase.COLLAPSING;
            timer = 0f;
        }
    }

    /**
     * (After raid) Shows a short text message centered on screen that fades after a few seconds
     */
    public void showFloatingText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        floatingText = text;
        floatingTimer = 0f;
    }

    /** Returns true if a floating text message is currently showing. */
    public boolean isFloatingTextActive() {
        return floatingText != null && floatingTimer < FLOATING_DURATION;
    }


    /**
     * Returns whether a world-space coordinate is inside the drawn banner
     * Used by GridInputHandler to detect banner clicks
     */
    public boolean contains(float worldX, float worldY) {
        if (!isActive()) {
            return false;
        }
        Texture tex = currentType == BannerType.WARNING ? warningTexture : attackTexture;
        float screenW   = Gdx.graphics.getWidth();
        float fullH     = tex.getHeight() * (screenW / tex.getWidth());
        float bannerY   = (Gdx.graphics.getHeight() - fullH) / 2f;
        return worldX >= 0 && worldX <= screenW
            && worldY >= bannerY && worldY <= bannerY + fullH;
    }

    /**
     * Updates animation state and renders the banner
     * Call every frame from Main.render() regardless of ScreenState
     *
     * @param delta time in seconds since last frame
     */
    public void update(float delta) {
        // Advance floating text timer when no active banner
        if (floatingText != null) {
            floatingTimer += delta;
            if (floatingTimer >= FLOATING_DURATION) {
                floatingText = null;
            }
        }

        if (phase == Phase.IDLE) {
            return;
        }
        timer += delta;
        switch (phase) {
            case EXPANDING:
                if (timer >= EXPAND_DURATION) {
                    timer = 0f;
                    phase = Phase.VISIBLE;
                }
                break;
            case VISIBLE:
                // Stays open until explicitly dismissed via dismiss()
                break;
            case COLLAPSING:
                if (timer >= COLLAPSE_DURATION) {
                    phase = Phase.IDLE;
                    timer = 0f;
                    if (onDismissed != null) {
                        onDismissed.run();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Updates animation and renders banner and floating text
     *
     * @param batch sprite batch
     */
    public void render(SpriteBatch batch) {
        // Draw floating text if active (separate from banner image)
        if (floatingText != null && floatingTimer < FLOATING_DURATION) {
            float alpha = 1f - (floatingTimer / FLOATING_DURATION); // fade out
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();
            float maxW = screenW * 0.6f;
            float savedScale = font.getScaleX();

            font.getData().setScale(1.5f);
            layout.setText(font, floatingText, Color.WHITE, maxW,
                com.badlogic.gdx.utils.Align.center, true);

            float tx = (screenW - maxW) / 2f;
            float ty = screenH * 0.65f + layout.height / 2f;

            batch.begin();

            // Outline
            font.setColor(0f, 0f, 0f, alpha);
            float o = 2f;
            font.draw(batch, floatingText, tx-o, ty, maxW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, floatingText, tx+o, ty, maxW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, floatingText, tx, ty-o, maxW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, floatingText, tx, ty+o, maxW, com.badlogic.gdx.utils.Align.center, true);

            // Main text
            font.setColor(1f, 1f, 1f, alpha);
            font.draw(batch, floatingText, tx, ty, maxW, com.badlogic.gdx.utils.Align.center, true);

            batch.end();

            font.getData().setScale(savedScale);
        }

        if (phase == Phase.IDLE) {
            return;
        }

        Texture tex   = currentType == BannerType.WARNING ? warningTexture : attackTexture;
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Scale banner to full screen width, maintain aspect ratio
        float fullW   = screenW;
        float fullH   = tex.getHeight() * (screenW / (float) tex.getWidth());
        float centerY = (screenH - fullH) / 2f;  // vertical centre of screen

        // Compute drawn height based on animation phase
        float drawnH;
        if (phase == Phase.EXPANDING) {
            float t = timer / EXPAND_DURATION;  // 0→1
            drawnH  = MIN_HEIGHT + (fullH - MIN_HEIGHT) * t;
        } else if (phase == Phase.COLLAPSING) {
            float t = timer / COLLAPSE_DURATION;  // 0→1
            drawnH  = fullH - (fullH - MIN_HEIGHT) * t;
        } else {
            drawnH = fullH;  // VISIBLE phase: full height
        }

        // Banner is always centred vertically; shrinks/grows from the middle
        float drawnY = centerY + (fullH - drawnH) / 2f;

        // Draw banner image
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(tex, 0f, drawnY, fullW, drawnH);

        // Only draw text when fully open
        if (phase == Phase.VISIBLE) {
            // Draw text with black outline for improved readability
            float maxTextW = fullW * 0.85f;
            layout.setText(font, currentMessage,
                Color.WHITE, maxTextW, com.badlogic.gdx.utils.Align.center, true);
            float textX = (screenW - maxTextW) / 2f;
            if (currentType == BannerType.ATTACK) {
                textX -= screenW * 0.12f;  // shift left by 12% of screen width
            }
            float textY = centerY + fullH / 2f + layout.height / 2f;

            // Outline: draw text 4 times offset in black
            font.setColor(Color.BLACK);
            float o = 1.5f;  // outline offset in pixels
            font.draw(batch, currentMessage, textX - o, textY,
                maxTextW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, currentMessage, textX + o, textY,
                maxTextW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, currentMessage, textX, textY - o,
                maxTextW, com.badlogic.gdx.utils.Align.center, true);
            font.draw(batch, currentMessage, textX, textY + o,
                maxTextW, com.badlogic.gdx.utils.Align.center, true);
            // Main white text on top
            font.setColor(Color.WHITE);
            font.draw(batch, currentMessage, textX, textY,
                maxTextW, com.badlogic.gdx.utils.Align.center, true);
        }

        batch.end();
    }

    /**
     * Draws a semi-transparent black overlay over the full screen.
     * Call this after all world/HUD rendering but before {@link #render(SpriteBatch)}.
     *
     * @param shapeRenderer shape renderer used for the overlay fill
     */
    public void renderDimOverlay(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Frees texture and font resources. */
    public void dispose() {
        warningTexture.dispose();
        attackTexture.dispose();
        font.dispose();
    }
}


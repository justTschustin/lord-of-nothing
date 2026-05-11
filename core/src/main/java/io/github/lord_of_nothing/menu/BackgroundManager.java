package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages background and cloud rendering with scroll offset persistence.
 */
public class BackgroundManager {
    private final Texture background;
    private final Texture cloudsTexture;
    private final Texture blurredBackground;
    private final Texture blurredCloudsTexture;
    private float cloudScrollOffset = 0f;
    private static final float CLOUD_SCROLL_SPEED = 30f;

    public BackgroundManager(String backgroundPath, String cloudsPath, String blurredBackgroundPath, String blurredCloudsPath) {
        this.background = new Texture(backgroundPath);
        this.cloudsTexture = new Texture(cloudsPath);
        this.blurredBackground = new Texture(blurredBackgroundPath);
        this.blurredCloudsTexture = new Texture(blurredCloudsPath);

        cloudsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        blurredCloudsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
    }

    /**
     * Updates cloud scroll offset based on delta time.
     */
    public void update(float deltaTime, float screenWidth) {
        cloudScrollOffset += CLOUD_SCROLL_SPEED * deltaTime;

        // If Offset>ScreenWidth: reset, so that loop starts again
        if (cloudScrollOffset >= screenWidth) {
            cloudScrollOffset = 0;
        }
    }

    /**
     * Renders background and clouds. Use blurred version if specified.
     */
    public void render(SpriteBatch batch, float screenWidth, float screenHeight, boolean useBlurred) {
        Texture bgToUse = (useBlurred && blurredBackground != null) ? blurredBackground : background;
        batch.draw(bgToUse, 0, 0, screenWidth, screenHeight);

        Texture cloudsToUse = (useBlurred && blurredCloudsTexture != null) ? blurredCloudsTexture : cloudsTexture;

        // Draw first instance
        batch.draw(
            cloudsToUse,
            -cloudScrollOffset, 0,
            screenWidth, screenHeight
        );

        // Draw second instance immediately following the first
        batch.draw(
            cloudsToUse,
            -cloudScrollOffset + screenWidth, 0,
            screenWidth, screenHeight
        );
    }

    public void dispose() {
        background.dispose();
        cloudsTexture.dispose();
        if (blurredBackground != null) {
            blurredBackground.dispose();
        }
        if (blurredCloudsTexture != null) {
            blurredCloudsTexture.dispose();
        }
    }
}

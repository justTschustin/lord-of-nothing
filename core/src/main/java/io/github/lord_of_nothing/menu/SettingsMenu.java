package io.github.lord_of_nothing.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.button.Button;
import io.github.lord_of_nothing.button.TextButton;
import io.github.lord_of_nothing.events.CloseSettingsMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ToggleFullscreenEvent;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.select.DropDownSelect;
import io.github.lord_of_nothing.select.GameSpeedSelector;
import io.github.lord_of_nothing.select.ResolutionSelector;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.ui.UiElement;
import io.github.lord_of_nothing.select.VolumeSlider;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders and manages the settings menu UI.
 */
public class SettingsMenu {
    private static final float BUTTON_WIDTH = 280f;
    private static final float BUTTON_HEIGHT = 44f;
    private static final float BUTTON_GAP = 14f;

    private final BitmapFont font = new BitmapFont();
    private final List<UiElement> settingsControls = new ArrayList<>();
    private final ResolutionSelector resolutionSelector;
    private final GameSpeedSelector gameSpeedSelector;
    private final VolumeSlider musicVolumeSlider;
    private final VolumeSlider soundVolumeSlider;
    private final BackgroundManager backgroundManager;
    /**
     * Creates the settings menu and its buttons.
     *
     * @param eventBus event bus used to publish UI actions
     * @param sharedBackgroundManager background manager from MainMenu
     */
    public SettingsMenu(EventBus eventBus, BackgroundManager sharedBackgroundManager) {
        this.backgroundManager = sharedBackgroundManager;

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float toggleY = centerY + BUTTON_GAP / 2f;
        float backY = toggleY - BUTTON_HEIGHT - BUTTON_GAP;

        settingsControls.add(new TextButton(
            x,
            toggleY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "Display Mode: Windowed",
            () -> eventBus.publish(new ToggleFullscreenEvent()),
            false
        ));

        resolutionSelector = new ResolutionSelector(
            0,
            x,
            toggleY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus
        );
        settingsControls.add(resolutionSelector);

        gameSpeedSelector = new GameSpeedSelector(
            x,
            toggleY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus
        );
        settingsControls.add(gameSpeedSelector);

        musicVolumeSlider = new VolumeSlider(x, toggleY, BUTTON_WIDTH, BUTTON_HEIGHT, "Music Volume",
            v -> eventBus.publish(new io.github.lord_of_nothing.events.MusicVolumeChangedEvent(v))
        );
        settingsControls.add(musicVolumeSlider);

        soundVolumeSlider = new VolumeSlider(x, toggleY, BUTTON_WIDTH, BUTTON_HEIGHT, "Sound Volume",
            v -> eventBus.publish(new io.github.lord_of_nothing.events.SoundVolumeChangedEvent(v))
        );
        settingsControls.add(soundVolumeSlider);

        settingsControls.add(new TextButton(
            x,
            backY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            eventBus,
            "Back",
            () -> eventBus.publish(new CloseSettingsMenuEvent()),
            false
        ));
        setButtonLayout();
    }

    /**
     * Registers menu buttons as interactive UI elements.
     *
     * @param eventBus event bus used for publishing UI element creation
     */
    public void registerUiElements(EventBus eventBus) {
        setButtonLayout();
        for (UiElement control : settingsControls) {
            eventBus.publish(new UiElementCreatedEvent(control));
        }
    }

    /**
     * Syncs settings controls from persisted values.
     *
     * @param gameSettings loaded settings to mirror in UI controls
     */
    public void syncDisplaySettings(GameSettings gameSettings) {
        resolutionSelector.syncFromSettings(gameSettings);
        gameSpeedSelector.syncFromSettings(gameSettings);
        musicVolumeSlider.setVolume(gameSettings.musicVolume);
        soundVolumeSlider.setVolume(gameSettings.soundVolume);    }

    /**
     * Renders the settings screen and buttons.
     *
     * @param shapeRenderer shape renderer used for background
     * @param batch sprite batch used for text and buttons
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        setButtonLayout();

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        batch.begin();
        backgroundManager.update(Gdx.graphics.getDeltaTime(), Gdx.graphics.getWidth());
        backgroundManager.render(batch, screenW, screenH, true);
        batch.end();

        batch.begin();
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        float titleY = Gdx.graphics.getHeight() / 2f + getControlsHeight() / 2f + 50f;
        font.draw(batch, "SETTINGS", 0, titleY, Gdx.graphics.getWidth(), Align.center, false);
        font.getData().setScale(1f);
        updateDisplayModeLabel();
        renderControls(batch);
        batch.end();
    }

    /**
     * Disposes menu-owned rendering resources.
     */
    public void dispose() {
        font.dispose();
        musicVolumeSlider.dispose();
        soundVolumeSlider.dispose();    }

    /**
     * Recalculates button layout based on the current window size.
     */
    private void setButtonLayout() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float x = centerX - BUTTON_WIDTH / 2f;
        float totalHeight = getControlsHeight();
        float y = Gdx.graphics.getHeight() / 2f + totalHeight / 2f;

        for (UiElement control : settingsControls) {
            float controlHeight = getControlHeight(control);
            y -= controlHeight;
            setControlBounds(control, x, y, controlHeight);
            y -= BUTTON_GAP;
        }
    }

    private void renderControls(SpriteBatch batch) {
        for (UiElement control : settingsControls) {
            if (!(control instanceof DropDownSelect) && !(control instanceof VolumeSlider)) {
                control.render(batch);
            }
        }

        for (UiElement control : settingsControls) {
            if (control instanceof DropDownSelect && !((DropDownSelect) control).isOpen()) {
                control.render(batch);
            }
        }

        for (UiElement control : settingsControls) {
            if (control instanceof VolumeSlider) {
                control.render(batch);
            }
        }

        for (UiElement control : settingsControls) {
            if (control instanceof DropDownSelect && ((DropDownSelect) control).isOpen()) {
                control.render(batch);
            }
        }
    }

    private void updateDisplayModeLabel() {
        TextButton displayModeButton = (TextButton) settingsControls.get(0);
        displayModeButton.setText(
            Gdx.graphics.isFullscreen() ? "Display Mode: Fullscreen" : "Display Mode: Windowed"
        );
    }

    private float getControlsHeight() {
        if (settingsControls.isEmpty()) {
            return 0f;
        }

        float totalHeight = 0f;
        for (UiElement control : settingsControls) {
            totalHeight += getControlHeight(control);
        }
        totalHeight += BUTTON_GAP * (settingsControls.size() - 1);
        return totalHeight;
    }

    private float getControlHeight(UiElement control) {
        if (control instanceof VolumeSlider) {
            return ((VolumeSlider) control).getHeight();
        }
        if (control instanceof DropDownSelect) {
            return ((DropDownSelect) control).getHeight();
        }
        if (control instanceof Button) {
            return ((Button) control).getHeight();
        }
        return BUTTON_HEIGHT;
    }

    private void setControlBounds(UiElement control, float x, float y, float height) {
        if (control instanceof VolumeSlider) {
            ((VolumeSlider) control).setBounds(x, y, BUTTON_WIDTH, height);
        } else if (control instanceof DropDownSelect) {
            ((DropDownSelect) control).setBounds(x, y, BUTTON_WIDTH, height);
        } else if (control instanceof Button) {
            ((Button) control).setBounds(x, y, BUTTON_WIDTH, height);
        }
    }
}

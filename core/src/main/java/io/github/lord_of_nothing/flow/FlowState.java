package io.github.lord_of_nothing.flow;

/**
 * Holds mutable state that describes the current high-level screen flow.
 */
public class FlowState {
    private ScreenState screenState = ScreenState.MAIN_MENU;
    private boolean gameStarted;
    private boolean paused;
    private boolean settingsOpenedFromPause;

    /**
     * Returns the current screen state.
     *
     * @return active screen state
     */
    public ScreenState getScreenState() {
        return screenState;
    }

    /**
     * Sets the active screen state.
     *
     * @param screenState screen state to apply
     */
    public void setScreenState(ScreenState screenState) {
        this.screenState = screenState;
    }

    /**
     * Returns whether gameplay has started.
     *
     * @return {@code true} if a game session is active
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Sets whether gameplay has started.
     *
     * @param gameStarted started state to apply
     */
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    /**
     * Returns whether gameplay is currently paused.
     *
     * @return {@code true} if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets whether gameplay is currently paused.
     *
     * @param paused paused state to apply
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Returns whether settings were opened from the paused state.
     *
     * @return {@code true} if settings were opened while paused
     */
    public boolean isSettingsOpenedFromPause() {
        return settingsOpenedFromPause;
    }

    /**
     * Sets whether settings were opened from the paused state.
     *
     * @param settingsOpenedFromPause flag to apply
     */
    public void setSettingsOpenedFromPause(boolean settingsOpenedFromPause) {
        this.settingsOpenedFromPause = settingsOpenedFromPause;
    }
}


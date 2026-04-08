package io.github.lord_of_nothing.flow;

public class FlowState {
    private ScreenState screenState = ScreenState.MAIN_MENU;
    private boolean gameStarted;
    private boolean paused;
    private boolean settingsOpenedFromPause;

    public ScreenState getScreenState() {
        return screenState;
    }

    public void setScreenState(ScreenState screenState) {
        this.screenState = screenState;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isSettingsOpenedFromPause() {
        return settingsOpenedFromPause;
    }

    public void setSettingsOpenedFromPause(boolean settingsOpenedFromPause) {
        this.settingsOpenedFromPause = settingsOpenedFromPause;
    }
}


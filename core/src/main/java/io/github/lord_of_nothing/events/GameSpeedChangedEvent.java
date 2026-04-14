package io.github.lord_of_nothing.events;

/**
 * Event that requests changing simulation speed.
 */
public class GameSpeedChangedEvent implements Event {
    private final int gameSpeed;

    /**
     * Creates a game-speed change request.
     *
     * @param gameSpeed requested simulation speed multiplier
     */
    public GameSpeedChangedEvent(int gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    /**
     * Returns the requested simulation speed.
     *
     * @return requested speed multiplier
     */
    public int getGameSpeed() {
        return gameSpeed;
    }
}



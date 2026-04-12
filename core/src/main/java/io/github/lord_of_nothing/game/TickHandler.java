package io.github.lord_of_nothing.game;

/**
 * Tracks fixed-interval simulation ticks independently from render framerate.
 */
public class TickHandler {
    /** Default real-time duration of one simulation tick in seconds. */
    public static final float DEFAULT_TICK_DURATION_SECONDS = 1f;
    /** Baseline tick count used to complete one in-game day at 1x speed. */
    public static final int DEFAULT_TICKS_PER_DAY = 24;
    /** Default simulation speed multiplier. */
    public static final int DEFAULT_GAME_SPEED = 1;

    private final float tickDurationSeconds = DEFAULT_TICK_DURATION_SECONDS;
    private int gameSpeed = DEFAULT_GAME_SPEED;
    private float accumulatorSeconds;
    private int tickProgressInDay;

    /** Creates a tick handler with fixed default timing values. */
    public TickHandler() {}

    /**
     * Returns the real-time seconds per tick at 1x speed.
     *
     * @return base tick duration in seconds
     */
    public float getTickDurationSeconds() {
        return tickDurationSeconds;
    }

    /**
     * Returns the baseline ticks required for one in-game day.
     *
     * @return baseline ticks per day at 1x speed
     */
    public int getTicksPerDay() {
        return DEFAULT_TICKS_PER_DAY;
    }

    /**
     * Returns the current simulation speed multiplier.
     *
     * @return speed multiplier (1, 2, or 4)
     */
    public int getGameSpeed() {
        return gameSpeed;
    }

    /**
     * Updates the simulation speed when the value is supported.
     *
     * @param gameSpeed speed multiplier (1, 2, or 4)
     */
    public void setGameSpeed(int gameSpeed) {
        if (gameSpeed != 1 && gameSpeed != 2 && gameSpeed != 4) {
            return;
        }
        this.gameSpeed = gameSpeed;
    }

    /**
     * Returns the current in-game hour in range [0, 23].
     *
     * @return current in-game hour derived from tick progress
     */
    public int getCurrentIngameHour() {
        int hour = (tickProgressInDay * 24) / getTicksPerDay();
        return Math.min(23, Math.max(0, hour));
    }

    /**
     * Advances simulation time and returns how many in-game days elapsed.
     *
     * @param delta time in seconds since the last frame
     * @return number of fully completed in-game days in this update
     */
    public int update(float delta) {
        if (delta <= 0f) {
            return 0;
        }

        accumulatorSeconds += delta;
        int completedDays = 0;

        float effectiveTickDuration = tickDurationSeconds / (float) gameSpeed;

        while (accumulatorSeconds >= effectiveTickDuration) {
            accumulatorSeconds -= effectiveTickDuration;
            tickProgressInDay++;

            if (tickProgressInDay >= getTicksPerDay()) {
                tickProgressInDay = 0;
                completedDays++;
            }
        }

        return completedDays;
    }
}

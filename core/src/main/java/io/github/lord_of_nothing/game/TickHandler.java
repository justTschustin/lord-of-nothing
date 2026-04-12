package io.github.lord_of_nothing.game;

/**
 * Tracks fixed-interval simulation ticks independently from render framerate.
 */
public class TickHandler {
    public static final float DEFAULT_TICK_DURATION_SECONDS = 1f;
    public static final int DEFAULT_TICKS_PER_DAY = 24;

    private final float tickDurationSeconds = DEFAULT_TICK_DURATION_SECONDS;
    private final int ticksPerDay = DEFAULT_TICKS_PER_DAY;
    private float accumulatorSeconds;
    private int tickProgressInDay;

    public TickHandler() {}

    public float getTickDurationSeconds() {
        return tickDurationSeconds;
    }

    public int getTicksPerDay() {
        return ticksPerDay;
    }

    /**
     * Returns the current in-game hour in range [0, 23].
     *
     * @return current in-game hour derived from tick progress
     */
    public int getCurrentIngameHour() {
        float dayProgress = (float) tickProgressInDay / (float) ticksPerDay;
        int hour = (int) (dayProgress * 24f);
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

        while (accumulatorSeconds >= tickDurationSeconds) {
            accumulatorSeconds -= tickDurationSeconds;
            tickProgressInDay++;

            if (tickProgressInDay >= ticksPerDay) {
                tickProgressInDay = 0;
                completedDays++;
            }
        }

        return completedDays;
    }
}

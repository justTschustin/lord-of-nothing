package io.github.lord_of_nothing.game;

/**
 * Tracks fixed-interval simulation ticks independently from render framerate.
 */
public class TickHandler {
    public static final float DEFAULT_TICK_DURATION_SECONDS = 1f;
    public static final int DEFAULT_TICKS_PER_DAY = 60;

    private final float tickDurationSeconds;
    private final int ticksPerDay;
    private float accumulatorSeconds;

    public TickHandler() {
        this(DEFAULT_TICK_DURATION_SECONDS, DEFAULT_TICKS_PER_DAY);
    }

    public TickHandler(float tickDurationSeconds, int ticksPerDay) {
        this.tickDurationSeconds = tickDurationSeconds <= 0f ? DEFAULT_TICK_DURATION_SECONDS : tickDurationSeconds;
        this.ticksPerDay = ticksPerDay <= 0 ? DEFAULT_TICKS_PER_DAY : ticksPerDay;
    }

    public float getTickDurationSeconds() {
        return tickDurationSeconds;
    }

    public int getTicksPerDay() {
        return ticksPerDay;
    }

    /**
     * @param delta time in seconds since the last tick
     */
    public void update(float delta) {
        if (delta <= 0f) {
            return;
        }

        accumulatorSeconds += delta;

        // Placeholder: consume due ticks in fixed intervals.
        while (accumulatorSeconds >= tickDurationSeconds) {
            accumulatorSeconds -= tickDurationSeconds;
            // TODO: Run one game tick and advance day counter after ticksPerDay ticks.
        }

    }
}

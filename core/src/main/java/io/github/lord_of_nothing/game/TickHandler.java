package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Tracks fixed-interval simulation ticks independently from render framerate.
 */
public class TickHandler {
    /// Default real-time duration of one simulation tick in seconds
    public static final float DEFAULT_TICK_DURATION_SECONDS = 1f;
    /// Baseline tick count used to complete one in-game day at 1x speed
    public static final int DEFAULT_TICKS_PER_DAY = 24;
    /// Default simulation speed multiplier
    public static final int DEFAULT_GAME_SPEED = 1;
    /// Base minimum citizen arrival count at the start of the game
    public static final int DEFAULT_CITIZEN_ARRIVAL_MIN = 1;
    /// Base maximum citizen arrival count at the start of the game
    public static final int DEFAULT_CITIZEN_ARRIVAL_MAX = 5;
    /// Number of in-game days between citizen arrival border increases
    public static final int DEFAULT_CITIZEN_ARRIVAL_BORDER_INCREASE_INTERVAL_DAYS = 5;
    /// How much the citizen arrival borders increase after each interval
    public static final int DEFAULT_CITIZEN_ARRIVAL_BORDER_INCREASE = 1;
    /// Citizens arrive at 6am each day
    public static final int DEFAULT_CITIZEN_ARRIVAL_HOUR = 6;

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
     * Returns the minimum number of citizens that can arrive on the given day.
     *
     * @param currentIngameDay current in-game day
     * @return minimum arrival count for the day
     */
    public int getCitizenArrivalMin(int currentIngameDay) {
        return DEFAULT_CITIZEN_ARRIVAL_MIN + getCitizenArrivalBorderIncrease(currentIngameDay);
    }

    /**
     * Returns the maximum number of citizens that can arrive on the given day.
     *
     * @param currentIngameDay current in-game day
     * @return maximum arrival count for the day
     */
    public int getCitizenArrivalMax(int currentIngameDay) {
        return DEFAULT_CITIZEN_ARRIVAL_MAX + getCitizenArrivalBorderIncrease(currentIngameDay);
    }

    /**
     * Returns the tick progress in day where citizens arrive.
     *
     * @return tick index for the 6am arrival event
     */
    public int getCitizenArrivalTickProgress() {
        return (getTicksPerDay() * DEFAULT_CITIZEN_ARRIVAL_HOUR) / 24;
    }

    /**
     * Advances simulation time, optionally applying the daily 6am citizen arrival event.
     *
     * @param delta time in seconds since the last frame
     * @param currentIngameDay current in-game day before any newly completed days are applied
     * @param resourceState mutable resource state used to add arriving citizens
     * @return number of fully completed in-game days in this update
     */
    public int update(float delta, int currentIngameDay, ResourceStateMutator resourceState) {
        if (delta <= 0f) {
            return 0;
        }

        accumulatorSeconds += delta;
        int completedDays = 0;

        float effectiveTickDuration = tickDurationSeconds / (float) gameSpeed;
        int baseIngameDay = Math.max(1, currentIngameDay);

        while (accumulatorSeconds >= effectiveTickDuration) {
            accumulatorSeconds -= effectiveTickDuration;
            tickProgressInDay++;

            if (resourceState != null && tickProgressInDay == getCitizenArrivalTickProgress()) {
                int arrivalDay = baseIngameDay + completedDays;
                int minimum = getCitizenArrivalMin(arrivalDay);
                int maximum = getCitizenArrivalMax(arrivalDay);
                int arrivedCitizens = ThreadLocalRandom.current().nextInt(minimum, maximum + 1);
                resourceState.addResource(ResourceType.CITIZENS, arrivedCitizens);
                System.out.println(
                    "Day " + arrivalDay
                        + " citizen arrival: min=" + minimum
                        + ", max=" + maximum
                        + ", arrived=" + arrivedCitizens
                );
            }

            if (tickProgressInDay >= getTicksPerDay()) {
                tickProgressInDay = 0;
                completedDays++;
            }
        }

        return completedDays;
    }

    private int getCitizenArrivalBorderIncrease(int currentIngameDay) {
        int clampedDay = Math.max(1, currentIngameDay);
        int intervalCount = (clampedDay - 1) / DEFAULT_CITIZEN_ARRIVAL_BORDER_INCREASE_INTERVAL_DAYS;
        return intervalCount * DEFAULT_CITIZEN_ARRIVAL_BORDER_INCREASE;
    }
}

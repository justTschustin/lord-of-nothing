package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.hud.EventLog;
import io.github.lord_of_nothing.resources.ResourceType;

/**
 * Manages the population hunger level that changes every simulation tick (= 1 in-game hour).
 *
 * <p>Each tick every citizen consumes {@value #FOOD_PER_CITIZEN_PER_TICK} food unit(s).
 * If the food supply is sufficient the hunger bar rises slowly; if food runs out it falls.
 * At {@code 0 %} the game ends.</p>
 */
public class HungerMechanic {

    /** Food units consumed per citizen per tick (1 tick = 1 in-game hour). */
    public static final int FOOD_PER_CITIZEN_PER_TICK = 3;

    /**
     * Base hunger rise per tick when citizens are fully fed.
     * Bonus food above consumption increases this rate (up to 2×).
     */
    public static final float BASE_HUNGER_RISE_RATE = 0.004f;

    /**
     * Hunger drop per tick when no food is available at all.
     * Partial feeding produces a proportionally smaller drop.
     */
    public static final float BASE_HUNGER_FALL_RATE = 0.007f;

    private float hungerLevel = 0.5f;
    private boolean pendingGameOver = false;

    // -----------------------------------------------------------------------
    // Public state
    // -----------------------------------------------------------------------

    /** Returns the current hunger value in [0, 1]. */
    public float getHungerLevel() { return hungerLevel; }

    /** Returns the {@link HungerLevel} tier that corresponds to the current value. */
    public HungerLevel getCurrentTier() { return HungerLevel.forLevel(hungerLevel); }

    /**
     * Returns {@code true} once after hunger reaches {@code 0 %}, then resets the flag.
     * Must be polled every tick to avoid missing the event.
     */
    public boolean consumePendingGameOver() {
        if (!pendingGameOver) { return false; }
        pendingGameOver = false;
        return true;
    }

    // -----------------------------------------------------------------------
    // Tick update
    // -----------------------------------------------------------------------

    /**
     * Consumes food proportional to the current citizen count and adjusts the hunger bar.
     * Should be called once per simulation tick.
     *
     * @param resources mutable resource state (food is deducted here)
     * @param log       event log for starvation messages
     */
    public void update(ResourceStateMutator resources, EventLog log) {
        int citizens = resources.getResourceAmount(ResourceType.CITIZENS_TOTAL);
        if (citizens <= 0) { return; }

        int food = resources.getResourceAmount(ResourceType.FOOD);
        int consumption = citizens * FOOD_PER_CITIZEN_PER_TICK;

        if (food >= consumption) {
            // Consume required food
            resources.tryConsumeResource(ResourceType.FOOD, consumption);

            // Bonus factor from excess food (capped at 2×)
            int remaining = food - consumption;
            float excessFactor = Math.min(2.0f, 1.0f + (float) remaining / Math.max(1, consumption));
            hungerLevel = Math.min(1.0f, hungerLevel + BASE_HUNGER_RISE_RATE * excessFactor);
        } else {
            // Consume whatever is left
            if (food > 0) {
                resources.tryConsumeResource(ResourceType.FOOD, food);
            }

            // Fall proportional to the unfed fraction
            float fedFraction = consumption > 0 ? (float) food / consumption : 0f;
            float fallAmount = BASE_HUNGER_FALL_RATE * (1.0f - fedFraction);
            float previous = hungerLevel;
            hungerLevel = Math.max(0.0f, hungerLevel - fallAmount);

            if (hungerLevel <= 0.0f && previous > 0.0f) {
                pendingGameOver = true;
                log.addMessage("Your people have starved to death! Game over.", true);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Lifecycle helpers
    // -----------------------------------------------------------------------

    /** Resets hunger to 50 % and clears any pending game-over flag. */
    public void reset() {
        hungerLevel = 0.5f;
        pendingGameOver = false;
    }
}

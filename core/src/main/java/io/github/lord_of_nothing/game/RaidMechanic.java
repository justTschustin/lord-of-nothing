package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Owns raid timeline scheduling and popup message generation.
 */
public final class RaidMechanic {
    public static final int FIRST_RAID_DETERMINATION_DAY = 10;
    public static final int RAID_WARNING_MIN_DAYS = 5;
    public static final int RAID_WARNING_MAX_DAYS = 20;
    public static final int RAID_REST_MIN_DAYS = 10;
    public static final int RAID_REST_MAX_DAYS = 20;

    private RaidMechanic() {}

    /**
     * Resets raid state for a fresh run.
     *
     * @param state mutable game state handler
     */
    public static void resetTimeline(GameStateHandler state) {
        if (state == null) {
            return;
        }
        state.setNextRaidScheduledDay(null);
        state.setNextRaidDeterminationDay(FIRST_RAID_DETERMINATION_DAY);
    }

    /**
     * Applies one in-game day of raid progression and emits raid popup text when needed.
     *
     * @param state mutable game state handler
     * @param currentDay newly started in-game day
     * @param popupConsumer sink for popup messages
     */
    public static void processDay(GameStateHandler state, int currentDay, Consumer<String> popupConsumer) {
        if (state == null || currentDay < 1) {
            return;
        }

        Integer scheduledRaidDay = state.getNextRaidScheduledDay();
        if (scheduledRaidDay == null) {
            Integer determinationDay = state.getNextRaidDeterminationDay();
            if (determinationDay != null && currentDay >= determinationDay) {
                scheduleAndAnnounceRaid(state, currentDay, popupConsumer);
                scheduledRaidDay = state.getNextRaidScheduledDay();
            }
        }

        if (scheduledRaidDay != null && currentDay >= scheduledRaidDay) {
            announceRaidArrival(state, currentDay, popupConsumer);
            state.setNextRaidScheduledDay(null);
            state.setNextRaidDeterminationDay(currentDay + randomIntInclusive(RAID_REST_MIN_DAYS, RAID_REST_MAX_DAYS));
        }
    }

    private static void scheduleAndAnnounceRaid(
        GameStateHandler state,
        int currentDay,
        Consumer<String> popupConsumer
    ) {
        int warningMin = randomIntInclusive(RAID_WARNING_MIN_DAYS, RAID_WARNING_MAX_DAYS);
        int warningMax = randomIntInclusive(RAID_WARNING_MIN_DAYS, RAID_WARNING_MAX_DAYS);
        if (warningMin > warningMax) {
            int temp = warningMin;
            warningMin = warningMax;
            warningMax = temp;
        }

        int daysUntilRaid = randomIntInclusive(warningMin, warningMax);
        state.setNextRaidScheduledDay(currentDay + daysUntilRaid);
        state.setNextRaidDeterminationDay(null);

        emitPopup(
            popupConsumer,
            "Prepare for the incoming raid! Our scouts have spotted bandits heading our direction! "
                + "They should be here in about " + warningMin + " to " + warningMax + " days!"
        );

        System.out.println("Raid warned to be in " + warningMin + " to " + warningMax + " days");
        System.out.println("Raid scheduled for " + state.getNextRaidScheduledDay());
    }

    private static void announceRaidArrival(GameStateHandler state, int currentDay, Consumer<String> popupConsumer) {
        int banditMin = 15 + (currentDay / 2);
        int banditMax = 15 + currentDay;
        int bandits = randomIntInclusive(banditMin, banditMax);
        int soldiers = state.getResourceAmount(ResourceType.SOLDIERS);

        emitPopup(
            popupConsumer,
            "The raid has begun! " + bandits + " bandits are attacking the village, and "
                + soldiers + " soldiers are defending."
        );
    }

    private static void emitPopup(Consumer<String> popupConsumer, String message) {
        if (popupConsumer == null || message == null || message.isEmpty()) {
            return;
        }
        popupConsumer.accept(message);
    }

    private static int randomIntInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}


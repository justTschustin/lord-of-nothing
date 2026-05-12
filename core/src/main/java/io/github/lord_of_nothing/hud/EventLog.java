package io.github.lord_of_nothing.hud;
import java.util.function.Supplier;
/**
 * Manages game events and retrieves timestamps dynamically via functional providers.
 * Using Suppliers decouples the log from the time-tracking logic, ensuring it always fetches the most recent game state.
 */
public class EventLog {
    private final java.util.LinkedList<String> messages = new java.util.LinkedList<>();
    private final int maxMessages = 8;
    private final Supplier<Integer> daySupplier;
    private final Supplier<Integer> hourSupplier;
    private int scrollOffset = 0;
    private static final int MAX_MESSAGES = 100;

    public EventLog(Supplier<Integer> daySupplier, Supplier<Integer> hourSupplier) {
        this.daySupplier = daySupplier;
        this.hourSupplier = hourSupplier;
    }

    /**
     * Adds a message to the end of the list and automatically scrolls to the newest entry.
     * @param message the content of the message to be logged
     * @param includeTimestamp whether to prepend a day/hour timestamp
     */
    public void addMessage(String message, boolean includeTimestamp) {
        String prefix = includeTimestamp ?
            String.format("[D%d %02d:00] ", daySupplier.get(), hourSupplier.get()) : "";

        messages.add(prefix + message);
        if (messages.size() > 100) {messages.remove(0);}

        scrollOffset = 0;
        System.out.println(message);
    }

    /**
     * Updates the vertical scroll position by shifting the offset based on mouse wheel movement.
     * Ensures the offset stays within valid bounds relative to the current number of logged messages.
     */
    public void scroll(float amountY) {
        int maxScroll = Math.max(0, messages.size() - 8);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)amountY));
    }

    /**
     * Clears all messages and resets the scroll position for a fresh game state.
     */
    public void clear() {
        messages.clear();
        scrollOffset = 0;
    }
    public int getScrollOffset() { return scrollOffset; }
    public java.util.List<String> getMessages() { return messages; }
}

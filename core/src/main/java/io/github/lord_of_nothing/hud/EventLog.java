package io.github.lord_of_nothing.hud;
import java.io.Console;
import java.util.function.Supplier;
/**
 *Manages game events and retrieves timestamps dynamically via functional providers.
 * <remarks>Using Suppliers decouples the log from the time-tracking logic, ensuring it always fetches the most recent game state.</remarks>
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
        addMessage("Welcome, Lord of Nothing!", false);
        addMessage("Tutorial: Build houses to attract citizens.", false);
    }

    /**
     * Adds a message to the end of the list and automatically scrolls to the newest entry.
     * <param name="message">The content of the message to be logged.</param>
     * <param name="includeTimestamp">Whether to prepend the current game day and hour to the message.</param>
     */
    public void addMessage(String message, boolean includeTimestamp) {
        String prefix = includeTimestamp ?
            String.format("[D%d %02d:00] ", daySupplier.get(), hourSupplier.get()) : "";

        messages.add(prefix + message);
        if (messages.size() > 100) messages.remove(0);

        scrollOffset = 0;
        System.out.println(message);
    }

    /**
     * <summary>Updates the vertical scroll position by shifting the offset based on mouse wheel movement.</summary>
     * <remarks>Ensures the offset stays within valid bounds relative to the current number of logged messages.</remarks>
     */
    public void scroll(float amountY) {
        int maxScroll = Math.max(0, messages.size() - 8);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)amountY));
    }

    /**
     * <summary>Clears all messages and resets the scroll position for a fresh game state.</summary>
     */
    public void clear() {
        messages.clear();
        scrollOffset = 0;
    }
    public int getScrollOffset() { return scrollOffset; }
    public java.util.List<String> getMessages() { return messages; }
}

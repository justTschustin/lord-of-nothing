package io.github.lord_of_nothing.hud;
import java.util.function.Supplier;
/**
 * <summary>Manages game events and retrieves timestamps dynamically via functional providers.</summary>
 * <remarks>Using Suppliers decouples the log from the time-tracking logic, ensuring it always fetches the most recent game state.</remarks>
 */
public class EventLog {
    private final java.util.LinkedList<String> messages = new java.util.LinkedList<>();
    private final int maxMessages = 8;
    private final Supplier<Integer> daySupplier;
    private final Supplier<Integer> hourSupplier;

    public EventLog(Supplier<Integer> daySupplier, Supplier<Integer> hourSupplier) {
        this.daySupplier = daySupplier;
        this.hourSupplier = hourSupplier;
        addMessage("Welcome, Lord of Nothing!", false);
        addMessage("Tutorial: Build houses to attract citizens.", false);
    }

    public void addMessage(String message, boolean includeTimestamp) {
        String prefix = includeTimestamp ?
            String.format("[D%d %02d:00] ", daySupplier.get(), hourSupplier.get()) : "";
        messages.addFirst(prefix + message);
        if (messages.size() > maxMessages) messages.removeLast();
    }

    public java.util.List<String> getMessages() { return messages; }
}

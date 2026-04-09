package io.github.lord_of_nothing.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple in-process event bus for publishing and subscribing to UI and flow events.
 */
public class EventBus {
    private List<Consumer<Event>> listeners = new ArrayList<>();

    private ArrayList<Event> eventLog = new ArrayList<>();

    /**
     * Registers a listener that receives all published events.
     *
     * @param listener event listener callback
     */
    public void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    /**
     * Publishes an event to all current listeners and records it in the event log.
     *
     * @param event event instance to dispatch
     */
    public void publish(Event event) {
        System.out.println("Publishing event: " + event.getClass().getSimpleName());
        eventLog.add(event);
        for (Consumer<Event> l : listeners) {
            l.accept(event);
        }
    }

    /**
     * Returns all events published since startup.
     *
     * @return mutable event log list
     */
    public ArrayList<Event> getEventLog() {
        return eventLog;
    }
}


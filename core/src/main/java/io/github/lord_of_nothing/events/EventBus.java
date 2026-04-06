package io.github.lord_of_nothing.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private List<Consumer<Event>> listeners = new ArrayList<>();

    private ArrayList<Event> eventLog = new ArrayList<>();

    public void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public void publish(Event event) {
        System.out.println("Publishing event: " + event.getClass().getSimpleName());
        eventLog.add(event);
        for (Consumer<Event> l : listeners) {
            l.accept(event);
        }
    }

    public ArrayList<Event> getEventLog() {
        return eventLog;
    }
}


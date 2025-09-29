/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.core;

import aut.utcluj.ssatr.eframework.core.Event;
import java.util.*;

public class EventStore {
    private Map<String, List<Event>> eventsByType;
    private List<Event> allEvents;

    public EventStore() {
        this.eventsByType = new HashMap<String, List<Event>>();
        this.allEvents = new ArrayList<Event>();
    }

    public void storeEvent(Event event) {
        allEvents.add(event);

        String type = event.getEventType();
        if (!eventsByType.containsKey(type)) {
            eventsByType.put(type, new ArrayList<Event>());
        }
        eventsByType.get(type).add(event);
    }

    public List<Event> getEventsByType(String eventType) {
        List<Event> events = eventsByType.get(eventType);
        return events != null ? new ArrayList<Event>(events) : new ArrayList<Event>();
    }

    public List<Event> getAllEvents() {
        return new ArrayList<Event>(allEvents);
    }

    public int getEventCount() {
        return allEvents.size();
    }

    public int getEventCountByType(String eventType) {
        List<Event> events = eventsByType.get(eventType);
        return events != null ? events.size() : 0;
    }
}
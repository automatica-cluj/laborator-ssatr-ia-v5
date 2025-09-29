/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample.listeners;

import aut.utcluj.ssatr.eframework.core.Event;
import aut.utcluj.ssatr.eframework.core.EventListener;
import java.util.*;

public class EventCounterListener implements EventListener {
    private Map<String, Integer> counters;

    public EventCounterListener() {
        this.counters = new HashMap<String, Integer>();
    }

    public void onEvent(Event event) {
        String type = event.getEventType();
        Integer count = counters.get(type);
        if (count == null) {
            count = 0;
        }
        counters.put(type, count + 1);

        System.out.println("📊 Event count for " + type + ": " + (count + 1));
    }

    public boolean canHandle(String eventType) {
        return true; // Count all events
    }

    public void printStatistics() {
        System.out.println("\n=== EVENT STATISTICS ===");
        for (String type : counters.keySet()) {
            System.out.println(type + ": " + counters.get(type));
        }
        System.out.println("========================");
    }
}
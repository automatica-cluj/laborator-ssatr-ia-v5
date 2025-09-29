/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.core;

import java.util.*;

public class EventPublisher {
    private List<EventListener> listeners;

    public EventPublisher() {
        this.listeners = new ArrayList<EventListener>();
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void publishEvent(Event event) {
        System.out.println("📢 Publishing: " + event);

        for (int i = 0; i < listeners.size(); i++) {
            EventListener listener = listeners.get(i);
            try {
                if (listener.canHandle(event.getEventType())) {
                    listener.onEvent(event);
                }
            } catch (Exception e) {
                System.err.println("Error in listener: " + e.getMessage());
            }
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }
}
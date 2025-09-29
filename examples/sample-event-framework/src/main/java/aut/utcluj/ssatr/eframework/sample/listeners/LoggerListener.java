/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample.listeners;

import aut.utcluj.ssatr.eframework.core.Event;
import aut.utcluj.ssatr.eframework.core.EventListener;

public class LoggerListener implements EventListener {
    private String name;

    public LoggerListener(String name) {
        this.name = name;
    }

    public void onEvent(Event event) {
        System.out.println("[" + name + "] Received: " + event);
    }

    public boolean canHandle(String eventType) {
        return true; // This logger handles all events
    }
}
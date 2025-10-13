/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample;

import aut.utcluj.ssatr.eframework.sample.listeners.EventCounterListener;
import aut.utcluj.ssatr.eframework.sample.listeners.UserActionListener;
import aut.utcluj.ssatr.eframework.sample.listeners.LoggerListener;
import aut.utcluj.ssatr.eframework.sample.events.UserActionEvent;
import aut.utcluj.ssatr.eframework.sample.events.StatusChangeEvent;
import aut.utcluj.ssatr.eframework.core.EventStore;
import aut.utcluj.ssatr.eframework.core.Event;
import aut.utcluj.ssatr.eframework.core.EventPublisher;

public class EventFrameworkDemo {
    public static void main(String[] args) {
        // Create the event system components
        EventPublisher publisher = new EventPublisher();
        EventStore store = new EventStore();
        
        // Create listeners
        LoggerListener logger = new LoggerListener("MainLogger");
        UserActionListener userListener = new UserActionListener();
        EventCounterListener counter = new EventCounterListener();
        
        // Register listeners
        publisher.addListener(logger);
        publisher.addListener(userListener);
        publisher.addListener(counter);
        
        System.out.println("Event Framework Demo Started");
        System.out.println("Registered " + publisher.getListenerCount() + " listeners\n");
        
        // Create and publish some events
        Event event1 = new UserActionEvent("E001", "LOGIN", "user123");
        Event event2 = new StatusChangeEvent("E002", "order456", "PENDING", "PROCESSING");
        Event event3 = new UserActionEvent("E003", "LOGOUT", "user123");
        Event event4 = new StatusChangeEvent("E004", "order456", "PROCESSING", "COMPLETED");
        
        // Publish events
        publisher.publishEvent(event1);
        store.storeEvent(event1);
        
        publisher.publishEvent(event2);
        store.storeEvent(event2);
        
        publisher.publishEvent(event3);
        store.storeEvent(event3);
        
        publisher.publishEvent(event4);
        store.storeEvent(event4);
        
        // Show statistics
        System.out.println();
        counter.printStatistics();
        
        System.out.println("\nStore Statistics:");
        System.out.println("Total events: " + store.getEventCount());
        System.out.println("User actions: " + store.getEventCountByType("USER_ACTION"));
        System.out.println("Status changes: " + store.getEventCountByType("STATUS_CHANGE"));
        
        System.out.println("\n Demo completed!");
    }
}


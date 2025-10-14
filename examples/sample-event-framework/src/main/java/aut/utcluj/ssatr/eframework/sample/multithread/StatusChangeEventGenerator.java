package aut.utcluj.ssatr.eframework.sample.multithread;

import aut.utcluj.ssatr.eframework.core.EventPublisher;
import aut.utcluj.ssatr.eframework.sample.events.StatusChangeEvent;

public class StatusChangeEventGenerator implements Runnable {
    private final EventPublisher publisher;
    private volatile boolean running = true;
    private int eventCount = 0;

    public StatusChangeEventGenerator(EventPublisher publisher) {
        this.publisher = publisher;
        
    }

    @Override
    public void run() {
        Thread.currentThread().setName("StatusChangeEventGenerator");
        System.out.println("StatusChangeEventGenerator started");

        while (running) {
            try {
                eventCount++;
                String eventId = "SC_" + eventCount;
                String objectId = "object" + (eventCount % 5 + 1);
                String oldStatus = "STATUS_" + eventCount;
                String newStatus = "STATUS_" + (eventCount + 1);

                StatusChangeEvent event = new StatusChangeEvent(eventId, objectId, oldStatus, newStatus);
                publisher.publishEvent(event);

                Thread.sleep(1500);

            } catch (InterruptedException e) {
                System.out.println("StatusChangeEventGenerator interrupted");
                break;
            }
        }

        System.out.println("StatusChangeEventGenerator stopped");
    }

    public void stopGeneration() {
        running = false;
    }
}
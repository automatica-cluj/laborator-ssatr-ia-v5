package aut.utcluj.ssatr.eframework.sample.multithread;

import aut.utcluj.ssatr.eframework.core.EventPublisher;
import aut.utcluj.ssatr.eframework.sample.events.UserActionEvent;

public class UserActionEventGenerator extends Thread {
    private final EventPublisher publisher;
    private volatile boolean running = true;
    private int eventCount = 0;

    public UserActionEventGenerator(EventPublisher publisher) {
        this.publisher = publisher;
        setName("UserActionEventGenerator");
    }

    @Override
    public void run() {
        System.out.println(getName() + " started");

        while (running) {
            try {
                eventCount++;
                String eventId = "UA_" + eventCount;
                String action = "ACTION_" + eventCount;
                String userId = "user" + (eventCount % 3 + 1);

                UserActionEvent event = new UserActionEvent(eventId, action, userId);
                publisher.publishEvent(event);

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                System.out.println(getName() + " interrupted");
                break;
            }
        }

        System.out.println(getName() + " stopped");
    }

    public void stopGeneration() {
        running = false;
        interrupt();
    }
}
package aut.utcluj.ssatr.eframework.sample.multithread;

import aut.utcluj.ssatr.eframework.core.EventPublisher;
import java.util.ArrayList;
import java.util.List;

public class EventGeneratorController {
    private final EventPublisher publisher;
    private final List<Thread> threads;
    private final List<UserActionEventGenerator> userGenerators;
    private final List<StatusChangeEventGenerator> statusGenerators;

    public EventGeneratorController(EventPublisher publisher) {
        this.publisher = publisher;
        this.threads = new ArrayList<>();
        this.userGenerators = new ArrayList<>();
        this.statusGenerators = new ArrayList<>();
    }

    public void addUserActionGenerator() {
        UserActionEventGenerator generator = new UserActionEventGenerator(publisher);
        userGenerators.add(generator);
        threads.add(generator);
    }

    public void addStatusChangeGenerator() {
        StatusChangeEventGenerator generator = new StatusChangeEventGenerator(publisher);
        Thread thread = new Thread(generator);
        statusGenerators.add(generator);
        threads.add(thread);
    }

    public void startAll() {
        System.out.println("Starting " + threads.size() + " threads");
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void stopAll() {
        System.out.println("Stopping all threads");

        for (UserActionEventGenerator generator : userGenerators) {
            generator.stopGeneration();
        }

        for (StatusChangeEventGenerator generator : statusGenerators) {
            generator.stopGeneration();
        }

        for (Thread thread : threads) {
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread stop interrupted");
            }
        }
    }
}
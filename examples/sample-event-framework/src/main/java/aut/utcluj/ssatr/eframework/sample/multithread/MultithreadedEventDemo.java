package aut.utcluj.ssatr.eframework.sample.multithread;

import aut.utcluj.ssatr.eframework.core.EventPublisher;
import aut.utcluj.ssatr.eframework.sample.listeners.LoggerListener;

public class MultithreadedEventDemo {
    public static void main(String[] args) {
        System.out.println("Multithreaded Event Demo");
        System.out.println("========================");

        EventPublisher publisher = new EventPublisher();
        LoggerListener logger = new LoggerListener("Logger");
        publisher.addListener(logger);

        EventGeneratorController controller = new EventGeneratorController(publisher);
        controller.addUserActionGenerator();
        controller.addStatusChangeGenerator();

        System.out.println("Starting demo...");
        controller.startAll();

        try {
            Thread.sleep(35000);
        } catch (InterruptedException e) {
            System.out.println("Demo interrupted");
        }

        controller.stopAll();
        System.out.println("Demo completed");
    }
}
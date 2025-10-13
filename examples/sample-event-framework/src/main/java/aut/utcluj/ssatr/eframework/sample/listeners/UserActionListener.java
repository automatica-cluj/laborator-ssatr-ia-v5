/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample.listeners;

import aut.utcluj.ssatr.eframework.sample.events.UserActionEvent;
import aut.utcluj.ssatr.eframework.core.Event;
import aut.utcluj.ssatr.eframework.core.EventListener;

public class UserActionListener implements EventListener {
    public void onEvent(Event event) {
        if (event instanceof UserActionEvent) {
            UserActionEvent userEvent = (UserActionEvent) event;
            System.out.println("User " + userEvent.getUserId() +
                             " performed: " + userEvent.getAction());
        }
    }

    public boolean canHandle(String eventType) {
        return "USER_ACTION".equals(eventType);
    }
}
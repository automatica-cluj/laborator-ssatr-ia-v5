/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample.events;

import aut.utcluj.ssatr.eframework.core.Event;

public class UserActionEvent extends Event {
    public UserActionEvent(String eventId, String action, String userId) {
        super(eventId, "USER_ACTION", action + ":" + userId);
    }

    public String getAction() {
        String dataStr = (String) getData();
        return dataStr.split(":")[0];
    }

    public String getUserId() {
        String dataStr = (String) getData();
        return dataStr.split(":")[1];
    }
}
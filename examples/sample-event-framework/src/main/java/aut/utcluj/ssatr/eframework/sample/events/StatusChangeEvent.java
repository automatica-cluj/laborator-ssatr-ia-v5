/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.sample.events;

import aut.utcluj.ssatr.eframework.core.Event;

public class StatusChangeEvent extends Event {
    public StatusChangeEvent(String eventId, String objectId, String oldStatus, String newStatus) {
        super(eventId, "STATUS_CHANGE", objectId + ":" + oldStatus + ":" + newStatus);
    }

    public String getObjectId() {
        String dataStr = (String) getData();
        return dataStr.split(":")[0];
    }

    public String getOldStatus() {
        String dataStr = (String) getData();
        return dataStr.split(":")[1];
    }

    public String getNewStatus() {
        String dataStr = (String) getData();
        return dataStr.split(":")[2];
    }
}
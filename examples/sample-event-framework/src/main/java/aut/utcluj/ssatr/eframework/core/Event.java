/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aut.utcluj.ssatr.eframework.core;

public abstract class Event {
    protected String eventId;
    protected String eventType;
    protected long timestamp;
    protected Object data;

    public Event(String eventId, String eventType, Object data) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public long getTimestamp() { return timestamp; }
    public Object getData() { return data; }

    @Override
    public String toString() {
        return String.format("Event{id='%s', type='%s', time=%d}",
                           eventId, eventType, timestamp);
    }
}
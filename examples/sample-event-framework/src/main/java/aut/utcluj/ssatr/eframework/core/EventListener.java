/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package aut.utcluj.ssatr.eframework.core;

public interface EventListener {
    void onEvent(Event event);
    boolean canHandle(String eventType);
}
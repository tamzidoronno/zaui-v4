/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

/**
 *
 * @author ktonder
 */
public class RazberryLockLoggingResult {
    public int invalidateTime;
    public int updateTime;
    public Time time;
    public Event event;
    public uId uId;
    public EventString eventString;
}

class Time {
    public String value;
}

class Event {
    public int value;
}

class uId {
    public int value;
}

class EventString {
    public String value;
}

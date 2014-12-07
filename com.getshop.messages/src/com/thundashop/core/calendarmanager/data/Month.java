/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author ktonder
 */
public class Month extends DataCommon implements Comparable<Month> {
    public TreeMap<Integer, Day> days = new TreeMap();
    public int year;
    public int month;

    public Month() {
    }
    
    public Day getDay(int day) {
        Day dayObject = days.get(day);
        
        if (dayObject == null) {
            dayObject = new Day();
            dayObject.day = day;
            days.put(day, dayObject);
        }
        
        return dayObject;
    }
    
    public boolean is(int year, int month) {
        if (this.year == year && this.month == month)
            return true;
        else
            return false;
    }

    public void addEntry(int dayInt, Entry entry) {
        Day day = getDay(dayInt);
        day.add(entry);
    }

    public boolean isNewerOrEqual(int year, int month) {
        if (this.year > year) 
            return true;
        
        if (this.year == year && this.month >= month)
            return true;
        
        return false;
    }
    
    public Month(Month month) {
        this.month = month.month;
        for (Day day : month.days.values()) {
            Day newDay = new Day(day);
            this.days.put(newDay.day, newDay);
        }
    }
    
    @Override
    public Month clone() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, Month.class);
    }

    public boolean isOutOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int curyear = cal.get(Calendar.YEAR);
        int curmonth = cal.get(Calendar.MONTH) + 1;
        if (this.year > curyear) {
            return false;
        }
        if (this.year >= curyear && this.month >= curmonth) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Month o) {
        if (this.isNewerOrEqual(o.year, o.month)) {
            return 1;
        }
        
        return -1;
    }
}

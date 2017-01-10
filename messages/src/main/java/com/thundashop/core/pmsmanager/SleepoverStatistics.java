
package com.thundashop.core.pmsmanager;

import java.util.HashMap;

public class SleepoverStatistics {
    public Integer nightsSold = 0;
    public Integer nighsSlept = 0;
    //Total guests
    public HashMap<String, Integer> uniqueGuests = new HashMap();
    
    //Guests day by day.
    public HashMap<String, Integer> guests = new HashMap();
    public HashMap<String, Integer> guestsCompany = new HashMap();
    public HashMap<String, Integer> guestsConference = new HashMap();
    public HashMap<String, Integer> guestsRegular = new HashMap();
}

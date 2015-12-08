
package com.thundashop.core.pmsmanager;

import java.io.Serializable;

public class BookingContactData implements Serializable {
    static class ContactType {
        public static Integer privat = 1;
        public static Integer company = 2;
    }
    
    public Integer type = -1;
    public String orgid;
    public String birthday;
    public String name;
    public String address;
    public String postalCode;
    public String city;
    public String email;
}

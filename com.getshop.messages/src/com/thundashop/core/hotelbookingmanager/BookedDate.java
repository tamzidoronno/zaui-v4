package com.thundashop.core.hotelbookingmanager;

import java.io.Serializable;
import java.util.Date;

public class BookedDate implements Serializable {
    public int code;
    public Date date;
    public int bookingReference;
    
    @Override
    public String toString() {
        return date.toString() + " : " + bookingReference + " : " + code;
    }
}

package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.UUID;

public class PmsGuests implements Serializable {
    public String name;
    public String phone;
    public String email;
    public String prefix = "47";
    public String guestId = UUID.randomUUID().toString();
    public boolean isChild = false;
}

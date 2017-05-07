package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import java.io.Serializable;

public class PmsLockServer implements Serializable {
    public String locktype = "";
    public String arxHostname = "";
    @Administrator
    public String arxUsername = "";
    @Administrator
    public String arxPassword = "";
    public String arxCardFormat = "";
    public String arxCardFormatsAvailable = "";
    public Integer codeSize = 4;
    public boolean keepDoorOpenWhenCodeIsPressed = false;
    public String closeAllDoorsAfterTime = "22:00";

    public boolean isGetShopLockBox() {
        return locktype != null && locktype.equals("getshoplockbox");
    }

    public boolean isGetShopHotelLock() {
        return locktype != null && locktype.equals("getshophotellock");
    }
}

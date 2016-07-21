
package com.thundashop.core.getshop.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class GetShopLockCode implements Serializable {
    String code;
    Boolean used = false;
    Date addedToLock = null;
    Date needToBeRemoved = null;
    Integer slot = 0;
    
    public void refreshCode() {
        Integer newcode = new Random().nextInt(799999) + 200000;
        code = newcode + "";
        addedToLock = null;
        needToBeRemoved = null;
    }
}

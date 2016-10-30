package com.thundashop.core.pmsmanager;

import java.io.Serializable;

public class SimpleInventory implements Serializable {
    public String name;
    public String productId;
    public Integer originalCount;
    public Integer missingCount = 0;
}

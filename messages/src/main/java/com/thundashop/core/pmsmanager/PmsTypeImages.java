package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsTypeImages implements Serializable {
    public String fileId;
    public boolean isDefault = false;
    public String comment = "";
    public Date rowCreatedDate = new Date();
    public String filename = "";
}

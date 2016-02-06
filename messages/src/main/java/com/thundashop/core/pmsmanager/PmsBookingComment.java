
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsBookingComment implements Serializable {
    String userId = "";
    String comment = "";
    Date added = new Date();
}

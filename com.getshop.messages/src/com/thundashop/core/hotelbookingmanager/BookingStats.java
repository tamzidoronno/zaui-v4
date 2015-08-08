
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BookingStats extends DataCommon {
    LinkedHashMap<Long, BookingStatsDay> stats = new LinkedHashMap(); 
}

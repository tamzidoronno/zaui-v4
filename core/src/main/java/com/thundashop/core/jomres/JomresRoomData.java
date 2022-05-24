package com.thundashop.core.jomres;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;

import java.util.ArrayList;
import java.util.List;

@PermenantlyDeleteData
public class JomresRoomData extends DataCommon {
    String bookingItemId = "";
    int jomresPropertyId;
    List<Integer> jomresRoomIds = new ArrayList<Integer>();
}

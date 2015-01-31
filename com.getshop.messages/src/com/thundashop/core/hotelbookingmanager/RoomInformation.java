package com.thundashop.core.hotelbookingmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomInformation implements Serializable {
    public String roomId = "";
    public String cartItemId = "";
    public List<Visitors> visitors = new ArrayList();
}

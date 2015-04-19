package com.thundashop.core.hotelbookingmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RoomInformation implements Serializable {
    
    public static class RoomInfoState {
        public static Integer initial = 0;
        public static Integer externalDoorGranted = 1;
        public static Integer regrantAccess = 2;
        public static Integer accessGranted = 3;
    }
    
    public Integer code = -1;
    public String roomId = "";
    public List<Visitors> visitors = new ArrayList();
    public int roomState = 0;
    public String cartItemId = "";
}

package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.PmsTypeImages;
import java.util.HashMap;
import java.util.LinkedList;

public class BookingProcessRooms {
    public HashMap<Integer, Integer> roomsSelectedByGuests = new HashMap();
    public HashMap<Integer, Double> pricesByGuests = new HashMap();
    public Integer availableRooms = 0;
    public Integer maxGuests = 0;
    public Integer minGuests = 1;
    public String description = "";
    public String name = "";
    public String id = "";
    public Integer systemCategory = null;
    public LinkedList<PmsTypeImages> images = new LinkedList();
    public HashMap<String, String> utilities = new HashMap();
    public boolean visibleForBooker = false;
    public double totalPriceForRoom = 0.0;
    String userId = "";

    void sortDefaultImageFirst() {
        PmsTypeImages defaultImg = null;
        for(PmsTypeImages img : images) {
            if(img.isDefault) {
                defaultImg = img;
            }
        }
        
        LinkedList<PmsTypeImages> tmpImages = new LinkedList();
        if(defaultImg != null) {
            tmpImages.add(defaultImg);
        }
        
        for(PmsTypeImages img : images) {
            if(img.isDefault) {
                continue;
            }
            tmpImages.add(img);
        }
        
        images = tmpImages;
    }
}

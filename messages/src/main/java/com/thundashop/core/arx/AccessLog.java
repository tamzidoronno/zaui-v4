package com.thundashop.core.arx;

import java.io.Serializable;

public class AccessLog implements Serializable {
    public String personName;
    public String door;
    public String card;
    public long timestamp;
    public String type;
    public String dac_properties = "";
    
    public String toString() {
        return personName + "door, " + door + ",card: " + card + " : timestamp: " + timestamp + " ,type: " + type + ", dac_properties: " + dac_properties;
    }
}

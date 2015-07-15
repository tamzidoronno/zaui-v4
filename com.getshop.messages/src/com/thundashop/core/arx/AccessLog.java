package com.thundashop.core.arx;

import java.io.Serializable;

public class AccessLog implements Serializable {
    public String personName;
    public String door;
    public String card;
    public long timestamp;
    public String type;
    public String dac_properties = "";
}

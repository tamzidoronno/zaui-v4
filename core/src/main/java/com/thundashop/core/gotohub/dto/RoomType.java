package com.thundashop.core.gotohub.dto;

import java.util.ArrayList;
import java.util.List;

public class RoomType {
    public String room_type_code="";
    public String name="";
    public String description="";
    public String about="";
    public int number_of_unit;
    public String room_category="";
    public String size_measurement="";
    public String non_smoking="";
    public int max_guest=1;
    public int number_of_adults;
    public int number_of_children;
    public String status="";
    public List<String> images = new ArrayList<String>();
    public List<RatePlan> rate_plans = new ArrayList<RatePlan>();
}

package com.thundashop.core.yourcontestmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.List;

public class Contests extends DataCommon {
    public String title;
    public String description;
    public String longdesc;
    public String contestlink;
    public String imageLink;
    public String rulesLink;
    public Date startDate;
    public Date endDate;
    public Date addedDate;
    public Boolean featured;
    public String type;
    public List<String> tags;
    public String country_code;
    public List<String> provinces;
    public Integer rating;
    public Integer numraters;
    public Integer totalrating;
}

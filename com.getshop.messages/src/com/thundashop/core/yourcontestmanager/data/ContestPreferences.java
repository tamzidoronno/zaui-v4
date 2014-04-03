package com.thundashop.core.yourcontestmanager.data;

import java.io.Serializable;
import java.util.Date;

public class ContestPreferences implements Serializable {
    public Integer contestId;
    public Boolean entered;
    public Boolean notInterested;
    public Boolean favourite;
    public Date enteredDate;
}

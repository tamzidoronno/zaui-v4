package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class NewsLetterGroup extends DataCommon {
    public List<String> emails = new ArrayList();
    public String emailBody;
    public String title = "";
    public List<String> SentMailTo = new ArrayList();
}

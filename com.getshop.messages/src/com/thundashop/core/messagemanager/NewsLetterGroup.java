package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class NewsLetterGroup extends DataCommon {
    public List<String> userIds = new ArrayList();
    public String emailBody;
    public String title = "";
    public List<String> SentMailTo = new ArrayList();
    public List<String> attachments = new ArrayList();
}

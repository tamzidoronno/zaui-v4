package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsLetterGroup extends DataCommon {
    public List<String> userIds = new ArrayList();
    public String emailBody;
    public String title = "";
    public List<String> SentMailTo = new ArrayList();
    public HashMap<String, User> users = new HashMap();
}

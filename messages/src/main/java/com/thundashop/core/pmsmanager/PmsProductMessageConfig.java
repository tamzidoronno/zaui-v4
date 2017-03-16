package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PmsProductMessageConfig implements Serializable {
    String id = UUID.randomUUID().toString();
    String email = "";
    String emailTitle = "";
    String sms = "";
    List<String> productIds = new ArrayList();
    String language = "";
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SmsMessagePage {
    public int maxPages = 0;
    public int pageNumber = 0;
    public int pageSize = 0;
    public List<SmsMessage> messages = new ArrayList();
}

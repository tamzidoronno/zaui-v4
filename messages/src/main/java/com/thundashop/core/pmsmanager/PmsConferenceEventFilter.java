/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsConferenceEventFilter {
    public String userId;
    public Date start;
    public Date end;
    public String keyword;
    public List<String> itemIds = new ArrayList();
}

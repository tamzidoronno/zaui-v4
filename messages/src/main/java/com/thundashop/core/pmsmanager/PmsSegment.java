/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsSegment extends DataCommon {
    public String name;
    public List<String> types = new ArrayList();
    public boolean isPrivate = false;
    public boolean isBusiness = false;
    public String code;
    public String comment = "";
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class SupportStore extends DataCommon {
    public List<String> emails = new ArrayList();
    public List<String> adresses = new ArrayList();
    public boolean notifiedSupport = false;
    public String supportStoreId = "";
    public String mainEmailAdress = "";
    public String defaultWebAddress = "";
}

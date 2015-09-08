/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.appmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Setting;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class SavedApplicationSettings extends DataCommon {
    public HashMap<String, Setting> settings = new HashMap();
    public String applicationId;
}

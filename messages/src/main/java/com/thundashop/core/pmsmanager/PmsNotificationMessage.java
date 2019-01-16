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
public class PmsNotificationMessage extends DataCommon {
    public String content = "";
    public String title = "";
    public String type = "";
    public List<String> languages = new ArrayList();
    public List<String> prefixes = new ArrayList();
    public List<String> roomTypes = new ArrayList();
    public String key;
    public boolean isDefault = false;

    boolean containsLanguage(String language) {
        if(languages.isEmpty()) {
            return true;
        }
        if(languages.contains(language)) {
            return true;
        }
        return false;
    }

    boolean containsOneOrMoreRoomType(List<String> toCheckTypes) {
        for(String toCheck : toCheckTypes) {
            if(toCheck != null) {
                if(roomTypes.contains(toCheck)) {
                    return true;
                }
            }
        }
        return false;
    }
}

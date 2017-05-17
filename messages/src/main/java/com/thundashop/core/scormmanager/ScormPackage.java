/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ScormPackage extends DataCommon {
    public String name = "";
    public List<String> activatedGroups = new ArrayList();

    public List<String> groupedScormPackages = new ArrayList();

    public boolean isRequired = false;
    
    boolean isGroupActive(String groupId) {
        return activatedGroups.contains(groupId);
    }
}

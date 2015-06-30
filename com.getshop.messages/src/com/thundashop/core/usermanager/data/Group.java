/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Group extends DataCommon {
    public String groupName = "";
    public String imageId = "";
    private List<CertegoSystem> groupInformations = new ArrayList();

    public void addGroupInformation(CertegoSystem groupInfo) {
        if (groupInfo.getId() == null || groupInfo.getId().isEmpty()) {
            groupInfo.setId(UUID.randomUUID().toString());
        }
        
        removeGroupInformation(groupInfo.getId());
        groupInformations.add(groupInfo);
    }

    public void removeGroupInformation(String groupInformationId) {
        GroupInformation toRemove =  null;
        for (GroupInformation info : groupInformations) {
            if (info.getId().equals(groupInformationId)) {
                toRemove = info;
            }
        }
        
        if (toRemove != null) {
            groupInformations.remove(toRemove);
        }
    }
}

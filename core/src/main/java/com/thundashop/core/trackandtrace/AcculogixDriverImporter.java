/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AcculogixDriverImporter {
    private UserManager userManager;
    private String rawData;
    private final List<String[]> datas = new ArrayList();

    public AcculogixDriverImporter(UserManager userManager, String data) {
        this.userManager = userManager;
        this.rawData = data;
        
        loadLines();
        loadUsers();
    }

    private void loadLines() {
        for (String line : rawData.split("\n")) {
            if (line.contains("DP$ID"))
                continue;
            
            String[] values = line.split(",", -1);
            datas.add(values);
        }
    }

    private void loadUsers() {
        for (String[] data : datas) {
            User user = new User();
            user.id = data[1];
            user.fullName = data[2] + " " + data[3];
            user.username = data[3].toLowerCase();
            user.password = data[1].toLowerCase();
            
            userManager.saveUser(user);
            userManager.updatePassword(user.id, data[1].toLowerCase(), data[1].toLowerCase());
            
            userManager.addMetaData(user.id, "depotId", data[0]);
            
            String groupId = data[6];
            if (!groupId.isEmpty()) {
                Group group = userManager.getGroup(groupId);
                if (group == null) {
                    group = new Group();
                    group.id = groupId;
                    group.groupName = groupId;
                    userManager.saveGroup(group);
                }
                
                userManager.addGroupToUser(user.id, groupId);
            }
            
        }
    }
    
}

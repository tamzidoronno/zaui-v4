/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Group extends DataCommon {
    public String groupName = "";
    public String imageId = "";

    @Override
    public String toString() {
        return "Name: " + groupName + " id: " + id + " image: " + imageId;
    }
    
    
}

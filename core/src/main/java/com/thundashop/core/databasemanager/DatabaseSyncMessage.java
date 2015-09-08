/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.MessageBase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DatabaseSyncMessage extends MessageBase {
    public List<String> completeNetwork = new ArrayList();
    public List<ManagerData> managerDatas = new ArrayList();  
}

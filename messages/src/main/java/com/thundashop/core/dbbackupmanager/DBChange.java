/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.dbbackupmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DataCommonBackup;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DBChange implements Serializable {
    public String id = "";
    public String byUserId = "";
    public String className = "";
    public Date timeStamp = null;

    public DBChange(DataCommon data) {
        if (!(data instanceof DataCommonBackup)) {
            return;
        }
        
        DataCommonBackup backup = (DataCommonBackup)data;
        className = backup.originalClassName;
        byUserId = backup.doneByUserId;
        timeStamp = backup.rowCreatedDate;    
        id = backup.id;
    }
}

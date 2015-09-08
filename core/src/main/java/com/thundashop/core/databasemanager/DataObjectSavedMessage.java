/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.MessageBase;
import com.thundashop.core.databasemanager.data.Credentials;

/**
 *
 * @author ktonder
 */
public class DataObjectSavedMessage extends MessageBase {
    public Credentials credentials;
    public DataCommon data;
}

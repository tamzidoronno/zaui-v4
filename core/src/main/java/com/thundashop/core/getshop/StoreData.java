/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop;

import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class StoreData {
    public Credentials credentials;
    public List<DataCommon> dbObjects = new ArrayList();
}

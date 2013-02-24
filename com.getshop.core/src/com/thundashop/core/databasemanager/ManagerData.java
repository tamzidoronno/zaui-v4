/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.MessageBase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ManagerData extends MessageBase {
    public String database;
    public String collection;
    public List<DataCommon> datas = new ArrayList();
}

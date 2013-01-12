/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.databasemanager.data.Credentials;

/**
 *
 * @author ktonder
 */
public interface DatabaseSaver {
    public void saveObject(DataCommon data, Credentials credentials) throws ErrorException;
    public void deleteObject(DataCommon data, Credentials credentials) throws ErrorException;
}

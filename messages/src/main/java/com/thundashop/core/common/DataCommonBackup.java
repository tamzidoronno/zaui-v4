/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import difflib.Delta;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DataCommonBackup extends DataCommon {
    public DataCommon oldObject;
    public List<Delta> deltas;
    public String doneByUserId;
    public String originalClassName;
    public String originalId;
    public String database;
}
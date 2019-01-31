/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class ExportedCollectedData extends DataCommon {
    public CollectionTasks collectionTasks;
    public String routId = "";
    public long tntId = 0;

    public CollectionTasks getCollectionTask() {
        collectionTasks.tntId = tntId;
        return collectionTasks;
    }
}

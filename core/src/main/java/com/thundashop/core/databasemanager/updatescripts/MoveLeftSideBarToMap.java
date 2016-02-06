/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class MoveLeftSideBarToMap extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("03/02-2016");
    }
    
    @Override
    public String getId() {
        return "6e07d7df-3b88-4e77-8b40-9f42940c14ba";
    }
    
    public static void main(String[] args) {
        new MoveLeftSideBarToMap().runSingle();
    }
    
    @Override
    public void run() {
        Map<Store, List<DataCommon>> datas = getAllData(PageManager.class);
        for (DataCommon store : datas.keySet()) {
            List<DataCommon> dataObjects = datas.get(store);
            for (DataCommon data : dataObjects) {
                if (data instanceof CommonPageData) {
                    CommonPageData common = (CommonPageData)data;
                    if (!common.leftSideBars.containsKey("left_side_bar")) {
                        common.leftSideBars.put("left_side_bar", common.leftSideBar);
                        database.save(PageManager.class, common);
                    }
                }
            }
        }
    }
}

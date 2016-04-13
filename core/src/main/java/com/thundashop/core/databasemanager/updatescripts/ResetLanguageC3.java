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
import com.thundashop.core.productmanager.ProductManager;
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
public class ResetLanguageC3 extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("13/04-2016");
    }
    
    @Override
    public String getId() {
        return "8acc5967-1c7b-44ff-a43c-1ddc64bb4f38";
    }
    
    public static void main(String[] args) {
        new ResetLanguageC3().runSingle();
    }
    
    @Override
    public void run() {
        List<DataCommon> data = database.getAllDataForStore("f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1");
        for (DataCommon ob : data) {
            if(ob.hasTranslations()) {
                ob.resetLanguage();
                database.save(ob.gs_manager, ob.colection, ob);
            }
        }
    }
}

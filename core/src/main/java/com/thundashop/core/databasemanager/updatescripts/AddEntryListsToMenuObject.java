/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists. Needed to add a one to many relation between application and entrylist.
 * 
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.EntryList;
import com.thundashop.core.listmanager.data.Menu;
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
public class AddEntryListsToMenuObject extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("30/1-2016");
    }
    
    @Override
    public String getId() {
        return "dd5ba060-4493-4253-a94f-998350c30449";
    }
    
    public static void main(String[] args) {
        new AddEntryListsToMenuObject().runSingle();
    }
    
    @Override
    public void run() {
        Map<Store, List<DataCommon>> datas = getAllData(ListManager.class);
        for (Store store : datas.keySet()) {
            List<DataCommon> objects = datas.get(store);
            
            // Delete old Menu objects
            objects.stream()
                    .filter(o -> o.getClass().equals(Menu.class))
                    .forEach(o -> database.delete(ListManager.class, o));
            
            objects.stream()
                    .filter(o -> o.getClass().equals(EntryList.class))
                    .forEach(o -> addMenu(store, (EntryList)o));
        }
    }

    private void addMenu(Store store, EntryList o) {
        Menu menu = new Menu();
        menu.name = "Default Menu";
        menu.entryListId = o.appId;
        menu.appId = o.appId;
        menu.storeId = store.id;
        database.save(ListManager.class, menu);
    }
}

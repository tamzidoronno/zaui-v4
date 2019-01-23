/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.DayIncomeReport;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class MakeUUIDToDayIncome extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("23/01-2019");
    }
    
    @Override
    public String getId() {
        return "73f9b708-e950-4a21-95ab-6b90917ecc91";
    }
    
    public static void main(String[] args) {
        new MakeUUIDToDayIncome().runSingle();
    }
    
    @Override
    public void run() {
        List<Store> stores = storePool.getAllStores();
        for (Store store : stores) {
            BasicDBObject query = new BasicDBObject();
            query.put("className", DayIncomeReport.class.getCanonicalName());
            
            List<DayIncomeReport> reports = database.query("OrderManager", store.id, query).stream()
                    .map(o -> (DayIncomeReport)o)
                    .collect(Collectors.toList());
            
            
            for (DayIncomeReport report : reports) {
                for (DayIncome inc : report.incomes) {
                    inc.id = UUID.randomUUID().toString();
                }
                database.save("OrderManager", "col_"+store.id, report);
            }
        }
    }
}

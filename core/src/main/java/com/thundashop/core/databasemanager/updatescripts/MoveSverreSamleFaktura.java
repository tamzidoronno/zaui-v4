/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import java.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class MoveSverreSamleFaktura extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("08/01-2019");
    }
    
    @Override
    public String getId() {
        return "92c8a5df-8b31-487f-82bb-feed48bc6d77";
    }
    
    public static void main(String[] args) {
        new MoveSverreSamleFaktura().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "fd2fecef-1ca1-4231-86a6-0ec445fbac83";
        List<DataCommon> data = getAllDataForStoreAndManager("OrderManager", store);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, 2019);
        
        Date firstOfJan2019 = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDayOf2018 = cal.getTime();
        
        List<Order> ordersToHaveALookAt = data.stream()
            .filter(o -> {
                if (o instanceof Order)
                    return true;

                return false;
            })
            .map(o -> (Order)o)
            .filter(o -> o.isSamleFaktura())
            .filter(o -> o.rowCreatedDate.after(firstOfJan2019) || (o.overrideAccountingDate != null && o.overrideAccountingDate.after(firstOfJan2019)))
            .collect(Collectors.toList());
        
        ordersToHaveALookAt.stream()
            .forEach(order -> {
                if (order.overrideAccountingDate != null) {
                    order.overrideAccountingDate = lastDayOf2018;
                }

                order.rowCreatedDate = lastDayOf2018;
                database.save(OrderManager.class, order);
            });
    }
}

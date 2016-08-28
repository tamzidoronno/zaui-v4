/**
 * Gjenstår på WH:
 * 1. Få statistikken til å bli riktig
 * 2. Expedia må merkes som betalt med en gang (OK)
 * 3. Overføring til visma igjen
 * 4. Varlslingen av bookinger etter 5 og helger booking.com må på plass igjen
 * 5. Oppdatering av priser og avialability (egentlig done)
 * 6. No show må merkes riktig (DONE)
 * 
 */
package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.util.List;

public class WuBookManagerProcessor extends GetShopSchedulerBase {

    public WuBookManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }    
    
    @Override
    public void execute() throws Exception {
        long start = System.currentTimeMillis();
        GetShopLogHandler.logPrintStatic("Searching for new bookings", null);
        getApi().getWubookManager().addNewBookingsPastDays(getMultiLevelName(), 2);
        GetShopLogHandler.logPrintStatic("Wubook operation takes:" + (System.currentTimeMillis() - start), null);
    }
    
}

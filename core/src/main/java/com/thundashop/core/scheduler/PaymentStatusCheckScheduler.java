package com.thundashop.core.scheduler;

import com.getshop.scope.GetShopSchedulerBase;

/**
 * @author Naim Murad (naim)
 * @since 6/28/22
 */
public class PaymentStatusCheckScheduler extends GetShopSchedulerBase {

    public PaymentStatusCheckScheduler(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    public PaymentStatusCheckScheduler() {
    }

    @Override
    public void execute() throws Exception {
        getApi().getOrderManager().checkForOrdersFailedCollecting();
    }

}

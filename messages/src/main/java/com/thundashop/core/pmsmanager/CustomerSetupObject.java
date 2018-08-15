
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

public class CustomerSetupObject extends DataCommon {
    public Date storeDate = null;
    public Integer paymentMethodsSetup = 0;
    public Integer numberOfMessagesSetup = 0;
    public boolean acceptedGdpr = false;
    public boolean addedTermsAndConditions = false;
    public Integer numberOfCategoriesAdded = 0;
    public Integer numberOfRoomsAdded = 0;
    public boolean websiteCompleted = false;
    public Integer numberOfProductsCreated = 0;
    public boolean completedAccountingIntegration = false;
    public boolean completedPaymentlinkTest = false;
    public boolean pricesCompleted = false;
    public Integer wubookSetUpState = 0; //0 no configuration, 1 users added, 2 bookings income.
    public Integer numberOfFieldsSetupInInvoice = 0;
    public boolean locksInstalled = false;
    public boolean trainingCompleted = false;
    public boolean completed = false;
    public String customerStoreId;
    public String address;
    public String comment = "";
}

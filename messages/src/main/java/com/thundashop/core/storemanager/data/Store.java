package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author ktonder
 */
public class Store extends DataCommon {
    public String webAddress;
    public String webAddressPrimary;
    public String identifier;
    public List<String> additionalDomainNames;
    public boolean registeredDomain=false;
    public boolean readIntroduction=false;
    public boolean isExtendedMode=true;
    public boolean deactivated=false;
    public StoreConfiguration configuration;
    public boolean isVIS = false; //Very important shop.
    public boolean isDeepFreezed = false;
    public String deepFreezePassword = "";
    public boolean premium = false;
    public boolean mobileApp = false;
    public String favicon = "";
    public String masterStoreId = "";
    public List<String> acceptedSlaveIds = new ArrayList();
    public Integer incrementalStoreId = null;
    
    /**
     * If this is set the it will default to this 
     * multilevelname unless its specified something else.
     * 
     */
    public String defaultMultilevelName = "default";
    
    public Date expiryDate;
    
    /**
     * This specifies if this store is setup as a template
     * If it is a template it can be cloned into another store :D
     */
    public boolean isTemplate = false;
    public User registrationUser;
    public String country;
    
    /**
     * Never make this one public,
     * all functions to work with times should already be 
     * existing her, see:
     * 
     * getCurrentTimeInTimeZone
     * getTimeZoneDifferenceInHours
     * convertToTimeZone
     * 
     */
    private String timeZone;
    
    public boolean acceptedGDPR = false;
    public Date acceptedGDPRDate = null;
    public String acceptedByUser = "";
    public boolean newPaymentProcess = false;

    public String getDefaultMailAddress() {
        if (configuration.emailAdress == null || configuration.emailAdress.isEmpty()) {
            return "post@getshop.com";
        }
        
        return configuration.emailAdress;
    }

    public String getDefaultWebAddress() {
        if (webAddress != null && !webAddress.isEmpty()) {
            return webAddress;
        }
        
        if (webAddressPrimary != null && !webAddressPrimary.isEmpty()) {
            return webAddressPrimary;
        }
        
        if (additionalDomainNames != null && !additionalDomainNames.isEmpty()) {
            return additionalDomainNames.get(0);
        }
        
        return null;
    }
   
    public boolean isPikStore() {
        Calendar pikTime = Calendar.getInstance();
        pikTime.set(Calendar.YEAR, 2018);
        pikTime.set(Calendar.MONTH, 3);
        pikTime.set(Calendar.DAY_OF_MONTH, 1);
        return rowCreatedDate.after(pikTime.getTime());
    }
    
    public Date getCurrentTimeInTimeZone() {
        if(timeZone == null || timeZone.isEmpty()) {
            return new Date();
        }
        
        Date date = new Date();
        TimeZone tz1 = TimeZone.getTimeZone(timeZone);
        TimeZone tz2 = TimeZone.getDefault();
        long timeDifference = tz1.getOffset(date.getTime())- tz2.getOffset(date.getTime());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, (int)timeDifference);
        
        return cal.getTime();
    }
    
    public Date convertToTimeZone(Date timing) {

        if(timeZone == null || timeZone.isEmpty()) {
            return timing;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(timing);
        
        TimeZone tz1 = TimeZone.getTimeZone(timeZone);
        TimeZone tz2 = TimeZone.getDefault();
        long timeDifference = tz1.getOffset(timing.getTime())- tz2.getOffset(timing.getTime());
        
        cal.add(Calendar.MILLISECOND, (int)timeDifference);
        return cal.getTime();
    }

    public int getTimeZoneDifferenceInHours(Date dateToCheckAgainst) {
        
        if(timeZone != null && !timeZone.isEmpty()) {
            TimeZone tz1 = TimeZone.getTimeZone(timeZone);
            TimeZone tz2 = TimeZone.getDefault();
            long timeDifference = tz1.getOffset(dateToCheckAgainst.getTime())- tz2.getOffset(dateToCheckAgainst.getTime());
            if(timeDifference != 0) {
                long seconds = timeDifference / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                return (int)hours;
            }
        }
        
        return 0;
    }

    public void setTimeZone(String timezone) {
        this.timeZone = timezone;
    }
    public String getTimeZone() {return this.timeZone;}
    
    public String getTimeZone() {
        return this.timeZone;
    }
    
}
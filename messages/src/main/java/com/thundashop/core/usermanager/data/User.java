/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hjemme
 */

public class User extends DataCommon implements Comparable<User> {
    public int resetCode;

    public List<UserPrivilege> privileges = new ArrayList();
    public List<UserCard> savedCards = new ArrayList();
    public String lastRegisteredToken;
    public boolean triedToFetch = false;
    public boolean suspended = false;
    
    @Transient
    public Company companyObject;
    
    public boolean virtual = false;
    
    @Transient
    public String useGroupId = "";
 
    public void cleanWhiteSpaces() {
        cleanWhiteSpace(cellPhone);
        cleanWhiteSpace(emailAddressToInvoice);
        cleanWhiteSpace(emailAddress);
    }
    
    private void cleanWhiteSpace(String toClean) {
        if (toClean != null) 
            toClean = toClean.trim();
        
    }

    public String getName() {
        if (fullName == null)
            return "";
        
        return fullName;
    }

    public String getCellPhone() {
        String numb = "";
        if (prefix != null && !prefix.isEmpty()) {
            numb += prefix;
        }
        
        if (cellPhone != null && !cellPhone.isEmpty()) {
            numb += cellPhone;
        }
        
        return numb;
    }

    public Date getBirthDate() {
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("MM.dd.YY");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("MM.dd.yyyy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("mm-dd-YY");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat("mm-dd-yyyy");
            return formater.parse(birthDay);
        }catch(Exception e) {
            
        }
        
        return null;
    }
 
    public static class Type {
        public static int GETSHOPADMINISTRATOR = 200;
        public static int ADMINISTRATOR = 100;
        public static int EDITOR = 50;
        public static int CUSTOMER = 10;
    }
    
    public Address address;
    public String fullName = "";
    public String emailAddress = "";
    public String emailAddressToInvoice = "";
    public String prefix = "47";
    public String password = "";
    public String username = "";
    public int type = 0;
    public int loggedInCounter = 0;
    public Date lastLoggedIn;
    public Date prevLoggedIn;
    public Date expireDate;
    public String birthDay;
    public String cellPhone;
    public HashMap<String, Comment> comments = new HashMap();
    public HashMap<String, String> metaData = new HashMap();
    public String key;
    public String group;
    public String userAgent;
    public boolean hasChrome;
    public boolean isTransferredToAccountSystem = false;
    public String accountingId = "";
    public String referenceKey = "";
    public String pinCode = null;
    public boolean isCompanyOwner = false;
    public boolean wantToBecomeCompanyOwner = false;
    public String preferredPaymentType = "";
    public Integer invoiceDuePeriode = 14;
    public int sessionTimeOut = 180;
    public boolean canChangeLayout = false;
    public String couponId = "";
    public String profilePicutreId = "";
    
    public List<String> company = new ArrayList();
    
    /**
     * Autogenerated numeric id,
     */
    public Integer customerId = -1;
    
    public double discount = 0;
    
    public boolean showHiddenFields = false;
    
    public boolean showLoguotCounter = false;
    
    //ApplicationId, int = 0 rw, 1=r, 2=w
    public HashMap<String, Integer> applicationAccessList = new HashMap();

    @Transient
    public String partnerid;
    
    /**
     * If the user is connected
     * to a perticullary application,
     * this will be set to the application instance id.
     */
    public String appId = ""; 
    
    /**
     * This user can be connected to
     * a group(s).
     */
    public List<String> groups;

    public void ValidateUserFields() throws ErrorException {
        if (fullName.equals("")) {
            throw new ErrorException(2);
        }
        
        if (emailAddress.equals("")) {
            throw new ErrorException(4);
        }
    }
    
    public boolean isGetShopAdministrator() {
        return (type == User.Type.GETSHOPADMINISTRATOR);
    }
    
    public boolean isAdministrator() {
        return (type == User.Type.ADMINISTRATOR);
    }
    
    public boolean isEditor() {
        return (type == User.Type.EDITOR);
    }
    
    public boolean isCustomer() {
        return (type == User.Type.CUSTOMER);
    }
    
    public boolean hasKey(String key) {
        if(this.key != null && this.key.equals(key)) {
            return true;
        }
        
        return false;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    /** 
     * Generates a checksum for the user that can be used to compare users to eachother 
     */
    @Override
    public String toString() {
       String toCompare = "";
       if(fullName != null) {
           toCompare += fullName;
       }
       if(emailAddress != null) {
           toCompare += emailAddress;
       }
       if(address != null) {
           if(address.address != null) {
               toCompare += address.address;
           }
           if(address.city != null) {
               toCompare += address.city;
           }
           if(address.postCode != null) {
               toCompare += address.postCode;
           }
       }
       if(cellPhone != null) {
           toCompare += cellPhone;
       }
       toCompare += " customerId, " + customerId;
       
       return toCompare;
    }
    
    @Override
    public int compareTo(User o) {
        if (o.fullName != null && fullName != null) {
            return o.fullName.compareTo(fullName);
        }
        
        if(o.rowCreatedDate == null || rowCreatedDate == null) {
            return 0;
        }
        return rowCreatedDate.compareTo(o.rowCreatedDate);
    }

    public User jsonClone() {
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        String json = gson.toJson(this);
        User copied = gson.fromJson(json, User.class);
        return copied;
    }    

    public boolean matchByString(String search) {
        if (search == null || search.isEmpty())
            return true;
        
        
        return toString().toLowerCase().contains(search.toLowerCase());
    }
}

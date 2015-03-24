/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.cartmanager.data.Cart;
import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    
    public String password = "";
    public String username = "";
    
    public int type = 0;
    
    public int loggedInCounter = 0;
    
    public Date lastLoggedIn;
    public Date expireDate;
    
    public String birthDay;
    
    public String companyName;
    
    public String cellPhone;
    
    public HashMap<String, Comment> comments = new HashMap();
    
    public String key;
    
    public String userAgent;
    public boolean hasChrome;
    
    public String referenceKey = "";
    
    public boolean isPrivatePerson = true;
    public boolean mvaRegistered = false;
    
    public Company company = null;
    
    public Integer customerId = -1;
    
    public double discount = 0;
    
    //ApplicationId, int = 0 rw, 1=r, 2=w
    public HashMap<String, Integer> applicationAccessList = new HashMap();

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
       return toCompare;
    }


    
    @Override
    public int compareTo(User o) {
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

}

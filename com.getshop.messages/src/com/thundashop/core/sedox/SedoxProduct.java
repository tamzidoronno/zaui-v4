/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.usermanager.data.User;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class SedoxProduct extends SedoxProductCopiedData implements Comparable<SedoxProduct> {

    public String firstUploadedByUserId;
    public String useCreditAccount;
    public String comment;
    public boolean started;
    public Boolean isFinished = false;
    public String uploadOrigin;
    public Map<String,String> reference = new HashMap();
    public Map<String, Date> states = new HashMap();
    public String startedByUserId = "";
    public List<SedoxProductHistory> histories = new ArrayList();
    public Date startedDate;
    public String sharedProductId = "";
    public boolean isBuiltFromSpecialRequest = false;
    public boolean duplicate = false;
    
    @Override
    public int compareTo(SedoxProduct o) {
        if (rowCreatedDate.after(o.rowCreatedDate)) {
            return -1;
        } else if (rowCreatedDate.before(o.rowCreatedDate)) {
            return 1;
        } else {
            return 0;
        }  
    }
    
    public String fileSafeName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Failed to make safe filename", ex);
        }
    }

    private SedoxProductHistory getHistoryEntry(String userId, String comment) {
        SedoxProductHistory hist = new SedoxProductHistory();
        hist.dateCreated = new Date();
        hist.description = comment;
        hist.userId = userId;
        return hist;
    }

    void addCreatedHistoryEntry(String userId) {
        histories.add(getHistoryEntry(userId, "Product created"));
    }
    
    void addFileAddedHistory(String userId, String fileName) {
        String fileComment = "Added " + fileName + " file.";
        histories.add(getHistoryEntry(userId, fileComment));
    }

    void addDeveloperHasBeenNotifiedHistory(String userId) {
        histories.add(getHistoryEntry(userId, "Sedox file developers has been notified"));
    }
    
    void addSmsSentToCustomer(String userId, User user) {
        histories.add(getHistoryEntry(user.id, "User " + user.fullName + " has been notified on SMS number: " + user.cellPhone));
    }
    
    void addProductSentToEmail(String userId, User user) {
        histories.add(getHistoryEntry(user.id, "Product has been sent to " + user.fullName + " on email address: " + user.emailAddress));
    }
    
    void addCustomerNotified(String userId, User user) {
        histories.add(getHistoryEntry(user.id, "User " + user.fullName + " has been notified on email address: " + user.emailAddress));
    }
    
    
    void addMarkedAsStarted(String userId, boolean started) {
        if (started) {
            histories.add(getHistoryEntry(userId, "Product has been marked as started"));        
        } else {
            histories.add(getHistoryEntry(userId, "Product has been marked as stopped"));
        }
    }

    @Override
    public String toString() {
        throw new RuntimeException("This is no longe in use, use shared sedox product");
    }
}
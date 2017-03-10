/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager.data;

import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author boggi
 * @param navigateByPages
 */
public class Entry extends TranslationHandler implements Serializable {
    public boolean navigateByPages = true;
    @Translation
    public String name;
    public String parentId;
    public String pageId;
    public String scrollPageId = "";
    public String scrollAnchor = "";
    public boolean pageScroll = false;
    public String imageId;
    public String hardLink;
    public int userLevel;
    public String id;
    public String productId;
    public String fontAwsomeIcon;
    public int counter = -1;
    public boolean hidden = false;
    public boolean openInSeperatedTab = false;
    public boolean confirmed = false;
    public String prefix = "";
    public String phone = "";
    public String emailToSendConfirmationTo = "";
    public HashMap<String, String> metaData = new HashMap();
    public int badges = 0;
    
    //Appended when fetching the list.
    public List<Entry> subentries;
    
    public List<String> disabledLangues = new ArrayList();
    
    @Transient
    //If you don't want it to use the default page type 1, then specify it here on creation.
    public int pageType = 1;
    
    @Transient
    public String uniqueId;
    
    
    public List<Entry> getAllEntries() {
        List<Entry> retEntries = new ArrayList();
        retEntries.add(this);
        
        if (subentries != null) {
            for (Entry entry : subentries) {
                retEntries.addAll(entry.getAllEntries());
            }
        }
        
        return retEntries;
    }
}

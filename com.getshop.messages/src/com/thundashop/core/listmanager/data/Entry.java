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
    public String imageId;
    public String hardLink;
    public int userLevel;
    public String id;
    public String productId;
    public String fontAwsomeIcon;
    public int counter = -1;
    
    //Appended when fetching the list.
    public List<Entry> subentries;
    
    public List<String> disabledLangues = new ArrayList();
    
    @Transient
    //If you don't want it to use the default page type 1, then specify it here on creation.
    public int pageType = 1;
    
    @Transient
    public String uniqueId;
    }

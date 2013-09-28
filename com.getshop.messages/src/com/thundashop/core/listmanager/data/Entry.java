/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager.data;

import com.google.code.morphia.annotations.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author boggi
 * @param navigateByPages
 */
public class Entry implements Serializable {
    public boolean navigateByPages = true;
    public String name;
    public String parentId;
    public String pageId;
    public String imageId;
    public String hardLink;
    public int userLevel;
    public String id;
    public String productId;
    public int counter = -1;
    
    //Appended when fetching the list.
    public List<Entry> subentries;
    
    @Transient
    //If you don't want it to use the default page type 1, then specify it here on creation.
    public int pageType = 1;
    
    @Transient
    public String uniqueId;
}

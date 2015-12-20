/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.appmanager.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class ApplicationModule {
    public boolean needToShowInMenu = false;
    public String moduleName;
    public String id;
    public String description;
    public String faIcon;
    
    /**
     * If empty = all stores allowed to this module.
     */
    public List<String> allowedStoreIds = new ArrayList();
}

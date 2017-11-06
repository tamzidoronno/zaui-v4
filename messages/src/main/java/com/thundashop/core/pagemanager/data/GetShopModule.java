/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.DataCommon;

/**
 * A GetShop Module is a system, all modules will be present at top of the webpage,
 * when a module is selected it will have its own template.
 * 
 * Examples of a module:
 * - CMS (Default if none is selected)
 * - LMS
 * - PMS
 * - ECommerce
 * - CRM
 * 
 * @author ktonder
 */
public class GetShopModule extends DataCommon {
    public String name;
    public boolean storeDataRemoteForThisModule = false;
    public Integer sequence;
    public String themeApplicationId = "";
    public String fontAwesome;
}

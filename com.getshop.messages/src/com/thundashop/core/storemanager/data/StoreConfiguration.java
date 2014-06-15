/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Translation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class StoreConfiguration extends DataCommon {
    public String homePage = "home";
    public String phoneNumber;
    public String Adress;
    public String postalCode;
    public String streetAddress;
    public String emailAdress;
    public String orgNumber;
    public String contactContent;
    public String theeme;
    public String shopName;
    public String contactFirstName;
    public String contactLastName;
    public String city;
    public String state;
    public String country;
    @Translation
    public List<TranslationObject> translationMatrix = new ArrayList();
    public HashMap<String, String> configurationFlags;
    public String customCss = "";
    
    public Colors colors = new Colors();
    
    public boolean hasSMSPriviliges = false;
    public boolean hasSelectedDesign = false;
}

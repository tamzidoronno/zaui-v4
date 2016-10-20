package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Company extends DataCommon {
    public Address invoiceAddress;
    public Address address;
    public String vatNumber = "";
    public String type = "";
    public String name = "";
    public String prefix = "47";
    public String phone = "";
    public boolean vatRegisterd = true;
    public String invoiceEmail = "";
    public String website = "";
    public String email = "";
    public String contactPerson = "";
    public String groupId = "";
    
    public String reference = "";
    
    /* This are legacy attributes, replaced with address field*/
    public String streetAddress = "";
    public String postnumber = "";
    public String country = "";
    public String city = "";
    
    public String invoiceReference = "";
    
    public List<Company> subCompanies = new ArrayList();
    
    public String companyLeaderUserId = "";
}
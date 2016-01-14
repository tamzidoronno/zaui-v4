package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;

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
    public String prefix = "";
    public String phone = "";
    public boolean vatRegisterd = true;
    public String invoiceEmail = "";
    public String website = "";
    public String email = "";
    public String contactPerson = "";
}

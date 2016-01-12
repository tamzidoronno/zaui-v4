package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Company extends DataCommon {
    public Address invoiceAddress;
    public Address address;
    public String postnumber = "";
    public String vatNumber = "";
    public String country = "";
    public String type = "";
    public String city = "";
    public String name = "";
    public String prefix = "";
    public String phone = "";
    public boolean vatRegisterd = true;
    public String invoiceEmail = "";
}

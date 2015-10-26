
package com.thundashop.core.salesmanager;

import com.thundashop.core.common.DataCommon;

public class SalesCustomer extends DataCommon {
    public String orgid;
    public String name;
    public String streetAddress;
    public String phone;
    public String contactPerson;
    public String website;
    public String state;
    public String type;
    public String comment;

    public void update(SalesCustomer customer) {
        name = customer.name;
        streetAddress = customer.streetAddress;
        phone = customer.phone;
        contactPerson = customer.contactPerson;
        website = customer.website;
        state = customer.state;
        comment = customer.comment;
        type = customer.type;
    }
}

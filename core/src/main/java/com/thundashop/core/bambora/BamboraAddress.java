
package com.thundashop.core.bambora;

import com.thundashop.core.usermanager.data.User;

public class BamboraAddress {
    public String att = "";
    public String city = "";
    public String country = "";
    public String firstname = "";
    public String lastname = "";
    public String street = "";
    public String zip = "";

    void setAddress(User user) {
        att = "";
        city = user.address.city;
        country = "NO";
        if(user.fullName != null && user.fullName.indexOf(" ") > 0) {
            firstname = user.fullName.split(" ")[0];
            lastname = user.fullName.substring(user.address.fullName.indexOf(" "));
        } else {
            firstname = "";
            lastname = "";
        }
        street = user.address.address;
        zip = user.address.postCode;
    }
}

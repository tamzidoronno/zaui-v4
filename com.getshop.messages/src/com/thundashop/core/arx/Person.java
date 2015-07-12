
package com.thundashop.core.arx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person {
    public String id;
    public String lastName = "";
    public String firstName = "";
    public List<AccessCategory> accessCategories = new ArrayList();
    public Date endDate = null;
    public List<Card> cards = new ArrayList();
    public boolean deleted = false;
}

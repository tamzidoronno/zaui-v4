
package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RegistrationRulesField implements Serializable {
    public boolean required = false;
    public boolean active = false;
    public String type = "";
    public List<String> additional = new ArrayList();
    public String dependsOnCondition = "";
    public String title = "";
    public String name = "";
    public String width = "100";
}

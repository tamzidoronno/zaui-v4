
package com.thundashop.core.arx;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person extends DataCommon {
    public String lastName = "";
    public String firstName = "";
    public List<AccessCategory> accessCategories = new ArrayList();
    public Date endDate = null;
}

package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author boggi
 */
public class PmsConferenceItem extends DataCommon {
    public String name = "";
    public String toItemId = "";
    
    @Transient
    public boolean hasSubItems = false;
    
    @Transient
    public List<String> subItems = new ArrayList();
}

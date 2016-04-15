package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonPageData extends DataCommon {
    public Map<String, ArrayList<PageCell>> leftSideBars = new HashMap();
    public Map<String, ArrayList<PageCell>> modals = new HashMap();
    
    /**
     * Legacy, this can be deleted after the script "MoveLeftSideBarToMap" has been runned, 
     * is not in use anywhere in the code
     */
    public ArrayList<PageCell> leftSideBar = new ArrayList();
    
    public ArrayList<PageCell> header = new ArrayList();
    public ArrayList<PageCell> footer = new ArrayList();
    
    public ArrayList<PageCell> bodyFooter = new ArrayList();
    
    public String mobileLink = "";
}

package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.LinkedList;

public class CommonPageData extends DataCommon {
    public LinkedList<PageCell> header = new LinkedList();
    public LinkedList<PageCell> footer = new LinkedList();
}

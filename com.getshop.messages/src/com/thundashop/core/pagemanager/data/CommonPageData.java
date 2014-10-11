package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.DataCommon;

public class CommonPageData extends DataCommon {
    public PageCell header = new PageCell();
    public PageCell footer = new PageCell();

    public CommonPageData() {
        header.cellId = "header";
        footer.cellId = "footer";
    }
}

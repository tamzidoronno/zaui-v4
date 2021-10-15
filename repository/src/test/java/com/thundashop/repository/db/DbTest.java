package com.thundashop.repository.db;

import com.thundashop.core.common.DataCommon;

public class DbTest extends DataCommon {

    private String strMatch;

    private int order;

    public DbTest() {
    }

    public DbTest(String strMatch) {
        this.strMatch = strMatch;
    }

    public String getStrMatch() {
        return strMatch;
    }

    public void setStrMatch(String strMatch) {
        this.strMatch = strMatch;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

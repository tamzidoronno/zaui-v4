package com.thundashop.repository.db;

import com.thundashop.core.common.DataCommon;

import java.util.Date;

public class DbTest extends DataCommon {

    private String strMatch;

    private int order;

    private Date testDate;

    public DbTest() {
    }

    public DbTest(String strMatch) {
        this.strMatch = strMatch;
    }

    public DbTest(String id, String strMatch) {
        this.id = id;
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

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }
}

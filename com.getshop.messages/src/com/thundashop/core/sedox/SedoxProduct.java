/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxProduct extends DataCommon implements Comparable<SedoxProduct> {
    public List<SedoxBinaryFile> binaryFiles = new ArrayList();
    public List<SedoxProductHistory> histories = new ArrayList();
    public String filedesc;
    public String brand;
    public String model;
    public String engineSize;
    public String year;
    public String power;
    public String tool;
    public String status;
    public boolean saleAble;
    public String firstUploadedByUserId;
    public String originalChecksum;
    public String gearType;
    public String useCreditAccount;
    public String comment;
    public String series;
    public String build;
    public boolean started;
    public String channel;

    @Override
    public int compareTo(SedoxProduct o) {
        if (rowCreatedDate.after(o.rowCreatedDate)) {
            return -1;
        } else if (rowCreatedDate.before(o.rowCreatedDate)) {
            return 1;
        } else {
            return 0;
        }  
    }
}
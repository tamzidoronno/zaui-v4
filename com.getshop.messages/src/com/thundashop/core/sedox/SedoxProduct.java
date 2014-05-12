/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxProduct implements Comparable<SedoxProduct> {
    public int id;
    public List<SedoxBinaryFile> binaryFiles = new ArrayList();
    public String filedesc;
//    public Date dateCreated;
    public String userBrand;
    public String userModel;
    public String userCharacteristic;
    public String userYear;
    public String userPower;
    public String userTool;
    public String status;
    public boolean saleAble;
    public int userId;
    public String originalChecksum;
    public String gearType;
    public String useCreditAccount;
    public String comment;
    public String userSeries;
    public String userBuild;
    public boolean started;
    public String channel;

    @Override
    public int compareTo(SedoxProduct o) {
        return 1;
    }
}
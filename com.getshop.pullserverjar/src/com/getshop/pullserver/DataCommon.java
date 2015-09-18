/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.pullserver;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DataCommon {
    public String id = "";
    public String storeId = "";
    public Date deleted = null;
    public String className = getClass().getName();
    public Date rowCreatedDate;
    public Date lastModified = null;   
}

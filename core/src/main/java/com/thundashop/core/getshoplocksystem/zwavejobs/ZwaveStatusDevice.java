/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

/**
 *
 * @author ktonder
 */
public class ZwaveStatusDevice {
    public int id;
    public Data data;
}

class Data {
    public IsFailed isFailed;
}

class IsFailed {
    int invalidateTime;
    int updateTime;
    boolean value;
}
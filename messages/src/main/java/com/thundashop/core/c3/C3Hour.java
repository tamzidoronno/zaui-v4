/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

/**
/**
 *
 * @author ktonder
 */
public class C3Hour extends ProjectCost {
    public String costType = "hour";
    public double hours;
    public String rateId = "";
    public int rate = 0;
    
    public int fixedSumToUse = 0;
    
    public boolean fixedSum = false;
    
    @Transient
    public double cost;
    boolean nfr;
}

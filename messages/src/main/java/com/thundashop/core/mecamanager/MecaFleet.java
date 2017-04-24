/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class MecaFleet extends DataCommon {
    public String name = "";
    
    /**
     * If this flag is set to true it means that the 
     * fleet should be followed up manually.
     */
    public boolean followup = true;
    public String contactName = "";
    public String contactEmail = "";
    public String contactPhone = "";
    
    public String discount = "";
    public String rentalcar = "";
    public String contactDetails = "";
    public String contactOther = "";
    public String pageId = "";
    public List<String> cars = new ArrayList();
}

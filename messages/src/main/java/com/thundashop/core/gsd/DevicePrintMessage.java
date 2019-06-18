/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.pdf.data.AccountingDetails;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DevicePrintMessage extends GetShopDeviceMessage implements Serializable {    
    public String payLoad = "";
    public double totalExVat = 0.0;
    public double totalIncVat = 0.0;
    public List<ItemLine> itemLines = new ArrayList();
    public List<VatLine> vatLines = new ArrayList();
    public AccountingDetails accountDetails;
    public String paymentMethod;
    public Date paymentDate;
    public String printerType = "";
}
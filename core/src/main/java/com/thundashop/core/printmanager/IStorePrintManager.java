/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IStorePrintManager {
    
    @Administrator
    public void savePrinter(Printer printer);
    
    public List<Printer> getPrinters();
    
    @Administrator
    public void deletePrinter(String id);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class StorePrintManager extends ManagerBase implements IStorePrintManager {

    public HashMap<String, Printer> printers = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon i : data.data) {
            if (i instanceof Printer) {
                printers.put(i.id, (Printer)i);
            }
        }
    }

    @Override
    public void savePrinter(Printer printer) {
        saveObject(printer);
        printers.put(printer.id, printer);
    }

    @Override
    public List<Printer> getPrinters() {
        return new ArrayList(printers.values());
    }

    @Override
    public void deletePrinter(String id) {
        Printer printer = printers.remove(id);
        if (printer != null)
            deleteObject(printer);  
    }

    public Printer getPrinter(String printerId) {
        return printers.get(printerId);
    }
    
}

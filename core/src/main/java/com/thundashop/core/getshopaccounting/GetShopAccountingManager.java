/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GetShopAccountingManager extends ManagerBase implements IGetShopAccountingManager {
    
    private Configuration config = new Configuration();
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private final List<AccountingSystemBase> accountingSystems = new ArrayList();
    
    @Override
    public List<String> createNextOrderFile(Date endDate) {
        List<String> ret = new ArrayList();
        
        List<Order> orders = orderManager.getOrdersToTransferToAccount(endDate);
        
        List<String> others = new ArrayList();
        List<String> invoices = new ArrayList();
        if (getActivatedAccountingSystemOther().getSystemType().equals(SystemType.GENERELL_NORWEGIAN)) {
            orders = orderManager.getAllOrderNotTransferredToAccounting();
            others = getActivatedAccountingSystemOther().createNextOrderFile(endDate, null, orders);            
        } else {
            others = getActivatedAccountingSystemInvoices().createNextOrderFile(endDate, "invoice", orders);
            invoices = getActivatedAccountingSystemOther().createNextOrderFile(endDate, "other", orders);            
        }
        
        if (getActivatedAccountingSystemInvoices().equals(getActivatedAccountingSystemOther())) {
            if(invoices != null) {
                ret.addAll(invoices);
            }
        } else {
            if(others != null) {
                ret.addAll(others);
            }
            if(invoices != null) {
                ret.addAll(invoices);
            }
        }
        
        return ret;
    }
    
    @Override
    public List<SavedOrderFile> getOrderFiles() {
        List<SavedOrderFile> files = new ArrayList();
        
        if (getActivatedAccountingSystemInvoices().equals(getActivatedAccountingSystemOther())) {
            files.addAll(getActivatedAccountingSystemInvoices().getOrderFiles());
        } else {
            files.addAll(getActivatedAccountingSystemInvoices().getOrderFiles());
            files.addAll(getActivatedAccountingSystemOther().getOrderFiles());
        }
        
        return files;
    }
    
    @Override
    public SavedOrderFile getOrderFile(String fileId) {
        SavedOrderFile file = getActivatedAccountingSystemInvoices().getOrderFile(fileId);
        if (file == null) {
            file = getActivatedAccountingSystemOther().getOrderFile(fileId);
        }
        
        return file;
    }
    
    @Override
    public Date getPreviouseEndDate() {
        Date date = getActivatedAccountingSystemInvoices().getPreviouseEndDate();
        Date otherdate = getActivatedAccountingSystemOther().getPreviouseEndDate();
        
        if (date.equals(otherdate)) {
            return date;
        }

        if (date == null) {
            return otherdate;
        }
        
        if (otherdate == null) {
            return date;
        }
        
        if (date.after(otherdate)) {
            return date;
        }
        
        if (otherdate.after(date)) {
            return otherdate;
        }
        
        return null;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(datacommon -> {
            if (datacommon instanceof Configuration) {
                this.config = (Configuration)datacommon;
            }     
        });
    }
    
    public Configuration getGlobalConfiguration() {
        return config;
    }
    
    private AccountingSystemBase getActivatedAccountingSystemInvoices() {
        AccountingSystemBase system = getSystem(config.activatedSystemTypeInvoices);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        return system;
    }
    
    private AccountingSystemBase getActivatedAccountingSystemOther() {
        AccountingSystemBase system = getSystem(config.activatedSystemTypeOther);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        return system;
    }

    private AccountingSystemBase getSystem(SystemType type) {
        AccountingSystemBase system = accountingSystems
                .stream()
                .filter(o -> o.getSystemType().equals(type))
                .findAny()
                .orElse(null);
        return system;
    }

    public void transferOldFiles(Collection<SavedOrderFile> savedFiles) {
        savedFiles.stream()
                .forEach(file -> {
                    getActivatedAccountingSystemInvoices().transferOldOrderFile(file);
                });
        
        getActivatedAccountingSystemInvoices().markTransferred();
    }

    @Override
    public List<String> getOrdersToIncludeForNextTransfer(Date endDate) {
        return getActivatedAccountingSystemInvoices().getOrdersToIncludeForNextTransfer(endDate);
    }

    @Override
    public void deleteFile(String fileId, String password) {
        if (password.equals("asdjfa094u51jn12on51o2n35123nasdfasdf")) {
            getActivatedAccountingSystemInvoices().deleteFile(fileId);
            getActivatedAccountingSystemOther().deleteFile(fileId);
        }
    }

    @Override
    public List<String> getLogEntries() {
        List<String> arr = new ArrayList();
        arr.addAll(getActivatedAccountingSystemInvoices().getLogEntries());
        arr.addAll(getActivatedAccountingSystemOther().getLogEntries());
        return arr;
    }

    @Override
    public HashMap<String, String> getListOfSystems() {
        HashMap<String, String>  systems = new HashMap();
        
        accountingSystems.stream().forEach(system -> {
            systems.put(system.getSystemType().toString(), system.getSystemName());
        });
        
        return systems;
    }

    @Override
    public String getCurrentSystemInvoices() {
        if (this.config.activatedSystemTypeInvoices == null) {
            return null;
        }
        
        return this.config.activatedSystemTypeInvoices.toString();
    }
    
    @Override
    public String getCurrentSystemOther() {
        if (this.config.activatedSystemTypeOther == null) {
            return null;
        }
        
        return this.config.activatedSystemTypeOther.toString();
    }

    @Override
    public void setSystemTypeInvoice(String systemType) {
        this.config.activatedSystemTypeInvoices = SystemType.valueOf(systemType);
        saveObject(this.config);
    }

    @Override
    public void setSystemTypeOther(String systemType) {
        this.config.activatedSystemTypeOther = SystemType.valueOf(systemType);
        saveObject(this.config);
    }

    @Override
    public void setConfig(String systemType, String key, String value) {
        SystemType type = SystemType.valueOf(systemType);
        AccountingSystemBase system = getSystem(type);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        system.setConfig(key, value);
    }

    @Override
    public HashMap<String, String> getConfigs(String systemType) {
        SystemType type = SystemType.valueOf(systemType);
        AccountingSystemBase system = getSystem(type);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        return system.getConfigs();
    }

    @Override
    public HashMap<String, String> getConfigOptions(String systemType) {
        SystemType type = SystemType.valueOf(systemType);
        AccountingSystemBase system = getSystem(type);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        return system.getConfigOptions();
    }

    @Override
    public boolean canOrderBeTransferredDirect(String orderId) {
        Order order = orderManager.getOrder(orderId);
        String subType = getActivatedAccountingSystemInvoices().getSubType(order.getPaymentApplicationId());
        
        if (subType.equals("other")) {
            return getActivatedAccountingSystemOther().supportDirectTransfer();
        } else {
            return getActivatedAccountingSystemInvoices().supportDirectTransfer();
        }
    }

    @Override
    public List<String> transferDirect(String orderId) {
        Order order = orderManager.getOrder(orderId);
        
        getActivatedAccountingSystemOther().clearLog();
        getActivatedAccountingSystemInvoices().clearLog();
        
        if (order != null) {
            String subType = getActivatedAccountingSystemInvoices().getSubType(order.getPaymentApplicationId());

            if (subType.equals("other")) {
                getActivatedAccountingSystemOther().directTransfer(orderId);
                return getActivatedAccountingSystemOther().getLogEntries();
            } else {
                getActivatedAccountingSystemInvoices().directTransfer(orderId);
                return getActivatedAccountingSystemInvoices().getLogEntries();
            }
        }
        
        return new ArrayList();
    }

    @Override
    public String createBankTransferFile() {
        AccountingSystemBase res = getActivatedAccountingSystemInvoices();
        return res.createBankTransferFile();
    }

    @Override
    public boolean isCurrentSelectedAccountingSystemPrimitive() {
        return getActivatedAccountingSystemOther().isPrimitive();
    }

    @Override
    public boolean isCurrentSelectedSupportingDirectTransfer() {
        return getActivatedAccountingSystemOther().supportDirectTransfer();
    }

    @Override
    public void transferData(Date start, Date end) {
        List<DayIncome> incomes = orderManager.getDayIncomes(start, end);
        
        for (DayIncome income : incomes) {
            if (!income.isFinal) {
                throw new RuntimeException("Can not transfer to accountin a dayincome that is nor marked as final!");
            }
        }
        
        getActivatedAccountingSystemOther().transfer(incomes);
    }

}

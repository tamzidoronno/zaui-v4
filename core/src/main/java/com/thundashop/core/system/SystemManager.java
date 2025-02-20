/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.system;

import com.getshop.javaapi.GetShopApi;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.director.DailyUsage;
import com.thundashop.core.director.DirectorManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@GetShopSession
@Component
public class SystemManager extends ManagerBase implements ISystemManager {
    public HashMap<String, GetShopSystem> systems = new HashMap();
    public HashMap<String, DailyUsage> usages = new HashMap();

    @Autowired
    UserManager userManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon o : data.data) {
            if (o instanceof GetShopSystem) {
                systems.put(o.id, (GetShopSystem)o);
            }
            if (o instanceof DailyUsage) {
                usages.put(o.id, (DailyUsage)o);
            }
        }
    }

    @Override
    public GetShopSystem createSystem(String systemName, String companyId) {
        GetShopSystem system = new GetShopSystem();
        system.systemName = systemName;
        system.companyId = companyId;
        saveObject(system);
        systems.put(system.id, system);
        return system;
    }

    @Override
    public void deleteSystem(String systemId) {
        GetShopSystem system = systems.remove(systemId);
        if (system != null) {
            deleteObject(system);
        }
    }

    @Override
    public GetShopSystem getSystem(String systemId) {
        return systems.get(systemId);
    }

    
    
    @Override
    public void saveSystem(GetShopSystem system) {
        saveObject(system);
        systems.put(system.id, system);
    }

    @Override
    public List<GetShopSystem> getSystemsForCompany(String companyId) {
        return systems.values()
                .stream()
                .filter(o -> o.companyId != null && o.companyId.equals(companyId))
                .collect(Collectors.toList());
    }
    
    @Override
    public void syncSystem(String systemId) throws Exception {
        GetShopSystem system = getSystem(systemId);
        Date yesterday =  getYesterDay();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        if (system != null) {
            GetShopApi api = getApiForSystem(system);
            Date storeCreatedDate = api.getDirectorManager().getCreatedDate(DirectorManager.password);
            cal.setTime(storeCreatedDate);
            
            while (cal.getTime().before(yesterday)) {
                Date timeToGet = cal.getTime();
                DailyUsage savedUsageForDay = getSavedUsageForDay(system, timeToGet);
                
                if (savedUsageForDay == null) {
                    DailyUsage usage = api.getDirectorManager().getDailyUsage(DirectorManager.password, cal.getTime());
                    usage.systemId = system.id;
                    saveObject(usage);
                    usages.put(usage.id, usage);
                }
                
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            api.transport.close();
        }
    }

    private GetShopApi getApiForSystem(GetShopSystem system) {
        String sessionId = UUID.randomUUID().toString();
        GetShopApi api;
        try {
            api = new GetShopApi(25554, system.serverVpnIpAddress, sessionId, system.storeId);
            api.getStoreManager().initializeStore(system.webAddresses, sessionId);
            return api;
        } catch (Exception ex) {
            Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new RuntimeException("Could not initialize");
    }

    private DailyUsage getSavedUsageForDay(GetShopSystem system, Date timeToGet) {
        return usages.values()
                .stream()
                .filter(o -> o.systemId.equals(system.id))
                .filter(o -> o.isOnDay(timeToGet))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<DailyUsage> getDailyUsage(String systemId) {
        return usages.values()
                .stream()
                .filter(o -> o.systemId.equals(systemId))
                .collect(Collectors.toList());
    }

    private Date getYesterDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public void markUsageAsBilled(DailyUsage usage) {
        usage.markAsInvoiced();
        saveObject(usage);
        usages.put(usage.id, usage);
    }

    @Override
    public List<UnpaidInvoices> getUnpaidInvoicesForStore(String storeId) {
        List<Order> orders = orderManager.getAllOrders();
        List<UnpaidInvoices> result = new ArrayList();
        for(GetShopSystem sys : systems.values()) {
            if(sys.remoteStoreId.equals(storeId)) {
                String companyId = sys.companyId;
                User usr = userManager.getMainCompanyUser(companyId);
                for(Order order : orders) {
                    if(!order.userId.equals(usr.id)) {
                        continue;
                    }
                    if(order.isFullyPaid()) {
                       continue; 
                    }
                    UnpaidInvoices toAdd = new UnpaidInvoices();
                    toAdd.amount = order.getTotalAmount();
                    toAdd.paidRest = order.getPaidRest();
                    int days = Days.daysBetween(new LocalDate(order.getDueDate()), new LocalDate()).getDays();
                    toAdd.daysDue = days;
                    toAdd.incrementOrderId = order.incrementOrderId;
                    if(toAdd.daysDue > 4 && toAdd.paidRest > 0) {
                        result.add(toAdd);
                    }
                }
            }
        }
        return result;
    }
    
    @Override
    public String getCustomerIdForStoreId(String storeId) {
        GetShopSystem system = systems.values()
                .stream()
                .filter(o -> o.remoteStoreId != null && o.remoteStoreId.equals(storeId))
                .distinct()
                .findFirst()
                .orElse(null);
        
        if (system != null) {
            User user = userManager.getMainCompanyUser(system.companyId);
            if (user != null)
                return user.id;
        }
        
        return "";
    }

    private void printAllSystems() {
        for(GetShopSystem sys : systems.values()) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


            System.out.println(simpleDateFormat.format(sys.activeFrom) + ":" + sys.systemName + ":" + sys.webAddresses);
        }
    }

    @Override
    public List<GetShopSystem> getSystemsForStore(String storeId) {
        GetShopSystem retSystem = systems.values()
                .stream()
                .filter(o -> o.remoteStoreId != null && o.remoteStoreId.equals(storeId))
                .findAny()
                .orElse(null);
        
        if (retSystem == null || retSystem.companyId == null || retSystem.companyId.isEmpty()) {
            return new ArrayList();
        }
        
        return systems.values()
                .stream()
                .filter(o -> o.companyId != null && o.companyId.equals(retSystem.companyId))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetShopSystem> findSystem(String keyword) {
        List<Company> companies = userManager.getAllCompanies();
        List<GetShopSystem> res = new ArrayList();
        for(Company com : companies) {
            if(com == null || com.name == null) {
                continue;
            }
            if(com.name.toLowerCase().contains(keyword.toLowerCase())) {
                List<GetShopSystem> systemstoadd = getSystemsForCompany(com.id);
                res.addAll(systemstoadd);
            }
        }
        
        return res;
    }

    @Override
    public void moveSystemToCustomer(String systemId, String userId) {
        GetShopSystem system = getSystem(systemId);
        User user = userManager.getUserById(userId);
        if (user != null && user.companyObject != null) {
            system.companyId = user.companyObject.id;
            saveObject(system);
        }
    }

    @Override
    public List<GetShopSystem> getAllSystems() {
        ArrayList<GetShopSystem> allsystems = new ArrayList(systems.values());
        
        Collections.sort(allsystems);
        
        return allsystems;
    }
        
}

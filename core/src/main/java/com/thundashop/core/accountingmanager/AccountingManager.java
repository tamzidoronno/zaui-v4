/*
 * Transfer orders to xledger.
 */
package com.thundashop.core.accountingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class AccountingManager extends ManagerBase implements IAccountingManager {

    public HashMap<String, SavedOrderFile> files = new HashMap();
    
    @Autowired
    OrderManager orderManager;

    @Autowired
    UserManager userManager;
    
    private List<AccountingInterface> interfaces = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof SavedOrderFile) {
                files.put(((SavedOrderFile) obj).id, (SavedOrderFile) obj);
            }
        }
    }
    
    @Override
    public List<String> createUserFile(boolean newOnly) throws Exception {
        getInterfaceForStore();
        
        for(AccountingInterface iface : interfaces) {
            List<User> users = userManager.getAllUsers();
            List<User> toFetch = new ArrayList();
            if(newOnly) {
                for(User user : users) {
                    if(user.isTransferredToAccountSystem) {
                        continue;
                    }
                    toFetch.add(user);
                }
            } else {
                toFetch.addAll(users);
            }
            
            List<String> result = iface.createUserFile(toFetch);
            
            for(User usr : toFetch) {
                usr.isTransferredToAccountSystem = true;
                userManager.saveUser(usr);
            }
            
            return result;
        }
        return new ArrayList();
    }
    
    @Override
    public List<String> createOrderFile() throws Exception {
        getInterfaceForStore();

        for(AccountingInterface iface : interfaces) {
            List<Order> orders = new ArrayList();
            for(Order order : orderManager.getOrders(null, null, null)) {
                if(order.status != Order.Status.SEND_TO_INVOICE) {
                    continue;
                }
                if(!order.transferredToAccountingSystem) {
                    orders.add(order);
                }
            }
            if(!orders.isEmpty()) {
                List<String> result = iface.createOrderFile(orders);
                saveFile(result);
                for(Order ord : orders) {
                    ord.transferredToAccountingSystem = true;
                    orderManager.saveOrder(ord);
                }
                return result;
            }
        }
        return new ArrayList();
    }

    private void saveFile(List<String> result) {
        SavedOrderFile file = new SavedOrderFile();
        file.result = result;
        saveObject(file);
        files.put(file.id, file);
    }

    private void getInterfaceForStore() throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForStore.class));
        interfaces.clear();
        for (BeanDefinition bd : scanner.findCandidateComponents("com.thundashop.core.accountingmanager")) {
            Class myClass = Class.forName(bd.getBeanClassName());
            ForStore res = (ForStore) myClass.getAnnotation(ForStore.class);
            if(res != null && res.storeId().equals(storeId)) {
                Constructor<?> ctor = myClass.getConstructor();
                AccountingInterface object = (AccountingInterface) ctor.newInstance();
                object.setUserManager(userManager);
                interfaces.add(object);
            }
        }
    }

    @Override
    public HashMap<String, String> getAllFiles() {
        HashMap<String, String> result = new HashMap();
        for(String res : files.keySet()) {
            SavedOrderFile obj = files.get(res);
            result.put(res, obj.rowCreatedDate.toString());
        }
        return result;
    }
    
    @Override
    public List<String> getFile(String id) {
        return files.get(id).result;
    }

}

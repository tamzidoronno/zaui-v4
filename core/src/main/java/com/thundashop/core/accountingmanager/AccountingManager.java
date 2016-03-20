/*
 * Transfer orders to xledger.
 */
package com.thundashop.core.accountingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.CheckSendQuestBackScheduler;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @Autowired
    FtpManager ftpManager;
    
    private List<AccountingInterface> interfaces = new ArrayList();
    private AccountingManagerConfig config = new AccountingManagerConfig();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof AccountingManagerConfig) {
                this.config = (AccountingManagerConfig) obj;
            }
            if(obj instanceof SavedOrderFile) {
                files.put(((SavedOrderFile) obj).id, (SavedOrderFile) obj);
            }
        }
        
        createScheduler("autotransferfiles", "0 0 * * *", AutoTransferFiles.class);
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
    public List<String> createCombinedOrderFile(boolean newUsersOnly) throws Exception {
        getInterfaceForStore();
        List<String> users = createUserFile(newUsersOnly);
        List<String> orders = createOrderFile(false);
        users.addAll(orders);
        saveFile(users);
        return users;
    }
    
    @Override
    public List<String> createOrderFile() throws Exception {
        return createOrderFile(true);
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

    @Override
    public void setAccountingManagerConfig(AccountingManagerConfig config) {
        saveObject(config);
        this.config = config;
    }

    @Override
    public AccountingManagerConfig getAccountingManagerConfig() {
        return this.config;
    }

    @Override
    public List<SavedOrderFile> getAllFilesNotTransferredToAccounting() {
        List<SavedOrderFile> res = new ArrayList();
        for(SavedOrderFile file :files.values()) {
            if(!file.transferred) {
                res.add(file);
            }
        }
        return res;
    }

    @Override
    public void markAsTransferredToAccounting(String id) {
        SavedOrderFile file = files.get(id);
        file.transferred = true;
        saveObject(file);
    }

    @Override
    public void transferFilesToAccounting() {
        //First create a file if needed.
        if(!hasOrdersToTransfer()) {
            return;
        }
        try {
            createCombinedOrderFile(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<SavedOrderFile> filesToTransfer = getAllFilesNotTransferredToAccounting();
        for(SavedOrderFile saved : filesToTransfer) {
            String path = saveFileToDisk(saved);
            try {
                boolean transferred = ftpManager.transferFile(config.username, config.password, config.hostname, path, config.path, 21);
                if(transferred) {
                    saved.transferred = true;
                    saveObject(saved);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String saveFileToDisk(SavedOrderFile saved) {
        try {

            String filename = "orders_" + new SimpleDateFormat("yyyyMMdd-k_m").format(saved.rowCreatedDate) + config.extension;

            File file = new File("/tmp/"+filename);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for(String line : saved.result) {
                bw.write(line);
            }
            bw.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return null;
    }

    private boolean hasOrdersToTransfer() {
        for(Order order : orderManager.getOrders(null, null, null)) {
            if(!config.statesToInclude.contains(order.status)) {
                continue;
            }
            if(!order.transferredToAccountingSystem) {
                return true;
            }
        }
        return false;
    }

    private List<String> createOrderFile(boolean save) throws Exception {
        getInterfaceForStore();

        for(AccountingInterface iface : interfaces) {
            List<Order> orders = new ArrayList();
            for(Order order : orderManager.getOrders(null, null, null)) {
                if(!config.statesToInclude.contains(order.status)) {
                    continue;
                }
                if(!order.transferredToAccountingSystem) {
                    orders.add(order);
                }
            }
            if(!orders.isEmpty()) {
                List<String> result = iface.createOrderFile(orders);
                if(save) {
                    saveFile(result);
                }
                for(Order ord : orders) {
                    ord.transferredToAccountingSystem = true;
                    orderManager.saveOrder(ord);
                }
                return result;
            }
        }
        return new ArrayList();    
    }

}

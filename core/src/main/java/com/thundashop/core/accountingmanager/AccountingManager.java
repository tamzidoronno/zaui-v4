/*
 * Transfer orders to accounting and creditors.
 */
package com.thundashop.core.accountingmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.svea.AddressType;
import com.getshop.svea.CustomerData;
import com.getshop.svea.CustomerData.Creditor;
import com.getshop.svea.CustomerData.Creditor.Cases;
import com.getshop.svea.CustomerData.Creditor.Cases.Case;
import com.getshop.svea.CustomerData.Creditor.Cases.Case.Debtor;
import com.getshop.svea.CustomerData.Creditor.Cases.Case.Invoice;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class AccountingManager extends ManagerBase implements IAccountingManager {

    public HashMap<String, SavedOrderFile> files = new HashMap();
    public HashMap<String, SavedOrderFile> creditorFiles = new HashMap();
    
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
                SavedOrderFile file = (SavedOrderFile) obj;
                if(file.type.equals("accounting")) {
                    files.put(((SavedOrderFile) obj).id, file);
                } else {
                    creditorFiles.put(((SavedOrderFile) obj).id, file);
                }
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
                    if(user.isAdministrator() || user.isEditor()) {
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

    @Override
    public List<String> createCreditorFile(boolean newOnly) throws Exception {
        if(!config.vendor.equals("svea")) {
            System.out.println("nothing but svea is supported yet...");
            return new ArrayList();
        }
        
        if(config.creditor_username == null || config.creditor_username.isEmpty()) {
            return new ArrayList();
        }
        
        List<Order> allOrders = orderManager.getOrders(null, null, null);
        List<String> result = new ArrayList();
        for(Order order : allOrders) {
            if(order.needToBeTranferredToCreditor()) {
                if(config.vendor.equals("svea")) {
                    String res = createSveaCreditorFile(order);
                    result.add(res);
                    order.transferredToCreditor = new Date();
                    System.out.println(res);
                    orderManager.saveOrder(order);
                }
            }
        }
        
        if(!result.isEmpty()) {
            SavedOrderFile file = new SavedOrderFile();
            file.type = "creditor";
            file.result = result;
            saveObject(file);
        }
        
        return result;
    }

    @Override
    public void transferFilesToCreditor() {
        try {
            createCreditorFile(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<SavedOrderFile> filesToTransfer = getCreditorFilesNotTransferredToAccounting();
        for(SavedOrderFile saved : filesToTransfer) {
            String path = saveFileToDisk(saved);
            try {
                boolean transferred = ftpManager.transferFile(config.creditor_username, 
                        config.creditor_password, 
                        config.creditor_hostname, 
                        path, 
                        config.creditor_path, 21);
                if(transferred) {
                    saved.transferred = true;
                    saveObject(saved);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }    
    }

    private List<SavedOrderFile> getCreditorFilesNotTransferredToAccounting() {
        List<SavedOrderFile> res = new ArrayList();
        for(SavedOrderFile file : creditorFiles.values()) {
            if(!file.transferred) {
                res.add(file);
            }
        }
        return res;
    }

    private String createSveaCreditorFile(Order order) {
        User user = userManager.getUserById(order.userId);
        CustomerData customer = new CustomerData();
        
        Cases cases = createCases(user, order);
                
        Creditor creditor = new Creditor();
        creditor.setCaseFromCreditorWeb(false);
        creditor.setCases(cases);
        
        customer.setCreditor(creditor);
        String res = "";
        try {
            File file = SveaXML.createXmlFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(CustomerData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(customer, file);
            jaxbMarshaller.marshal(customer, sw);
            String result = sw.toString();
            return result;
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return res;
    }

    private Cases createCases(User user, Order order) {
        Cases cases = new Cases();
        
        Case theCase = new Case();
        Invoice inv = new Invoice();
        inv.setCid(user.customerId + "");
        inv.setCurrencyCode("NOK");
        inv.setClaimTypeInvoice(1 + "");
        inv.setInvoiceNumber(order.incrementOrderId + "");
        
        double total = orderManager.getTotalAmount(order);
        inv.setInvoiceAmount(new BigDecimal(total));
        inv.setInvoiceRemainingAmount(new BigDecimal(total));

        List<Invoice> invoices = new ArrayList();
        invoices.add(inv);
        
        Debtor dep = createDeptor(user, order);
        theCase.setDebtor(dep);
        theCase.setInvoice(invoices);
        
        dep.setName(user.fullName);
        
        List<Case> allCases = new ArrayList();
        allCases.add(theCase);
        
        cases.setCases(allCases);
        
        return cases;
    }

    private Debtor createDeptor(User user, Order order) {
        Debtor deptor = new Debtor();
        deptor.setName(user.fullName);
        BigInteger bigInt = new BigInteger(user.customerId.toString());
        deptor.setCustomerNumber(bigInt);
        
        AddressType address = new AddressType();
        address.setAddress1(user.address.address);
        address.setAddress2(user.address.address2);
        address.setPostalPlace(user.address.city);
        address.setPostalNumber(user.address.postCode);
        deptor.setAddress(address);
        
        if(user.getBirthDate() != null) {
            try {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(user.getBirthDate());
                XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                deptor.setBirthDate(date2);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        Debtor.CellularPhoneNumber cellNumber = new Debtor.CellularPhoneNumber();
        cellNumber.setValue(user.cellPhone);
        cellNumber.setIsSMSVerified(false);
        
        deptor.setCellularPhoneNumber(cellNumber);
        deptor.setEmail(user.emailAddress);
        deptor.setIsFirm(user.company.size() > 0);
        if(user.companyObject != null) {
            Company comp = user.companyObject;
            deptor.setOrgNumberSocialnumber(comp.vatNumber + "");
            deptor.setIsFirm(true);
        } else {
            deptor.setIsFirm(false);
        }
        return deptor;
    }
}

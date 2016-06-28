/*
 * Transfer orders to accounting and creditors.
 */
package com.thundashop.core.accountingmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.getshop.svea.AddressType;
import com.getshop.svea.CustomerData;
import com.getshop.svea.CustomerData.Creditor;
import com.getshop.svea.CustomerData.Creditor.Cases;
import com.getshop.svea.CustomerData.Creditor.Cases.Case;
import com.getshop.svea.CustomerData.Creditor.Cases.Case.Debtor;
import com.getshop.svea.CustomerData.Creditor.Cases.Case.Invoice;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.thundashop.core.accountingmanager.TransferFtpConfig;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsManager;
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
    public HashMap<String, SavedOrderFile> otherFiles = new HashMap();
    
    @Autowired
    OrderManager orderManager;

    @Autowired
    UserManager userManager;
    
    @Autowired
    FtpManager ftpManager;
    
    @Autowired
    GetShopSessionScope getShopSessionScope;
    
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
                if(file.type.equals(getAccountingType())) {
                    files.put(((SavedOrderFile) obj).id, file);
                } else if(file.type.equals(getSveaCreditorType())) {
                    otherFiles.put(((SavedOrderFile) obj).id, file);
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
        saveFile(users, getAccountingType());
        return users;
    }
    
    @Override
    public List<String> createOrderFile() throws Exception {
        return createOrderFile(true);
    }

    private void saveFile(List<String> result, String type) {
        if(result == null || result.isEmpty()) {
            return;
        }
        
        SavedOrderFile file = new SavedOrderFile();
        file.result = result;
        file.type = type;
        saveObject(file);
        if(type.equals(getAccountingType())) {
            files.put(file.id, file);
        } else {
            otherFiles.put(file.id, file);
        }
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
    public List<SavedOrderFile> getAllFiles() {
        List<SavedOrderFile> result = new ArrayList();
        result.addAll(files.values());
        result.addAll(otherFiles.values());
        return result;
    }
    
    @Override
    public List<String> getFile(String id) {
        SavedOrderFile file = files.get(id);
        
        if(file == null) {
            file= otherFiles.get(id);
        }
        return file.result;
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
                boolean transferred = ftpManager.transferFile(config.username, config.password, config.hostname, path, config.path, config.port, config.useActiveMode);
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
                    saveFile(result, getAccountingType());
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
        
        if(config.username == null || config.username.isEmpty()) {
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
            saveFile(result, getSveaCreditorType());
        }
        
        return result;
    }

    private List<SavedOrderFile> getFilesNotTransferredYet(String type) {
        List<SavedOrderFile> res = new ArrayList();
        for(SavedOrderFile file : getAllFiles()) {
            if(!file.transferred && file.type.equals(type)) {
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

    @Override
    public void transferFiles(String type) throws Exception {
        if(type.equals(getSveaCreditorType())) {
            createCreditorFile(true);
        } else if(type.equals(getBookingComRateManagerType())) {
            createBookingComRateManagerFile();
        } else {
            System.out.println("Transferring of type... is not supported yet : " + type);
            return;
        }

        List<SavedOrderFile> filesToTransfer = getFilesNotTransferredYet(type);
        for(SavedOrderFile saved : filesToTransfer) {
            String path = saveFileToDisk(saved);
            try {
                TransferFtpConfig ftpconfig = getTransferConfig(type);
                
                boolean transferred = ftpManager.transferFile(ftpconfig.username, 
                        ftpconfig.password, 
                        ftpconfig.hostname, 
                        path, 
                        ftpconfig.path, 
                        ftpconfig.port, 
                        ftpconfig.useActiveMode);
                if(transferred) {
                    saved.transferred = true;
                    saveObject(saved);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public List<String> getNewFile(String type) throws Exception {
        if(type.equals(getSveaCreditorType())) {
            return createCreditorFile(true);            
        } else if(type.equals(getBookingComRateManagerType())) {
            return createBookingComRateManagerFile();
        } else {
            System.out.println("Cant get file of type: " + type);
        }
        return new ArrayList();
    }

    private List<String> createBookingComRateManagerFile() {
        List<String> files = new ArrayList();
        PmsManager manager = getPmsManager();
        BookingEngineAbstract engine = getBookingEngine();
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.onlyUntransferredToBookingCom = true;
        
        List<PmsBooking> bookings = manager.getAllBookings(filter);
        BComRateManager builder = new BComRateManager(bookings, engine.getBookingItemTypes());
        
        List<String> lines = builder.generateLines();
        saveFile(lines, getBookingComRateManagerType());
        
        for(PmsBooking booking : bookings) {
            booking.transferredToRateManager = true;
            manager.saveBooking(booking);
        }
        
        return lines;
    }

    private PmsManager getPmsManager() {
        TransferFtpConfig configuration = config.getBComConfig();
        PmsManager bean = getShopSessionScope.getNamedSessionBean(configuration.engineNames, PmsManager.class);
        return bean;
    }

    private BookingEngineAbstract getBookingEngine() {
        TransferFtpConfig configuration = config.getBComConfig();
        BookingEngineAbstract beanAbs = getShopSessionScope.getNamedSessionBean(configuration.engineNames, BookingEngineAbstract.class);
        return beanAbs;
    }

    private TransferFtpConfig getTransferConfig(String type) {
        if(type.equals(getSveaCreditorType())) { return config.getCreditorConfig(); }
        if(type.equals(getBookingComRateManagerType())) { return config.getBComConfig(); }
        
        return null;
    }

    private String getAccountingType() {
        return "accounting";
    }
    
    private String getBookingComRateManagerType() {
        return "bookingcomratemanager";
    }
    
    private String getSveaCreditorType() {
        return "sveacreditor";
    }
}

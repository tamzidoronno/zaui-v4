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
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForStore;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ftpmanager.FtpManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
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
import java.io.PrintWriter;
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
    InvoiceManager invoiceManager;
    
    @Autowired
    GetShopSessionScope getShopSessionScope;
    
    private List<AccountingInterface> interfaces = new ArrayList();
    private AccountingManagerConfig config = new AccountingManagerConfig();
    
    private HashMap<String, AccountingTransferConfig> configs = new HashMap();
    
    @Override
    public List<AccountingTransferConfig> getAllConfigs() {
        return new ArrayList(configs.values());
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof AccountingManagerConfig) {
                this.config = (AccountingManagerConfig) obj;
            }
            if(obj instanceof AccountingTransferConfig) {
                configs.put(obj.id, (AccountingTransferConfig) obj);
            }
            if(obj instanceof SavedOrderFile) {
                SavedOrderFile file = (SavedOrderFile) obj;
                if(file.type.equals(getAccountingType())) {
                    files.put(((SavedOrderFile) obj).id, file);
                } else {
                    otherFiles.put(((SavedOrderFile) obj).id, file);
                }
            }
        }
    }
    
    @Override
    public List<String> createUserFile(boolean newOnly) throws Exception {
        return createUserFileByAdapter(usersToTransfer(newOnly));
    }
    
    public List<String> createUserFileByAdapter(List<User> users) throws Exception {
        getInterfaceForStore();
        
        for(AccountingInterface iface : interfaces) {
            List<String> result = iface.createUserFile(users);
            
            for(User usr : users) {
                usr.isTransferredToAccountSystem = true;
                userManager.saveUser(usr);
            }
            
            return result;
        }
        return new ArrayList();
    }

    private List<User> usersToTransfer(boolean newOnly) {
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
            return toFetch;
    }
    
    @Override
    public List<String> createCombinedOrderFile(boolean newUsersOnly) throws Exception {
        getInterfaceForStore();
        if(config.invoice_path != null && !config.invoice_path.isEmpty()) {
           return createSepareateCombinedOrderFiles();
        }
        List<String> users = createUserFile(newUsersOnly);
        List<Order> orders = getOrdersToTransfer();
        List<Integer> added = new ArrayList();
        if(config.transferAllUsersConnectedToOrders) {
            List<User> usersToAdd = new ArrayList();
            for(Order ord : orders) {
                User user = userManager.getUserById(ord.userId);
                if(!added.contains(user.customerId)) {
                    usersToAdd.add(user);
                    added.add(user.customerId);
                }
            }
            users = createUserFileByAdapter(usersToAdd);
        }
        List<String> result = createOrderFile(orders, false, "");
        users.addAll(result);
        saveFile(users, getAccountingType(), "", orders);
        return users;
    }
    
    @Override
    public List<String> createOrderFile() throws Exception {
        List<Order> orders = getOrdersToTransfer();
        return createOrderFile(orders, true, "");
    }

    private void saveFile(List<String> result, String type, String subtype, List<Order> orders) {
        if(result == null || result.isEmpty()) {
            return;
        }
        Double amountEx = 0.0;
        Double amountInc = 0.0;
        List<String> orderIds = new ArrayList();
        
        for(Order ord : orders) {
            amountEx += orderManager.getTotalAmountExTaxes(ord);
            amountInc += orderManager.getTotalAmount(ord);
            orderIds.add(ord.id);
        }
        
        SavedOrderFile file = new SavedOrderFile();
        file.amountEx = amountEx;
        file.amountInc = amountInc;
        file.orders = orderIds;
        file.result = result;
        file.type = type;
        file.subtype = subtype;
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
                object.setOrderManager(orderManager);
                object.setInvoiceManager(invoiceManager);
                object.setStoreApplication(applicationPool);
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
            String internalPath = saveFileToDisk(saved, config.extension);
            String externalPath = config.path;
            int minutesToWait = 0;
            if(saved.subtype != null && !saved.subtype.isEmpty() && saved.subtype.equals("invoice")) {
                externalPath = config.invoice_path;
            }
            if(saved.subtype != null && !saved.subtype.isEmpty() && saved.subtype.equals("ccard")) {
                minutesToWait = 5;
            }
            try {
                ftpManager.transferFile(config.username, config.password, config.hostname, internalPath, externalPath, config.port, config.useActiveMode, minutesToWait);

                saved.transferred = true;
                saveObject(saved);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String saveFileToDisk(SavedOrderFile saved, String extension) {
        try {

            String filename = "orders_" + new SimpleDateFormat("yyyyMMdd-k_m").format(saved.rowCreatedDate) + extension;
            if(saved.subtype != null && !saved.subtype.isEmpty()) {
                filename = saved.subtype + "_" + filename;
            }

            File file = new File("/tmp/"+filename);

            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "ISO-8859-1");
            for(String line : saved.result) {
                writer.print(line);
            }
            writer.close();

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

    private List<String> createOrderFile(List<Order> orders, boolean save, String type) throws Exception {
        getInterfaceForStore();

        for(AccountingInterface iface : interfaces) {
            if(!orders.isEmpty()) {
                List<String> result = iface.createOrderFile(orders, type);
                if(save) {
                    saveFile(result, getAccountingType(), "", orders);
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
            logPrint("nothing but svea is supported yet...");
            return new ArrayList();
        }
        
        if(config.username == null || config.username.isEmpty()) {
            return new ArrayList();
        }
        
        List<Order> allOrders = orderManager.getOrders(null, null, null);
        List<String> result = new ArrayList();
        List<Order> orders = new ArrayList();
        for(Order order : allOrders) {
            if(order.needToBeTranferredToCreditor()) {
                if(config.vendor.equals("svea")) {
                    String res = createSveaCreditorFile(order);
                    result.add(res);
                    order.transferredToCreditor = new Date();
                    orderManager.saveOrder(order);
                    orders.add(order);
                }
            }
        }
        
        if(!result.isEmpty()) {
            saveFile(result, getSveaCreditorType(), "", orders);
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
            logPrint("Transferring of type... is not supported yet : " + type);
            return;
        }

        List<SavedOrderFile> filesToTransfer = getFilesNotTransferredYet(type);
        for(SavedOrderFile saved : filesToTransfer) {
            TransferFtpConfig ftpconfig = getTransferConfig(type);
            String path = saveFileToDisk(saved, ftpconfig.extension);
            try {
                
                boolean transferred = ftpManager.transferFile(ftpconfig.username, 
                        ftpconfig.password, 
                        ftpconfig.hostname, 
                        path, 
                        ftpconfig.path, 
                        ftpconfig.port, 
                        ftpconfig.useActiveMode, 0);

                saved.transferred = true;
                saveObject(saved);
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
            logPrint("Cant get file of type: " + type);
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
        saveFile(lines, getBookingComRateManagerType(), "", new ArrayList());
        
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

    private List<Order> getOrdersToTransfer() {
        List<Order> orders = new ArrayList();
        for(Order order : orderManager.getOrders(null, null, null)) {
            if(!config.statesToInclude.contains(order.status)) {
                continue;
            }
            if(order.testOrder) {
                continue;
            }

            if(!order.transferredToAccountingSystem) {
                double total = orderManager.getTotalAmount(order);
                if(total == 0.0) {
                    continue;
                }
                orders.add(order);
        }
        }
        return orders;
    }

    /**
     * This is used by visma / wilhelmsen house to make sure invoices are transferred to a different directory.
     * @return
     * @throws Exception 
     */
    private List<String> createSepareateCombinedOrderFiles() throws Exception {
        List<Order> orders = getOrdersToTransfer();
        List<Order> invoiceOrders = new ArrayList();
        for(Order ord : orders) {
            if(ord.payment.paymentType.toLowerCase().contains("invoice") || ord.payment.paymentType.toLowerCase().contains("expedia")) {
                invoiceOrders.add(ord);
            }
        }
        
        List<User> users = new ArrayList();
        for(Order order : orders) {
            User usr = userManager.getUserById(order.userId);
            if(usr != null && !users.contains(usr)) {
                users.add(usr);
            }
        }
        
        orders.removeAll(invoiceOrders);
        
        List<String> usersToSave = createUserFileByAdapter(users);
        List<String> invoiceOrderList = createOrderFile(invoiceOrders, false, "invoice");
        List<String> restOrderList = createOrderFile(orders, false, "");
        usersToSave.addAll(invoiceOrderList);
        
        if(!users.isEmpty()) {
            saveFile(usersToSave, getAccountingType(), "invoice", invoiceOrders);
        }
        if(!restOrderList.isEmpty()) {
            saveFile(restOrderList, getAccountingType(), "ccard", orders);
        }
        
        return new ArrayList();
    }

    private List<Integer> getOrdersToInclude() {
        
        List<Integer> tmpOrderList = new ArrayList();
        String numbers = "108489,108455,108385,108370,108006,107730,107729,107729,107718,107714,107714,107642,107273,107144,107063,106769,106530,106494,106468,106445,106445,106443,106402,106402,106387,106386,106161,106089,105773,105528,105362,105353,105353,105174,105174,105124,104906,104824,104823,104735,104734,104729,104725,104717,104710,104708,104677,104674,104673,104624,104621,104591,104550,104547,104464,104230,104116,104097,103966,103917,103804,103803,103688,103469,103466,103466,103444,103415,103337,103333,103268,103262,103132,103084,102865,102397,102381,102357,102355,102354,102353,102336,102335,102271,102136,102090,102089,102071,102061,101978,101968,101940,101846,101846,101844,101844,101805,101787,101733,101631,101390,101280,100544,100543,100507,100453,100428,100397,100352,100343,100330,100219,100171,100144,100089,100078,100075,100036,100023,100020,100015,100010";

        List<Integer> list = new ArrayList<Integer>();
        for (String s : numbers.split(","))
            list.add(Integer.parseInt(s));
        
        return list;
    }

    @Override
    public void saveConfig(AccountingTransferConfig config) {
        saveObject(config);
        configs.put(config.id, config);
    }

    @Override
    public void removeTransferConfig(String id) {
        AccountingTransferConfig objcet = configs.get(id);
        deleteObject(objcet);
        configs.remove(id);
    }
}

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
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForAccountingSystem;
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
import com.thundashop.core.pmsmanager.PmsOrderStatistics;
import com.thundashop.core.pmsmanager.PmsOrderStatsFilter;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
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
import java.util.Calendar;
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
    ProductManager productManager;
    
    @Autowired
    GetShopSessionScope getShopSessionScope;
    
    @Autowired
    WebManager webManager;
    
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
        List<String> orderIds = new ArrayList();
        
        for(Order ord : orders) {
            orderIds.add(ord.id);
        }
        
        SavedOrderFile file = new SavedOrderFile();
        file.orders = orderIds;
        file.result = result;
        file.type = type;
        file.subtype = subtype;
        sumOrders(file);
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
                
                AccountingManagers mgr = new AccountingManagers();
                mgr.ftpManager = ftpManager;
                mgr.invoiceManager = invoiceManager;
                mgr.orderManager = orderManager;
                mgr.productManager = productManager;
                mgr.userManager = userManager;
                object.setManagers(mgr);
                
                interfaces.add(object);
            }
        }
    }

    @Override
    public List<SavedOrderFile> getAllFiles() {
        List<SavedOrderFile> result = new ArrayList();
        result.addAll(files.values());
        result.addAll(otherFiles.values());
        
        for(SavedOrderFile saved : result) {
            try {
                if(finalizeFile(saved)) {
                    saveObject(saved);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    @Override
    public List<String> getFile(String id) throws Exception {
        SavedOrderFile file = files.get(id);
        
        if(file.configId != null && !file.configId.isEmpty() && file.startDate != null && file.endDate != null) {
            file = downloadOrdeFileNewType(file.configId, file.startDate, file.endDate, file);
        }
        
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
            if(!order.isInvoice()) {
                continue;
            }
            
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


    @Override
    public SavedOrderFile downloadOrderFileNewType(String configId, Date start, Date end) throws Exception {
        return downloadOrdeFileNewType(configId, start, end, null);
    }

    private List<User> getUsersFromNewFilter(AccountingTransferConfig configToUse, List<Order> selectedOrders) {
        List<User> users = userManager.getAllUsers();
        if(configToUse.includeUsers == null || configToUse.includeUsers == 0) {
            return new ArrayList();
        }
        
        if(configToUse.includeUsers == 1) {
            return users;
        }
        
        List<User> userList = new ArrayList();
        if(configToUse.includeUsers == 2) {
            for(User user : users) {
                if(!user.isTransferredToAccountSystem) {
                    userList.add(user);
                }
            }
            return userList;
        }
        
        if(configToUse.includeUsers == 3) {
            for(Order order : selectedOrders) {
                userList.add(userManager.getUserById(order.userId));
            }
            return userList;
        }
        
        if(configToUse.includeUsers == 4) {
            List<Order> orders = orderManager.getOrders(null, null, null);
            for(Order order : orders) {
                userList.add(userManager.getUserById(order.userId));
            }
            return userList;
        }
        
        return new ArrayList();
    }

    private List<Order> getOrdersFromNewFilter(AccountingTransferConfig configToUse) {
        if(configToUse == null) {
            return new ArrayList();
        }
            
        List<Order> orders = orderManager.getOrders(null, null, null);
        List<Order> result = new ArrayList();
        List<String> ordersAdded = new ArrayList();
        for(Order order : orders) {
            if(order.testOrder) {
                continue;
            }
            if(order.payment == null || order.payment.paymentType == null || order.payment.paymentType.isEmpty()) {
                continue;
            }
            Double amount = orderManager.getTotalAmount(order);
            if(amount == 0.0) {
                continue;
            }
            
            
            for(AccountingTransferConfigTypes actype : configToUse.paymentTypes) {
                String paymentMethod = actype.paymentType;
                paymentMethod = paymentMethod.replace("-", "_");
                if(order.payment.paymentType.contains(paymentMethod)) {
                    if(order.status == actype.status || actype.status == 0) {
                        if(!ordersAdded.contains(order.id)) {
                            result.add(order);
                            ordersAdded.add(order.id);
                        }
                    }
                    if(actype.status == -1 && amount < 0) {
                        if(!ordersAdded.contains(order.id)) {
                            result.add(order);
                            ordersAdded.add(order.id);
                        }
                    }
                }
            }
        }
        return result;
    }

    private AccountingTransferInterface getAccoutingInterface(String type) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ForAccountingSystem.class));
        interfaces.clear();
        for (BeanDefinition bd : scanner.findCandidateComponents("com.thundashop.core.accountingmanager")) {
            Class myClass = Class.forName(bd.getBeanClassName());
            ForAccountingSystem res = (ForAccountingSystem) myClass.getAnnotation(ForAccountingSystem.class);
            if(res != null && res.accountingSystem().equals(type)) {
                Constructor<?> ctor = myClass.getConstructor();
                AccountingTransferInterface object = (AccountingTransferInterface) ctor.newInstance();
                AccountingManagers mgrs = new AccountingManagers();
                mgrs.userManager = userManager;
                mgrs.invoiceManager = invoiceManager;
                mgrs.orderManager = orderManager;
                mgrs.userManager = userManager;
                mgrs.productManager = productManager;
                mgrs.webManager = webManager;
                object.setManagers(mgrs);
                return object;
            }
        }
        return null;
    }

    @Override
    public AccountingTransferConfig getAccountingConfig(String configId) {
        return configs.get(configId);
    }

    private void sumOrders(SavedOrderFile res) {
        if(res.orders == null) {
            return;
        }
        Double amountEx = 0.0;
        Double amountInc = 0.0;
        Double amountExDebet = 0.0;
        Double amountIncDebet = 0.0;
        List<String> orderIds = new ArrayList();
        
        for(String order : res.orders) {
            Order ord = orderManager.getOrder(order);
            Double sumEx = orderManager.getTotalAmountExTaxes(ord);
            Double sumInc = orderManager.getTotalAmount(ord);
            amountEx += sumEx;
            amountInc += sumInc;
            
            if(sumEx < 0) {
                amountExDebet += (sumEx * -1);
            } else {
                amountExDebet += sumEx;
            }
            
            if(sumInc < 0) {
                amountIncDebet += (sumInc * -1);
            } else {
                amountIncDebet += sumInc;
            }
            orderIds.add(ord.id);
        }
        
        res.amountEx = amountEx;
        res.amountInc = amountInc;
        res.amountExDebet = amountExDebet;
        res.amountIncDebet = amountIncDebet;
        finalizeFile(res);
    }

    private List<Order> filterOrders(List<Order> orders, Date start, Date end, AccountingTransferConfig configToUse) {
        List<Order> toReturn = new ArrayList();
        String filterDateByType = configToUse.orderFilterPeriode;
        
        for(Order order : orders) {
            Date dateToUse = null;
            if(filterDateByType.equals("created")) { dateToUse = order.rowCreatedDate; }
            if(filterDateByType.equals("started")) { dateToUse = order.getStartDateByItems(); }
            if(filterDateByType.equals("ended")) { dateToUse = order.getEndDateByItems(); }
            if(filterDateByType.equals("paymentdate")) { dateToUse = order.paymentDate; }
            if(dateToUse == null) {
                continue;
            }
            
            if(dateToUse.after(start) && (dateToUse.before(end) || dateToUse.equals(end))) {
                toReturn.add(order);
            }
        }
        return toReturn;
    }

    private boolean finalizeFile(SavedOrderFile saved) {
        saved.sumAmountExOrderLines = 0.0;
        saved.sumAmountIncOrderLines = 0.0;
        boolean needSaving = false;
        for(String orderId : saved.orders) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order.cart == null) {
                continue;
            } 
            for(CartItem item : order.cart.getItems()) {
                int count = item.getCount();
                if(count < 0) {
                    count *= -1;
                }
                saved.sumAmountExOrderLines += (item.getProduct().priceExTaxes*count);
                saved.sumAmountIncOrderLines += (item.getProduct().price*count);
            }
            double total = orderManager.getTotalAmount(order);
            double totalEx = orderManager.getTotalAmount(order);
            if(!saved.amountOnOrder.containsKey(order.id)) {
                saved.amountOnOrder.put(order.id, total);
                needSaving = true;
            } else if(total != saved.amountOnOrder.get(order.id)) {
                saved.tamperedOrders.add(order.id);
                needSaving = true;
            }
            if(total < 0) { total *= -1; }
            if(totalEx < 0) { totalEx *= -1; }
            saved.sumAmountIncOrderLines += total;
            saved.sumAmountExOrderLines += totalEx;
        }
        
        
        if(saved.configId != null && !saved.configId.isEmpty()) {
            AccountingTransferConfig configToUse = configs.get(saved.configId);
            List<Order> orders = getOrdersFromNewFilter(configToUse);
            saved.numberOfOrdersNow = 0;
            if(!orders.isEmpty() && configToUse != null) {
                orders = filterOrders(orders, saved.startDate, saved.endDate, configToUse);
                saved.numberOfOrdersNow = orders.size();
            }
        }
        
        
        return needSaving;
    }

    private SavedOrderFile downloadOrdeFileNewType(String configId, Date start, Date end, SavedOrderFile fileToUse) throws Exception {
        AccountingTransferConfig configToUse = configs.get(configId);
        
        if(configToUse == null) {
            logPrint("Could not find config : " + configId);
            return null;
        }
        if(fileToUse == null) {
            List<SavedOrderFile> firstCheckFiles = getAllFiles();
            for(SavedOrderFile f : firstCheckFiles) {
                if(!configToUse.subType.equals(f.subtype)) {
                    continue;
                }
                if(f.endDate != null && f.endDate.before(start)) {
                    return null;
                }
            }
        }
        
        List<Order> orders = getOrdersFromNewFilter(configToUse);
        if(orders.isEmpty()) {
            return null;
        }
        
        orders = filterOrders(orders, start, end, configToUse);
        
        List<User> users = getUsersFromNewFilter(configToUse, orders);
        
        AccountingTransferInterface transfer = getAccoutingInterface(configToUse.transferType);
        transfer.setOrders(orders);
        transfer.setUsers(users);
        transfer.setConfig(configToUse);
        
        SavedOrderFile res = transfer.generateFile();
        if(res != null) {
            if(fileToUse != null) {
                res.id = fileToUse.id;
            }

            sumOrders(res);
            res.subtype = configToUse.subType;
            res.type = configToUse.transferType;
            res.startDate = start;
            res.endDate = end;
            res.configId = configId;
            saveObject(res);

            files.put(res.id, res);
        }
        return res;    
    }

    @Override
    public PmsOrderStatistics getStats(String configId) {
        List<Order> ordersToUse = new ArrayList();
        List<SavedOrderFile> filesToUse = getAllFiles();
        Date start = new Date();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 2);
        
        for(SavedOrderFile f : filesToUse) {
            if(configId != null && !configId.isEmpty() && !f.id.equals(configId)) {
                continue;
            }
            for(String id : f.orders) {
                Order order = orderManager.getOrder(id);
                ordersToUse.add(order); 
                if(f.startDate != null && start.after(f.startDate)) {
                    start = f.startDate;
                }
            }
        }
        
        PmsOrderStatsFilter filter = new PmsOrderStatsFilter();
        filter.displayType = "dayslept";
        filter.start = start;
        filter.end = end.getTime();
        filter.priceType = "extaxes";
        
        PmsOrderStatistics stats = new PmsOrderStatistics();
        stats.createStatistics(ordersToUse, filter);
        return stats;
    }

    @Override
    public void deleteFile(String fileId) throws Exception {
        SavedOrderFile file = getFileById(fileId);
        files.remove(fileId);
        otherFiles.remove(fileId);
        deleteObject(file);
    }

    @Override
    public SavedOrderFile getFileById(String id) throws Exception {
        SavedOrderFile file = files.get(id);
        if(file == null) {
            file = otherFiles.get(id);
        }
        return file;

    }
}

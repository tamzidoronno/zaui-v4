/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.DummySmsFactory;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MailFactoryImpl;
import com.thundashop.core.messagemanager.SMSFactory;
import com.thundashop.core.sedox.autocryptoapi.FilesMessage;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SedoxProductManager extends ManagerBase implements ISedoxProductManager, ApplicationContextAware {

    @Autowired
    private SedoxSearchEngine sedoxSearchEngine;
    private Map<String, SedoxProduct> products = new HashMap();
    private Map<String, SedoxSharedProduct> productsShared = new HashMap();
    private Map<String, SedoxUser> users = new HashMap();

    @Autowired
    public SedoxPushOver sedoxPushover;

    @Autowired
    public MailFactory mailFactory;
    private ApplicationContext context;
    private MailFactoryImpl sedoxDatabankMailAccount;
    private List<FailedEvcOrder> failedEvcOrders = new ArrayList();

    @Autowired
    public SedoxCMDEncrypter sedoxCMDEncrypter;

    @Autowired
    public SedoxMagentoIntegration sedoxMagentoIntegration;

    @Autowired
    public WebSocketServerImpl webSocketServer;
    
//    @Autowired
//    @Qualifier("SmsFactoryClickatell")
    public SMSFactory smsFactory = new DummySmsFactory();
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public EvcApi evcApi;

    @PostConstruct
    public void setupDatabankMailAccount() {
        sedoxDatabankMailAccount = context.getBean(MailFactoryImpl.class);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof SedoxSharedProduct) {
                SedoxSharedProduct sharedProduct = (SedoxSharedProduct) dataCommon;
                productsShared.put(sharedProduct.id, sharedProduct);
            }
            
            if (dataCommon instanceof SedoxProduct) {
                SedoxProduct product = (SedoxProduct) dataCommon;
                products.put(product.id, product);
            }
            
            if (dataCommon instanceof FailedEvcOrder) {
                failedEvcOrders.add((FailedEvcOrder) dataCommon);
            }
            
            if (dataCommon instanceof SedoxUser) {
                SedoxUser user = (SedoxUser) dataCommon;
                user.checkForCreatedDate();
                users.put(user.id, user);
            }
        }
        
        try {
//            resetEvcOrders();
        } catch (ErrorException ex) {
            // limited
        }
    }

    @Override
    public synchronized SedoxProductSearchPage search(SedoxSearch search) {
        User user = getSession() != null ? getSession().currentUser : null;
        SedoxProductSearchPage result = sedoxSearchEngine.getSearchResult(new ArrayList(productsShared.values()), search, user);
        result.products.parallelStream().forEach(o -> finalize(o));
        return result;
    }

    @Override
    public synchronized void sync(String option) throws ErrorException {
        if (!getStoreId().equals("608afafe-fd72-4924-aca7-9a8552bc6c81")) {
            throw new ErrorException(26);
        }

        if (option == null) {
            option = "";
        }

        if (option.equals("orders")) {
            updateOrders();
            return;
        }

        if (option.equals("allmagentousers")) {
            updateAllUsers();
            return;
        }
        
        if (option.equals("magento") || option.equals("all")) {
            updateAllUsers();
            return;
        }

        System.out.println("Sync done");
    }

    @Override
    public synchronized SedoxUser getSedoxUserAccount() throws ErrorException {
        if (getSession().currentUser == null) {
            throw new ErrorException(26);
        }

        String id = getSession().currentUser.id;
        updateUserFromMagento(id, false);
        SedoxUser user = getSedoxUserById(id);
        return user;
    }

    
    private Stream<SedoxProduct> getProductsUploadedByCurrentUser() {
        String userId = getSession().currentUser.id;
        
        return products.values().parallelStream()
                .filter(product -> product.firstUploadedByUserId != null
                    && product.firstUploadedByUserId.equals(userId)
                    && !product.duplicate);
    }
    
    @Override
    public synchronized List<SedoxProduct> getProductsFirstUploadedByCurrentUser(FilterData filterData) {
        Stream<SedoxProduct> productStream = getProductsUploadedByCurrentUser();
        

        if (filterData.filterText != null && !filterData.filterText.isEmpty()) {
            productStream = productStream.filter(getFilterProductByName(filterData));
        }
        
//        productStream.forEach(o -> finalize(o));
        
        List<SedoxProduct> pagedProducts = new ArrayList(pageIt(productStream.collect(Collectors.toList()), filterData));
        pagedProducts.stream().forEach(o -> finalize(o));
        return pagedProducts;
    }
    
    @Override
    public int getProductsFirstUploadedByCurrentUserTotalPages(FilterData filterData) {
        long count = getProductsUploadedByCurrentUser().count();
        
        return (int)Math.ceil((double)count / (double)filterData.pageSize);
    }

    @Override
    public synchronized List<SedoxUser> getAllUsersWithNegativeCreditLimit() throws ErrorException {
        List<SedoxUser> retUsers = new ArrayList();

        for (SedoxUser user : users.values()) {
            if (user.creditAccount.getBalance() < 0) {
                retUsers.add(user);
            }
        }

        return retUsers;
    }

    @Override
    public synchronized List<SedoxProduct> getProductsByDaysBack(int daysBack) throws ErrorException {
        List<SedoxProduct> retProducts = new ArrayList<>();

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        int nowDayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR) - daysBack;
        int nowYear = nowCalendar.get(Calendar.YEAR);

        for (SedoxProduct product : products.values()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(product.rowCreatedDate);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            if (dayOfYear == nowDayOfYear && nowYear == calendar.get(Calendar.YEAR) && !product.duplicate) {
                finalize(product);
                retProducts.add(product);
            }
        }

        Collections.sort(retProducts, new SortByDateSedoxProductComperable());

        return retProducts;
    }

    @Override
    public synchronized SedoxProduct getProductById(String id) throws ErrorException {
        for (SedoxProduct product : products.values()) {
            if (product.id.equals(id)) {
                finalize(product);
                return product;
            }
        }
        
        if (getSession().currentUser != null) {

            for (SedoxProduct product : products.values()) {
                if (product.sharedProductId.equals(id) && product.firstUploadedByUserId.equals(getSession().currentUser.id)) {
                    finalize(product);
                    return product;
                }
            }
            
            SedoxSharedProduct sharedProduct = productsShared.get(id);
            
            if (sharedProduct != null && sharedProduct.id.equals(id)) {
                SedoxProduct sedoxProduct = new SedoxProduct();
                sedoxProduct.id = sharedProduct.id;
                sedoxProduct.firstUploadedByUserId = "";
                sedoxProduct.storeId = storeId;
                sedoxProduct.sharedProductId = id;
                sedoxProduct.rowCreatedDate = new Date();
                sedoxProduct.isFinished = true;
                sedoxProduct.virtual = true;
                return sedoxProduct;
            }
        }

        return null;
    }

    @Override
    public synchronized SedoxProduct createSedoxProduct(SedoxSharedProduct sharedProduct, String base64EncodeString, String originalFileName, String forSlaveId, String origin, String comment, String useCredit, SedoxBinaryFileOptions options) throws ErrorException {
        if (forSlaveId != null && !forSlaveId.equals("")) {
            checkIfMasterOfSlave(forSlaveId);
        }
        
        sharedProduct.id = UUID.randomUUID().toString();
        sharedProduct.storeId = storeId;
        productsShared.put(sharedProduct.id, sharedProduct);

        SedoxProduct sedoxProduct = new SedoxProduct();
        sedoxProduct.id = getNextProductId();
        sedoxProduct.firstUploadedByUserId = forSlaveId != null && !forSlaveId.equals("") ? forSlaveId : getSession().currentUser.id;
        sedoxProduct.storeId = storeId;
        sedoxProduct.sharedProductId = sharedProduct.id;
        sedoxProduct.rowCreatedDate = new Date();
        sedoxProduct.addCreatedHistoryEntry(getSession().currentUser.id);
        sedoxProduct.useCreditAccount = useCredit;
        sedoxProduct.comment = comment;
        
        products.put(sedoxProduct.id, sedoxProduct);

        SedoxBinaryFile cmdEncryptedFile = saveCmdEncryptedFile(base64EncodeString, originalFileName);
        if (cmdEncryptedFile != null) {
            cmdEncryptedFile.options = options;
            sharedProduct.saleAble = false;
            sharedProduct.isCmdEncryptedProduct = true;
            sharedProduct.binaryFiles.add(cmdEncryptedFile);
        }

        SedoxBinaryFile originalFile = getOriginalBinaryFile(base64EncodeString, originalFileName);
        if (originalFile != null) {
            originalFile.options = options;
            sharedProduct.binaryFiles.add(originalFile);

            if (cmdEncryptedFile != null) {
                cmdEncryptedFile.cmdFileType = originalFile.cmdFileType;
                originalFile.cmdFileType = null;
            }
        }

        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);
        sharedProduct.softwareSize = (fileData.length / 1024) + " KB";
        sedoxProduct.uploadOrigin = origin;

        sedoxProduct.addDeveloperHasBeenNotifiedHistory(getSession().currentUser.id);
        saveObject(sharedProduct);
        saveObject(sedoxProduct);
        sendFileCreatedNotification(sedoxProduct);
        sendNotificationToUploadedUser(sedoxProduct);
        notifyOnSocket(sedoxProduct);

        finalize(sedoxProduct);
        return sedoxProduct;
    }

    @Override
    public synchronized SedoxSharedProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException {
        for (SedoxSharedProduct product : productsShared.values()) {
            if (!product.hasMoreThenOriginalFile()) {
                continue;
            }

            for (SedoxBinaryFile binaryFile : product.binaryFiles) {
                if (binaryFile.md5sum.equals(md5sum) && product.saleAble && product.hasMoreThenOriginalFile()) {
                    return product;
                }
            }
        }

        return null;
    }

    @Override
    public synchronized void requestSpecialFile(String productId, String comment) throws ErrorException {
        if (productId == null) {
            return;
        }

        if (getSession().currentUser == null) {
            throw new ErrorException(26);
        }

        SedoxSharedProduct sharedProduct = getSharedProductById(productId);
        
        SedoxProduct customerProduct = getCustomerProduct(sharedProduct.id);
        SedoxProduct newProduct = null;
        
        if (customerProduct == null) {
            newProduct = new SedoxProduct();
            newProduct.sharedProductId = sharedProduct.id;
            newProduct.id = getNextProductId();
            newProduct.storeId = storeId;
            newProduct.isBuiltFromSpecialRequest = true;
            newProduct.firstUploadedByUserId = getSession().currentUser.id;
            products.put(newProduct.id, newProduct);
        } else {
            newProduct = customerProduct;
            newProduct.rowCreatedDate = new Date();
        }
        
        newProduct.comment = comment;

        if (newProduct != null) {
            sendNotificationEmail("files@tuningfiles.com", newProduct, comment);
        }

        
        saveObject(newProduct);
        saveObject(sharedProduct);

        sendAirgramMessages(userManager, newProduct);
        sendNotificationToUploadedUser(newProduct);
    }

    @Override
    public synchronized void addFileToProduct(String base64EncodedFile, String fileName, String fileType, String productId) throws ErrorException {
        SedoxBinaryFile sedoxBinaryFile = getOriginalBinaryFile(base64EncodedFile, fileName);
        sedoxBinaryFile.id = getNextFileId();
        sedoxBinaryFile.fileType = fileType;
        sedoxBinaryFile.updateParametersFromFileName(fileName);

        SedoxProduct sedoxProduct = getProductById(productId);
        
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);
        sharedProduct.binaryFiles.add(sedoxBinaryFile);
        sharedProduct.setParametersBasedOnFileString(fileName);
        sedoxProduct.addFileAddedHistory(getSession().currentUser.id, fileType);
        saveObject(sharedProduct);
        saveObject(sedoxProduct);
        notifyOnSocket(sedoxProduct);
    }

    private SedoxOrder purchaseProductInternalForUser(String productId, SedoxUser user, List<Integer> files) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxOrder order = getOrder(product, files, user);

        if (order.creditAmount <= 0) {
            // Dont register if product is free.
            return order;
        }

        checkCreditLimit(user, order.creditAmount);
        user.orders.add(order);
        int nextTransactionalId = getNextTransactionId();
        user.creditAccount.addOrderToCreditHistory(order, getSharedProductById(product.sharedProductId), nextTransactionalId);
        saveObject(user);
        users.put(user.id, user);

        sendNotificationProductPurchased(product, user, order);
        return order;
    }

    @Override
    public synchronized String purchaseProduct(String productId, List<Integer> files) throws ErrorException {
        if (getSession().currentUser == null) {
            throw new ErrorException(26);
        }

        SedoxUser user = users.get(getSession().currentUser.id);

        if (user == null) {
            throw new ErrorException(26);
        }

        if (files == null) {
            throw new ErrorException(2000003);
        }

        purchaseProductInternalForUser(productId, user, files);

        SedoxProduct product = getProductById(productId);
        String base64ZipFile;

        if (files.size() == 1) {
            try {
                base64ZipFile = getBaseEncodedFile(product, files);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ErrorException(1029);
            }
        } else {
            try {
                base64ZipFile = getZipfileAsBase64(product, files);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ErrorException(1026);
            }
        }

        product.states.put("purchaseProduct", new Date());

        return base64ZipFile;
    }

    private void updateUserFromMagento(String id, boolean force) throws ErrorException {
        User user = userManager.getUserById("" + id);
    
        if (user == null) {
            return;
        }
        
        if (user.fullName != null && user.emailAddress != null && !force) {
            return;
        }

        int magentoId = 0;
        try {
            magentoId = Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            // This user is probably not part of the magento collective.
            return;
        }

        SedoxMagentoIntegration.MagentoUser magentoUser = sedoxMagentoIntegration.getUserInformation(magentoId);
        if (magentoUser != null) {
            user.fullName = magentoUser.name;
            user.emailAddress = magentoUser.emailAddress;
            user.cellPhone = magentoUser.phone;
            user.group = magentoUser.group;
            userManager.saveUser(user);
        }
    }

    public synchronized void updateOrders(List<SedoxMagentoIntegration.Order> orders) throws ErrorException {

        for (SedoxMagentoIntegration.Order order : orders) {
            SedoxCreditOrder sedoxCreditOrder = getSedoxCreditOrderById(order.orderId);
            if (sedoxCreditOrder != null) {
                continue;
            }

            SedoxUser sedoxUser = getSedoxUserById(order.customer_id);

            sedoxCreditOrder = new SedoxCreditOrder();
            sedoxCreditOrder.amount = order.credit;
            sedoxCreditOrder.magentoOrderId = order.orderId;

            sedoxUser.addCreditOrderUpdate(sedoxCreditOrder, null);
            saveObject(sedoxUser);

            addCreditToMaster(sedoxUser, sedoxCreditOrder, order);
        }
    }

    private SedoxCreditOrder getSedoxCreditOrderById(int id) {
        for (SedoxUser user : users.values()) {
            for (SedoxCreditOrder sedoxOrder : user.creditOrders) {
                if (sedoxOrder.magentoOrderId == id) {
                    return sedoxOrder;
                }
            }
        }
        return null;
    }

    private SedoxEvcCreditOrder getEvcCreditOrderById(int id) {
        for (SedoxUser user : users.values()) {
            for (SedoxEvcCreditOrder sedoxOrder : user.evcCreditOrders) {
                if (sedoxOrder.magentoOrderId == id) {
                    return sedoxOrder;
                }
            }
        }
        return null;
    }

    private byte[] getByteContentOfProduct(SedoxProduct product, List<Integer> files) throws IOException, ErrorException {
        byte[] zippedContent;

        if (files == null) {
            throw new ErrorException(2000003);
        }

        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(); ZipOutputStream out = new ZipOutputStream(byteOutputStream)) {
            for (Integer fileId : files) {
                SedoxBinaryFile binFile = sharedProduct.getFileById(fileId);
                String filePathString = "/opt/files/" + binFile.md5sum;

                File f = new File(filePathString);
                if (!f.exists()) {
                    f.createNewFile();
                }

                try (FileInputStream in = new FileInputStream(f)) {
                    out.putNextEntry(new ZipEntry(binFile.id + " - " + sharedProduct.getName() + " " + binFile.fileType + ".bin"));

                    byte[] b = new byte[1024];
                    int count;
                    while ((count = in.read(b)) > 0) {
                        out.write(b, 0, count);
                    }
                }
            }

            out.flush();
            out.close();

            byteOutputStream.flush();
            byteOutputStream.close();;
            zippedContent = byteOutputStream.toByteArray();
        }

        return zippedContent;
    }

    private String getZipfileAsBase64(SedoxProduct product, List<Integer> files) throws IOException, ErrorException {
        byte[] zippedContent = getByteContentOfProduct(product, files);
        return DatatypeConverter.printBase64Binary(zippedContent);
    }

    private void checkCreditLimit(SedoxUser user, double purchaseSum) throws ErrorException {

        if (getSession().currentUser != null && getSession().currentUser.isAdministrator()) {
            return;
        }

        double newBalance = user.creditAccount.getBalance() - purchaseSum;
        if (newBalance < 0 && !user.creditAccount.allowNegativeCredit) {
            throw new ErrorException(1027);
        }
    }
    
    private SedoxOrder getOrder(SedoxProduct sedoxProduct, List<Integer> files, SedoxUser user) throws ErrorException {

        if (sedoxProduct == null) {
            throw new ErrorException(1011);
        }

        SedoxOrder order = new SedoxOrder();
        order.productId = sedoxProduct.id;
        order.dateCreated = new Date();
        order.creditAmount = getPrice(sedoxProduct, user, files);
        return order;
    }

    private int getNextTransactionId() {
        int i = 0;
        for (SedoxUser sedoxUser : users.values()) {
            for (SedoxCreditHistory hist : sedoxUser.creditAccount.history) {
                if (hist.transactionReference > i && hist.transactionReference < 300000000) {
                    i = hist.transactionReference;
                }
            }
        }

        i++;
        return i;
    }

    public List<SedoxCreditHistory> getFilteredResult(FilterData filterData) {
        SedoxUser userAccount = getSedoxUserAccount();
        
        if (filterData.slaveId != null && !filterData.slaveId.isEmpty()) {
            userAccount = getSlave(filterData.slaveId);
        }
        
        Stream<SedoxCreditHistory> history = userAccount.creditAccount.history.stream();
        
        if (filterData.filterText != null && !filterData.filterText.isEmpty()) {
            history = history.filter(getFilterText(filterData));
        }
        
        return history.collect(Collectors.toList());
    }
    @Override
    public List<SedoxCreditHistory> getCurrentUserCreditHistory(FilterData filterData) {
        return pageIt(getFilteredResult(filterData), filterData);
    }
    
    @Override
    public int getCurrentUserCreditHistoryCount(FilterData filterData) {
        double count = getFilteredResult(filterData).size();
        
        if (count == 0) {
            return 1;
        }
        
        return (int)(Math.ceil((count/(double)filterData.pageSize)));
        
    }
    
    private int getNextFileId() {
        int i = 0;
        for (SedoxSharedProduct sedoxProduct : productsShared.values()) {
            for (SedoxBinaryFile file : sedoxProduct.binaryFiles) {
                if (file.id > i) {
                    i = file.id;
                }
            }
        }

        i++;
        return i;
    }

    private SedoxBinaryFile saveCmdEncryptedFile(String base64EncodeString, String originalFileName) throws ErrorException {
        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);

        if (isCmdFile(fileData)) {
            SedoxBinaryFile cmdEncryptedFile = new SedoxBinaryFile();
            cmdEncryptedFile.fileType = "CmdEncrypted";
            cmdEncryptedFile.id = getNextFileId();
            cmdEncryptedFile.md5sum = getMd5Sum(fileData);
            cmdEncryptedFile.orgFilename = originalFileName + "-cmd-encrypted";

            writeFile(fileData);

            return cmdEncryptedFile;
        }

        return null;
    }

    private void writeFile(byte[] data) throws ErrorException {
        try {
            String fileName = "/opt/files/" + getMd5Sum(data);

            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(data);
            fos.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private SedoxBinaryFile getOriginalBinaryFile(String base64EncodeString, String originalFileName) throws ErrorException {
        // Check if needs to be decrypted.
        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);

        SedoxBinaryFile originalFile = new SedoxBinaryFile();

        if (isCmdFile(fileData)) {
            try {
                fileData = doCMDEncryptedFile(fileData, originalFile);
                if (fileData == null || fileData.length == 0) {
                    return null;
                }
            } catch (Exception ex) {
                return null;
            }
        }

        originalFile.fileType = "Original";
        originalFile.id = getNextFileId();
        originalFile.md5sum = getMd5Sum(fileData);
        originalFile.orgFilename = originalFileName;

        writeFile(fileData);

        return originalFile;
    }

    private void sendFileCreatedNotification(SedoxProduct sedoxProduct) throws ErrorException {
        sendAirgramMessages(userManager, sedoxProduct);
        sendNotificationEmail("files@tuningfiles.com", sedoxProduct);
    }

    private void sendAirgramMessages(UserManager userManager, SedoxProduct sedoxProduct) throws ErrorException {
        for (SedoxUser developer : getDevelopers()) {
            User user = userManager.getUserById(developer.id);
            if (developer.isActiveDelevoper) {
                sendPushoverMessage(user.emailAddress, sedoxProduct);
            }
        }
    }

    private void sendPushoverMessage(String emailAddress, SedoxProduct sedoxProduct) throws ErrorException {
        User user = userManager.getUserById(sedoxProduct.firstUploadedByUserId);
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);
        SedoxUser sedoxUser = getSedoxUserAccountById(user.id);

        String message = "File: " + sharedProduct.getName();
        message += " | User: " + user.fullName;
        message += " | Credit: " + sedoxUser.creditAccount.getBalance();
        sedoxPushover.sendMessage(sedoxUser.pushoverId, message);
    }

    private String getNextProductId() {
        int lowest = 0;
        for (SedoxProduct sedoxProduct : products.values()) {
            try {
                int sedoxId = Integer.parseInt(sedoxProduct.id);
                if (sedoxId > lowest) {
                    lowest = sedoxId;
                }
            } catch (Exception ex) {
                // Dont care.
            }
        }

        lowest++;
        return "" + lowest;
    }

    private void saveUser(SedoxUser user) throws ErrorException {
        String tmpPassword = "abcd1234-56789ss!"; // 

        User getshopUser = userManager.getUserById(user.magentoId);
        if (getshopUser == null) {
            getshopUser = new User();
            getshopUser.id = user.magentoId;
            getshopUser.username = user.magentoId;
            getshopUser.type = 10;
            getshopUser.password = tmpPassword;
            getshopUser.storeId = storeId;
            getshopUser = userManager.createUser(getshopUser);
        }

        user.id = getshopUser.id;
        user.storeId = storeId;
        saveObject(user);
        users.put(user.id, user);
    }

    private String getMd5Sum(byte[] data) throws ErrorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] array = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ErrorException(1024);
        }
    }

    private SedoxUser getSedoxUserById(String id) throws ErrorException {
        SedoxUser user = users.get(id);

        if (user == null) {
            user = new SedoxUser();
            user.magentoId = id;
            saveUser(user);
        }

        return user;
    }

    public synchronized void updateAllUsers() {
        int i = 0;
        for (SedoxUser user : users.values()) {
            i++;
            try {
                updateUserFromMagento(user.id, true);
            } catch (ErrorException | NullPointerException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Users updated");
    }

    @Override
    public List<User> searchForUsers(String searchString) throws ErrorException {
        if (searchString == null) {
            return new ArrayList();
        }
        

        Set<User> retUsers = new HashSet();

        for (String searchStringI : searchString.split(":")) {
            searchStringI = searchStringI.toLowerCase();
            for (User user : userManager.getAllUsers()) {
                if (user.fullName != null && user.fullName.toLowerCase().contains(searchStringI)) {
                    retUsers.add(user);
                }
                if (user.emailAddress != null && user.emailAddress.toLowerCase().contains(searchStringI)) {
                    retUsers.add(user);
                }
            }
        }

        return new ArrayList(retUsers);
    }

    @Override
    public SedoxUser getSedoxUserAccountById(String userid) {
        return users.get(userid);
    }

    @Override
    public void addUserCredit(String id, String description, int amount) throws ErrorException {
        SedoxUser user = getSedoxUserById(id);
        if (user != null) {
            int minimum = 1000200;
            int maxValue = 4000000;
            int minvalue = 1000000;
            Random rn = new Random();
            int tranid = minimum + rn.nextInt(maxValue - minvalue + 1);

            SedoxCreditOrder sedoxCreditOrder = new SedoxCreditOrder();
            sedoxCreditOrder.amount = amount;
            sedoxCreditOrder.magentoOrderId = tranid;

            user.addCreditOrderUpdate(sedoxCreditOrder, description);
            saveObject(user);
            users.put(user.id, user);
        }
    }

    @Override
    public User login(String emailAddress, String password) {
        try {
            return userManager.logOn(emailAddress, password);
        } catch (ErrorException ex) {
            // ITs Ok
        }

        String loggedUserId = sedoxMagentoIntegration.login(emailAddress, password);

        if (loggedUserId != null) {
            User user = userManager.forceLogon(loggedUserId);

            if (user == null) {
                user = saveNewUserAndUpdateFromMagento(loggedUserId, userManager);
            }

            return user;
        } else {
//            throw new ErrorException(13);
        }
        
        return null;
    }

    @Override
    public void setChecksum(String productId, String checksum) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        sharedProduct.originalChecksum = checksum;
        saveObject(sharedProduct);
    }

    @Override
    public SedoxOrder purchaseOnlyForCustomer(String productId, List<Integer> files) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxUser user = getSedoxUserById(product.firstUploadedByUserId);
        SedoxOrder order = purchaseProductInternalForUser(productId, user, files);
        product.states.put("purchaseOnlyForCustomer", new Date());
        saveObject(product);
        return order;
    }

    private String getSignature() {
        String signature = "<br><br>Thank you for using tuningfiles.com";
        signature += "<br>tuningfiles.com - The ultimate file delivery system!";
        signature += "<br>Phone: +47 909 16 585";
        signature += "<br>";
        signature += "<br><font style='color:red'>Note: If Your Credit depot is empty and You want to download the tuning file.";
        signature += "<br>please purchase Credit from the following link: </font><a href='http://www.tuningfiles.com/100-credits-configure-your-credit.html'>http://www.tuningfiles.com/100-credits-configure-your-credit.html</a>";
        return signature;
    }

    private User getGetshopUser(String id) throws ErrorException {
        User getshopUser = userManager.getUserById(id);
        if (getshopUser == null || getshopUser.emailAddress == null) {
            updateUserFromMagento(id, true);
        }
        return getshopUser;
    }

    @Override
    public void notifyForCustomer(String productId, String extraText) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        SedoxUser user = getSedoxUserById(product.firstUploadedByUserId);
        User getshopUser = getGetshopUser(user.id);
        String content = getMailContent(extraText, productId, null, user, null);
        mailFactory.send("files@tuningfiles.com", getshopUser.emailAddress, sharedProduct.getName(), content);
        product.addCustomerNotified(getSession().id, getshopUser);
        product.states.put("notifyForCustomer", new Date());

        if (getshopUser.cellPhone != null && !getshopUser.cellPhone.equals("")) {
            smsFactory.send("Sedox Performance", getshopUser.cellPhone, "Your file is ready from Sedox Performance");
            product.addSmsSentToCustomer(getSession().id, getshopUser);
        }

        saveObject(product);
    }

    private void copyFile(File from, File to) {

        try {
            if (!to.exists()) {
                to.createNewFile();
            }

            try (
                    FileChannel in = new FileInputStream(from).getChannel();
                    FileChannel out = new FileOutputStream(to).getChannel()) {

                out.transferFrom(in, 0, in.size());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendProductByMail(String productId, String extraText, List<Integer> files) throws ErrorException {
        SedoxOrder order = purchaseOnlyForCustomer(productId, files);

        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        HashMap<String, String> fileMap = new HashMap();
        if (sharedProduct.isCmdEncryptedProduct && files.size() != 2) {
            throw new ErrorException(1031);
        }
        if (sharedProduct.isCmdEncryptedProduct) {
            String fileName = encryptProductAndZipToTmpFolder(product, files);
            fileMap.put(fileName, sharedProduct.getName() + ".mod");
        } else {
            if (files.size() == 1) {
                for (Integer fileId : files) {
                    SedoxBinaryFile binFile = sharedProduct.getFileById(fileId);
                    File origFile = new File("/opt/files/" + binFile.md5sum);
                    String fileName = "/tmp/" + UUID.randomUUID().toString();
                    File tmpFile = new File(fileName);
                    copyFile(origFile, tmpFile);
                    String extentions = binFile.fileType != null && binFile.fileType.equals("Original") ? ".orig" : ".mod";
                    String fileNameInEmail = sharedProduct.getName() + extentions;
                    fileMap.put(fileName, fileNameInEmail);
                    break;
                }

            } else {
                String fileName = zipProductToTmpFolder(product, files);
                fileMap.put(fileName, sharedProduct.getName() + ".zip");
            }

        }

        SedoxUser user = getSedoxUserById(product.firstUploadedByUserId);
        User getshopUser = getGetshopUser(product.firstUploadedByUserId);
        String content = getMailContent(extraText, productId, order, user, files);

        mailFactory.sendWithAttachments("files@tuningfiles.com", getshopUser.emailAddress, sharedProduct.getName(), content, fileMap, true);
        product.states.put("sendProductByMail", new Date());

        if (getshopUser.cellPhone != null && !getshopUser.cellPhone.equals("") && smsFactory != null) {
            smsFactory.send("Sedox Performance", getshopUser.cellPhone, "Your file is ready from Sedox Performance");
            product.addSmsSentToCustomer(getSession().id, getshopUser);
        }

        product.addProductSentToEmail(getSession().id, getshopUser);
        
        saveObject(product);
        notifyOnSocket(product);
    }

    private String zipProductToTmpFolder(SedoxProduct sedoxProduct, List<Integer> files) throws ErrorException {
        try {
            byte[] zippedContent = getByteContentOfProduct(sedoxProduct, files);

            String fileName = "/tmp/" + UUID.randomUUID().toString();
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(zippedContent);
            fos.close();

            return fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getMailContent(String extraText, String productId, SedoxOrder order, SedoxUser user, List<Integer> files) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        String content = "Your file is ready! :)";
        if (extraText != null && !extraText.equals("")) {
            content += "<br>";
            content += "<br> Please note:";
            content += "<br> " + extraText.replace("\n", "<br/>");
        }
        
        if (product != null 
                && product.reference != null 
                && product.reference.get(product.firstUploadedByUserId) != null 
                && !product.reference.get(product.firstUploadedByUserId).isEmpty()
            ) {
            content += "<br>";
            content += "<br> Reference:";
            content += "<br> " + product.reference.get(product.firstUploadedByUserId).replace("\n", "<br/>");
        }
        
        if (sharedProduct != null && files != null && !files.isEmpty()) {
            content += "<br>";
            content += "<br>Files: ";
            
            for (Integer fileId : files) {
                SedoxBinaryFile file = sharedProduct.getFileById(fileId);
                if (file == null) {
                    continue;
                }
                
                content += "<br> " + file.fileType;
                if (file.extraInformation != null && !file.extraInformation.isEmpty()) {
                    content += " - " + file.extraInformation;
                }
            }
        }
        
        
        if (order != null) {
            content += "<br/><br/> Your account has been changed with: -" + order.creditAmount;
            content += "<br/> New account balance is now: " + user.creditAccount.getBalance() + " credit";
        } else {
            content += "<br/><br/> Your current account balance: " + user.creditAccount.getBalance() + " credit";
        }
        content += "<br/><br/>Link to product <a href='http://databank.tuningfiles.com/index.php?page=productview&productId=" + productId + "'>http://databank.tuningfiles.com/index.php?page=productview&productId=" + productId + "</a>";
        content += getSignature();
        return content;
    }

    @Override
    public void changeDeveloperStatus(String userId, boolean disabled) throws ErrorException {
        SedoxUser user = getSedoxUserAccountById(userId);
        user.isActiveDelevoper = disabled;
        users.put(user.id, user);
        saveObject(user);
    }

    @Override
    public List<SedoxUser> getDevelopers() throws ErrorException {
        List<SedoxUser> retUsers = new ArrayList();

        for (User user : userManager.getAllUsers()) {
            if (user.isAdministrator()) {
                SedoxUser sedoxUser = getSedoxUserAccountById(user.id);
                if (sedoxUser != null) {
                    retUsers.add(sedoxUser);
                }
            }
        }

        return retUsers;
    }

    private void sendNotificationEmail(String emailAddress, SedoxProduct sedoxProduct) throws ErrorException {
        sendNotificationEmail(emailAddress, sedoxProduct, null);
    }

    private void sendNotificationEmail(String emailAddress, SedoxProduct sedoxProduct, String special) throws ErrorException {
        HashMap<String, String> fileMap = new HashMap();
        
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);

        for (SedoxBinaryFile binFile : sharedProduct.binaryFiles) {
            fileMap.put("/opt/files/" + binFile.md5sum, binFile.id + " " + binFile.fileType);
        }

        User user = getGetshopUser(sedoxProduct.firstUploadedByUserId);
        SedoxUser sedoxUser = getSedoxUserAccountById(sedoxProduct.firstUploadedByUserId);
        String content = "New file uploaded";

        if (special != null) {
            content = "Special file request!";
            content += "<br>Comment:";
            content += "<br>" + special;
            user = getSession().currentUser;
            sedoxUser = getSedoxUserAccountById(user.id);
        }

        content += "<br><b>Car details: </b>" + sharedProduct.getName();
        content += "<br>";
        content += "<br>Name: " + user.fullName;
        content += "<br>Email: " + user.emailAddress;
        content += "<br>Customer id: " + user.id;

        if (special == null) {
            content += "<br>Tool: " + sharedProduct.tool;
            content += "<br>Gearbox: " + sharedProduct.gearType;
            content += "<br>Comment: ";
            content += "<br> " + sedoxProduct.comment;
            content += "<br>";
            for (SedoxBinaryFile file : sharedProduct.binaryFiles) {
                content += "<br>Original filename: " + file.orgFilename;
            }
        }

        content += "<br> ";
        content += "<br>Credit balance: " + sedoxUser.creditAccount.getBalance();
        content += "</br><br/>Link to product <a href='http://databank.tuningfiles.com/index.php?page=productview&productId=" + sedoxProduct.id + "'>http://databank.tuningfiles.com/index.php?page=productview&productId=" + sedoxProduct.id + "</a>";
        sedoxDatabankMailAccount.sendWithAttachments(user.emailAddress, emailAddress, "Upload id: " + sedoxProduct.id + " - " + sharedProduct.getName(), content, fileMap, false);
    }

    private double getAlreadySpentOnProduct(SedoxProduct sedoxProduct, SedoxUser user) {
        double spent = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -3);
        Date threeMonthsAgo = cal.getTime();
        
        for (SedoxOrder order : user.orders) {
            if (order.dateCreated.before(threeMonthsAgo)) {
                continue;
            }
            
            if (sedoxProduct.id != null && sedoxProduct.id.equals(order.productId)) {
                spent += order.creditAmount;
            }
        }

        return spent;
    }

    @Override
    public void removeBinaryFileFromProduct(String productId, int fileId) throws ErrorException {
        SedoxProduct sedoxProduct = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);
        
        if (sedoxProduct != null) {
            sharedProduct.removeBinaryFile(fileId);
            saveObject(sharedProduct);
            notifyOnSocket(sedoxProduct);
        }
    }

    @Override
    public void toggleAllowNegativeCredit(String userId, boolean allow) throws ErrorException {
        SedoxUser sedoxUser = getSedoxUserAccountById(userId);
        sedoxUser.creditAccount.allowNegativeCredit = allow;
        saveObject(sedoxUser);
    }

    @Override
    public void toggleAllowWindowsApp(String userId, boolean allow) throws ErrorException {
        SedoxUser sedoxUser = getSedoxUserAccountById(userId);
        sedoxUser.canUseExternalProgram = allow;
        saveObject(sedoxUser);
    }

    /**
     * Checks if the binary data contains the acscii chars CMD in beginning of
     * array
     *
     * @param fileData
     * @return
     */
    private boolean isCmdFile(byte[] fileData) {
        if (fileData.length > 3) {
            String check = "" + fileData[0] + fileData[1] + fileData[2];;
            return check.equals("677768");
        }

        return false;
    }

    private byte[] doCMDEncryptedFile(byte[] fileData, SedoxBinaryFile originalFile) {
        FilesMessage message = sedoxCMDEncrypter.decrypt(fileData);
        if (message.getFile_29Bl28Fm58W() != null) {
            fileData = DatatypeConverter.parseBase64Binary(message.getFile_29Bl28Fm58W());
            originalFile.cmdFileType = "29Bl28Fm58W";
        }
        if (message.getFile_mpc5Xxflash() != null) {
            fileData = DatatypeConverter.parseBase64Binary(message.getFile_mpc5Xxflash());
            originalFile.cmdFileType = "mpc5Xxflash";
        }
        if (message.getFile_seriale2Prom() != null) {
            fileData = DatatypeConverter.parseBase64Binary(message.getFile_seriale2Prom());
            originalFile.cmdFileType = "seriale2Prom";
        }
        return fileData;
    }

    private String encryptProductAndZipToTmpFolder(SedoxProduct product, List<Integer> files) throws ErrorException {
        try {
            SedoxBinaryFile originalFile = null;
            SedoxBinaryFile tuningFile = null;
            SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
            
            for (Integer sedoxBinaryFileId : files) {
                
                SedoxBinaryFile file = sharedProduct.getFileById(sedoxBinaryFileId);
                if (file.cmdFileType != null) {
                    originalFile = file;
                } else {
                    tuningFile = file;
                }
            }

            if (originalFile == null || tuningFile == null) {
                throw new ErrorException(2000001);
            }

            
            byte[] data = sedoxCMDEncrypter.decrypt(originalFile, tuningFile);
            String fileName = "/tmp/" + product.fileSafeName(sharedProduct.getName()) + ".mod";
            Path path = Paths.get(fileName);
            Files.write(path, data);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void addSlaveToUser(String masterUserId, String slaveUserId) throws ErrorException {
        SedoxUser slave = getSedoxUserAccountById(slaveUserId);
        slave.masterUserId = masterUserId;
        saveUser(slave);
    }

    @Override
    public List<SedoxUser> getSlaves(String masterUserId) {
        ArrayList<SedoxUser> sedoxUsers = new ArrayList();

        for (SedoxUser user : users.values()) {
            if (user.masterUserId != null && user.masterUserId.equals(masterUserId)) {
                sedoxUsers.add(user);
            }
        }

        return sedoxUsers;
    }

    @Override
    public void addCreditToSlave(String slaveId, double amount) throws ErrorException {
        SedoxUser user = getSedoxUserAccountById(slaveId);
        if (user != null) {
            user.slaveIncome = amount;
            saveUser(user);
        }
    }

    private void checkIfMasterOfSlave(String forSlaveId) throws ErrorException {
        SedoxUser slave = getSedoxUserAccountById(forSlaveId);
        String currentUserId = getSession().currentUser.id;

        if (slave.masterUserId != null && slave.masterUserId.equals(currentUserId)) {
            return;
        }

        throw new ErrorException(26);
    }

    @Override
    public void togglePassiveSlaveMode(String userId, boolean toggle) throws ErrorException {
        SedoxUser slave = getSedoxUserAccountById(userId);
        if (slave != null) {
            slave.isPassiveSlave = toggle;
            saveUser(slave);
        }
    }

    private void addCreditToMaster(SedoxUser sedoxUser, SedoxCreditOrder sedoxCreditOrder, SedoxMagentoIntegration.Order order) throws ErrorException {
        if (sedoxUser.masterUserId != null && !sedoxUser.masterUserId.equals("") && sedoxCreditOrder.amount > 0) {
            double creditToAdd = sedoxCreditOrder.amount / 100 * sedoxUser.slaveIncome;
            if (creditToAdd > 0) {
                User masterGetshopUser = userManager.getUserById(sedoxUser.id);
                SedoxCreditOrder kickbackCreditOrder = new SedoxCreditOrder();
                kickbackCreditOrder.amount = creditToAdd;
                kickbackCreditOrder.magentoOrderId = order.orderId;

                SedoxUser master = getSedoxUserAccountById(sedoxUser.masterUserId);
                master.addCreditOrderUpdate(kickbackCreditOrder, "Added " + kickbackCreditOrder.amount + " credit to your account, your partner " + masterGetshopUser.fullName + " placed order for " + order.credit + " credits");

                saveObject(master);
            }
        }
    }

    @Override
    public void toggleStartStop(String productId, boolean toggle) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        if (product != null) {
            product.started = toggle;
            product.startedByUserId = getSession().currentUser.id;
            product.startedDate = new Date();
            product.addMarkedAsStarted(getSession().currentUser.id, toggle);
            saveObject(product);
            
            notifyOnSocket(product);
        }
    }
    
    public void notifyOnSocket(SedoxProduct sedoxProduct) {
        if (getSession() != null && getSession().currentUser != null) {
            String userid = getSession().currentUser.id;
//            // TODO : SEND ON TOPIC.
//            webSocketServer.sendMessage("{ \"action\": \"startstoptoggled\", \"userId\": \""+userid+"\" , \"productId\": \""+sedoxProduct.id+"\" }");    
        }
    }

    @Override
    public String getExtraInformationForFile(String productId, int fileId) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        for (SedoxBinaryFile binFile : sharedProduct.binaryFiles) {
            if (binFile.id == fileId) {
                return binFile.extraInformation;
            }
        }

        return "";
    }

    @Override
    public void setExtraInformationForFile(String productId, int fileId, String text) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        if (product != null) {
            for (SedoxBinaryFile binFile : sharedProduct.binaryFiles) {
                if (binFile.id == fileId) {
                    binFile.extraInformation = text;
                }
            }

            saveObject(sharedProduct);
            notifyOnSocket(product);
        }

    }

    private void sendNotificationProductPurchased(SedoxProduct product, SedoxUser user, SedoxOrder order) throws ErrorException {
        User getshopUser = userManager.getUserById(user.id);
        
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);

        String subject = "Product purchased: " + product.id + " - " + sharedProduct.getName();
        String message = "Purchased by user: " + user.id + " - " + getshopUser.fullName;
        message += "<br/> Credit amount: " + order.creditAmount;
        message += "<br/> New credit balance: " + user.creditAccount.getBalance();

        for (SedoxUser developer : getDevelopers()) {
            if (developer.isActiveDelevoper) {
                User getshopUserDeveloper = userManager.getUserById(developer.id);
                mailFactory.send(getshopUser.emailAddress, getshopUserDeveloper.emailAddress, subject, message);
            }
        }
    }

    private void sendNotificationToUploadedUser(SedoxProduct sedoxProduct) throws ErrorException {
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);
        User getshopUser = userManager.getUserById(sedoxProduct.firstUploadedByUserId);
        String subject = "We have received your file request";
        String message = "You uploaded file: " + sharedProduct.getName();
        message += "<br/>";
        message += "<br/>Thank you. You will receive an email from us when we have processed your file.";
        message += "<br/>";
        message += "<br/>Best Regards";
        message += "<br/>Tuningfiles Support";
        mailFactory.send("files@tuningfiles.com", getshopUser.emailAddress, subject, message);
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.context = ac;
    }

    private User saveNewUserAndUpdateFromMagento(String userId, UserManager userManager) throws ErrorException {
        getSedoxUserById(userId);
        updateUserFromMagento(userId, true);
        return userManager.forceLogon(userId);
    }

    @Override
    public void toggleSaleableProduct(String productId, boolean saleable) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        if (product != null) {
            sharedProduct.saleAble = saleable;
            saveObject(sharedProduct);
            notifyOnSocket(product);
        }
    }

    private void updateOrders() throws ErrorException {
        List<SedoxMagentoIntegration.Order> orders = sedoxMagentoIntegration.getOrders();
        updateOrders(orders);
    }

    @Override
    public void toggleIsNorwegian(String userId, boolean isNorwegian) throws ErrorException {
        SedoxUser user = getSedoxUserById(userId);
        if (user != null) {
            user.isNorwegian = isNorwegian;
        }
    }

    private String getBaseEncodedFile(SedoxProduct product, List<Integer> files) throws IOException {
        Integer fileId = files.iterator().next();
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        
        for (SedoxBinaryFile file : sharedProduct.binaryFiles) {
            if (file.id == fileId) {
                String filePathString = "/opt/files/" + file.md5sum;
                
                File f = new File(filePathString);
                if(!f.exists() && !f.isDirectory()) { 
                    f.createNewFile();
                }
                
                Path path = Paths.get(filePathString);
                byte[] data = Files.readAllBytes(path);
                return DatatypeConverter.printBase64Binary(data);
            }
        }
        return null;
    }

    @Override
    public List<SedoxProduct> getLatestProductsList(int count) throws ErrorException {
        Set<SedoxProduct> reversedList = new TreeSet(products.values());

        List<SedoxProduct> retProducts = new ArrayList();
        int i = 0;

        for (SedoxProduct prod : reversedList) {
            if (prod.duplicate) {
                continue;
            }
            
            finalize(prod);
            retProducts.add(prod);
            i++;
            if (i >= count) {
                break;
            }

        }

        return retProducts;
    }

    @Override
    public void toggleBadCustomer(String userId, boolean badCustomer) throws ErrorException {
        SedoxUser user = getSedoxUserById(userId);
        if (user != null) {
            user.badCustomer = badCustomer;
            saveUser(user);
        }
    }

    @Override
    public void addReference(String productId, String reference) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        User user = getSession().currentUser;
        product.reference.put(user.id, reference);
        saveObject(product);
        notifyOnSocket(product);
    }

    @Override
    public void transferCreditToSlave(String slaveId, double amount) throws ErrorException {
        User user = getSession().currentUser;
        if (user == null) {
            throw new ErrorException(26);
        }
        
        SedoxUser sedoxMaster = getSedoxUserAccount();
        
        if(sedoxMaster.creditAccount.getBalance() < amount) {
            throw new ErrorException(102);
        }
        
        List<SedoxUser> slaveUsers = getSlaves(user.id);
        for (SedoxUser slaveUser : slaveUsers) {
            if (slaveUser.id.equals(slaveId)) {
                SedoxCreditOrder sedoxSlaveCreditOrder = new SedoxCreditOrder();
                sedoxSlaveCreditOrder.amount = amount;
                slaveUser.addCreditOrderUpdate(sedoxSlaveCreditOrder, "Transfered credit from " + user.fullName);
                saveObject(slaveUser);
                
                User islaveUser = getGetshopUser(slaveUser.id);
                SedoxCreditOrder sedoxCreditOrder = new SedoxCreditOrder();
                sedoxCreditOrder.amount = amount * -1;
                sedoxMaster.addCreditOrderUpdate(sedoxCreditOrder, "Transfered credit to " + islaveUser.fullName);
                saveObject(sedoxMaster); 
            }
        }
    }   

    @Override
    public List<SedoxProductStatistic> getStatistic() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        nowCalendar.add(Calendar.MONTH, 1);
        nowCalendar.set(Calendar.DAY_OF_MONTH, 1);
        nowCalendar.set(Calendar.HOUR, 0);
        nowCalendar.set(Calendar.MINUTE, 59);
        nowCalendar.set(Calendar.SECOND, 0);
            
            
        List<SedoxProductStatistic> results = new ArrayList();
        for (int i=1; i<50; i++) {
            
            SedoxProductStatistic statistic = new SedoxProductStatistic();
            Date endDate = nowCalendar.getTime();
            
            
            nowCalendar.add(Calendar.MONTH, -1);
            Date startDate = nowCalendar.getTime();
            
            statistic.startDate = startDate;
            statistic.endDate = endDate;
            
            for (SedoxProduct product : products.values()) {
                if (product.rowCreatedDate.after(startDate) && product.rowCreatedDate.before(endDate))
                    statistic.count++;
            }
            
            results.add(statistic);
        }
       
        return results;
    }

    @Override
    public List<String> getProductIds() throws ErrorException {
        List<String> productIds = new ArrayList();
        for (SedoxProduct product : products.values()) {
            productIds.add(product.id);
        }
        
        return productIds;
    }

    private double getPrice(SedoxProduct sedoxProduct, SedoxUser user, List<Integer> files) {
        double totalPrice = 40;
        
        if (user.fixedPrice != null && !user.fixedPrice.isEmpty()) {
            totalPrice = 0;
        }
        
        SedoxSharedProduct sharedProduct = getSharedProductById(sedoxProduct.sharedProductId);
        
        double mostExpensive = 0;
        if (files != null) {
            for (int fileId : files) {
                SedoxBinaryFile binFile = sharedProduct.getFileById(fileId);
                if (binFile != null && binFile.getPrice(user) > mostExpensive) {
                    mostExpensive = binFile.getPrice(user);
                }
            }
        }

        totalPrice += mostExpensive;
        double alreadySpentOnProduct = getAlreadySpentOnProduct(sedoxProduct, user);
        if (alreadySpentOnProduct < 0) {
            alreadySpentOnProduct = alreadySpentOnProduct * -1;
        }
        totalPrice = totalPrice - alreadySpentOnProduct;

        // Make sure that the user does not pay for its own original file.
        if (totalPrice == 40 && sedoxProduct.firstUploadedByUserId != null && sedoxProduct.firstUploadedByUserId.equals(user.id)) {
            totalPrice = 0;
        }
        
        return totalPrice;
    }

    @Override
    public Double getPriceForProduct(String productId, List<Integer> files) throws ErrorException {
        if (getSession() == null || getSession().currentUser == null)  {
            return -1D;
        }
            
        if (files == null || files.size() == 0) {
            return -1D;
        }
        
        SedoxUser sedoxUser = getSedoxUserById(getSession().currentUser.id);
        SedoxProduct product = getProductById(productId);
        double price = getPrice(product, sedoxUser, files);
        
        if (price > 0) {
            return price;
        }
        
        return 0D;
    }

    @Override
    public void setFixedPrice(String userId, String price) throws ErrorException {
        SedoxUser user = getSedoxUserAccountById(userId);
        if (user != null) {
            user.fixedPrice = price;
            saveObject(user);
        }
    }

    @Override
    public void syncFromMagento(String userId) throws ErrorException {
        getSedoxUserById(userId);
        updateUserFromMagento(userId, true);
    }

    @Override
    public int getFileNotProcessedToDayCount(int daysBack) throws ErrorException {
        List<SedoxProduct> productsToday = getProductsByDaysBack(daysBack);
        int count = 0;
        for (SedoxProduct product : productsToday) {
            if (product.isFinished || product.states.containsKey("sendProductByMail"))
                continue;
            
            count++;
        }
        
        return count;
    }

    @Override
    public void markAsFinished(String productId, boolean finished) throws ErrorException {
        SedoxProduct sedoxProduct = getProductById(productId);
        if (sedoxProduct != null) {
            sedoxProduct.isFinished = finished;
            saveObject(sedoxProduct);
        }
    }

    @Override
    public SedoxSharedProduct getSharedProductById(String sharedProductId) {
        return productsShared.get(sharedProductId);
    }

    private void finalize(SedoxSharedProduct product) {
        product.printableName = product.getName();
    }
    
    private void finalize(SedoxProduct product) {
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        product.populate(sharedProduct);
    }

    @Override
    public void updateEvcCreditAccounts() throws ErrorException {
        List<SedoxMagentoIntegration.Order> orders = sedoxMagentoIntegration.getEvcOrders();
        for (SedoxMagentoIntegration.Order order : orders) {
            SedoxEvcCreditOrder evcOrder = getEvcCreditOrderById(order.orderId);
            
            if (evcOrder != null) {
                continue;
            }
            
            if (hasAlreadyFailed(order)) {
                continue;
            }
            
            SedoxUser user = getSedoxUserAccountById(order.customer_id);
            if (user == null) {
                notifyEvcError("Could not add credit to account, user with id: " + order.customer_id, order);
                return;
            }
            
            addEvcCredit(user, order);
        }
    }

    private void notifyEvcError(String message, SedoxMagentoIntegration.Order order, boolean silent) throws ErrorException {
        FailedEvcOrder failedOrder = new FailedEvcOrder();
        failedOrder.message = message;
        failedOrder.magentoOrderId = order.orderId;
        failedOrder.storeId = storeId;
        
        failedEvcOrders.add(failedOrder);
        
        if (!silent)
            mailFactory.send("files@tuningfiles.com", "files@tuningfiles.com", "EVC Credit automatic update failed", message);
        
        saveObject(failedOrder);
    }
    
    private void notifyEvcError(String message, SedoxMagentoIntegration.Order order) throws ErrorException {
        notifyEvcError(message, order, false);
    }

    private void addEvcCredit(SedoxUser user, SedoxMagentoIntegration.Order order) throws ErrorException {
        boolean exists = evcApi.isPersonalAccount(order.evccustomerid);
        if (!exists) {
            boolean success = evcApi.addPersonalAccount(order.evccustomerid);
            
            if (!success) {
                notifyEvcError("Was not able to automatically update evc credit, orderid: " + order.orderId + " Credit: " + order.credit + ", failed because EVC does not accept that you add account: " + order.evccustomerid, order);
                return;
            }
        }
        
        int credit = evcApi.getPersonalAccountBalance(order.evccustomerid);
        credit += order.credit;
        
        boolean success = evcApi.setPersonalAccountBalance(order.evccustomerid, credit);
        
        if (!success) {
            notifyEvcError("Evc did not accept setting the new credit, orderid: " + order.orderId + " Credit: " + credit + " evcid: " + order.evccustomerid, order);
            return;
        }
        
        SedoxEvcCreditOrder creditOrder = new SedoxEvcCreditOrder();
        creditOrder.magentoOrderId = order.orderId;
        creditOrder.evcCustomerId = order.evccustomerid;
        creditOrder.amount = order.credit;
        
        user.addEvcCreditOrder(creditOrder);
        saveObject(user);
    }

    private boolean hasAlreadyFailed(SedoxMagentoIntegration.Order order) {
        for (FailedEvcOrder failed : failedEvcOrders) {
            if (failed.magentoOrderId == order.orderId) {
                return true;
            }
        }
        
        return false;
    }

    private void resetEvcOrders() throws ErrorException {
        if (failedEvcOrders.isEmpty()) {
            List<SedoxMagentoIntegration.Order> orders = sedoxMagentoIntegration.getEvcOrders();
            for (SedoxMagentoIntegration.Order order : orders) {
                notifyEvcError("skipped", order, true);
                System.out.println("Skipped it");
            }
        }
    }

    /**
     * Returns the first product based on a shared product
     * for logged in user.
     * 
     * @param shareProductId
     * @return 
     */
    private SedoxProduct getCustomerProduct(String shareProductId) {
        User loggedInUser = getSession().currentUser;
        if (loggedInUser == null) {
            return null;
        }
        
        for (SedoxProduct product: products.values()) {
            if (product.sharedProductId != null && product.sharedProductId.equals(shareProductId) && product.firstUploadedByUserId != null && product.firstUploadedByUserId.equals(loggedInUser.id)) {
                return product;
            }
        }
        
        return null;
    }

    @Override
    public List<SedoxOrder> getOrders(FilterData filterData) {
        List<SedoxOrder> orders = getOrdersInternal(filterData);
        return pageIt(orders, filterData);
    }
    
    private boolean filterMatch(String productId, String filterText) {
        SedoxProduct product = products.get(productId);
        
        if (product == null) {
            return false;
        }
        
        SedoxSharedProduct sharedProduct = getSharedProductById(product.sharedProductId);
        if (sharedProduct == null) {
            return false;
        }
        
        if (sharedProduct.getName().toLowerCase().contains(filterText)) {
            return true;
        }
        
        return false;
    }

    private List pageIt(List filteredOrders, FilterData filterData) {
        if (filterData.pageNumber == 0) 
            filterData.pageNumber = 1;
        
        
        
        filteredOrders = new ArrayList(filteredOrders);
        
        Comparator sorter = new Sorters(products, productsShared).getSorter(filterData);
        if (sorter != null) {
            Collections.sort(filteredOrders, sorter);
        }
        
        int end = (filterData.pageNumber*filterData.pageSize) ;
        int start = ((filterData.pageNumber-1)*filterData.pageSize);
        
        if (filteredOrders.size() >= end) {
            return new ArrayList(filteredOrders.subList(start, end));
        } 
        
        if (filteredOrders.size() >= start) {
            return new ArrayList(filteredOrders.subList(start, filteredOrders.size()));
        }
        
        
        return new ArrayList(filteredOrders);
    }

    private List<SedoxOrder> getOrdersInternal(FilterData filterData) {
            if (getSedoxUserAccount() == null) {
            return new ArrayList();
        }

        List<SedoxOrder> orders = getSedoxUserById(getSession().currentUser.id).orders;
        
        if (filterData.filterText != null && !filterData.filterText.isEmpty()) {
            ArrayList<SedoxOrder> filteredOrders = new ArrayList();
            
            for (SedoxOrder order : orders) {
                if (filterMatch(order.productId, filterData.filterText)) {
                    filteredOrders.add(order);
                }
            }

            orders = filteredOrders;
        }
        
        return orders;
    }

    @Override
    public int getOrdersPageCount(FilterData filterData) {
        List<SedoxOrder> orders = getOrdersInternal(filterData);
        if (orders.isEmpty()) {
            return 1;
        }
        
        return (int)Math.ceil((double)orders.size() / (double)filterData.pageSize);
    }

    private Predicate<? super SedoxProduct> getFilterProductByName(FilterData filterData) {
        return o -> productsShared.get(o.sharedProductId) != null 
                && productsShared.get(o.sharedProductId).getName().contains(filterData.filterText);
    }

    private Predicate<? super SedoxCreditHistory> getFilterText(FilterData filterData) {
        return o -> o.description.toLowerCase().contains(filterData.filterText.toLowerCase());
    }

    @Override
    public Long getUserFileUploadCount() {
        Stream<SedoxProduct> productStream = getProductsUploadedByCurrentUser();
        return productStream.count();
    }

    @Override
    public Long getUserFileDownloadCount() {
        return new Long(getSedoxUserAccount().orders.size());
    }

    @Override
    public List<SedoxFileHistory> getUploadHistory() {
        Stream<SedoxProduct> productStream = getProductsUploadedByCurrentUser();
        List<SedoxProduct> products = productStream.collect(Collectors.toList());
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MONTH, 1);
        
        List<SedoxFileHistory> hists = new ArrayList();
        for (int i=0;i<12;i++) {
            Date endDate = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            Date startDate = cal.getTime();
            long count = products.stream().filter(o -> o.rowCreatedDate.after(startDate) && o.rowCreatedDate.before(endDate)).count();
            
            SedoxFileHistory fileHistory = new SedoxFileHistory();
            fileHistory.count = count;
            fileHistory.month = cal.get(Calendar.MONTH) + 1;
            fileHistory.year = cal.get(Calendar.YEAR);
            hists.add(fileHistory);
            
        }
        
        return hists;
    }

    private SedoxUser getSlave(String slaveId) {
        
        String masterId = getSession().currentUser.id;
        boolean foundSlave = getSlaves(masterId)
                .stream()
                .anyMatch(o -> o.id.equals(slaveId));
        
        if (!foundSlave) {
            throw new ErrorException(26);
        }
        
        return getSedoxUserById(slaveId);
    }

    @Override
    public void setPushoverId(String pushover) {
        String id = getSession().currentUser.id;
        SedoxUser user = getSedoxUserAccountById(id);
        user.pushoverId = pushover;
        saveObject(user);
    }

    @Override
    public void setPushoverIdForUser(String pushover, String userId) {
        SedoxUser user = getSedoxUserAccountById(userId);
        user.pushoverId = pushover;
        saveObject(user);
    }

}

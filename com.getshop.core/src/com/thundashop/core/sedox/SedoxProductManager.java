/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.sedox.autocryptoapi.FilesMessage;
import com.thundashop.core.usermanager.IUserManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class SedoxProductManager extends ManagerBase implements ISedoxProductManager {

    @Autowired
    private SedoxSearchEngine sedoxSearchEngine;
    private List<SedoxProduct> products = new ArrayList();
    private Map<String, SedoxUser> users = new HashMap();

    @Autowired
    public SedoxAirgram sedoxAirgram;
    
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public SedoxProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Autowired
    public SedoxCMDEncrypter sedoxCMDEncrypter;
    
    @Autowired
    public SedoxMagentoIntegration sedoxMagentoIntegration;

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof SedoxProduct) {
                products.add((SedoxProduct) dataCommon);
            }
            if (dataCommon instanceof SedoxUser) {
                SedoxUser user = (SedoxUser) dataCommon;
                users.put(user.id, user);
            }
        }

        if (this.storeId.equals("608afafe-fd72-4924-aca7-9a8552bc6c81")) {
            sedoxMagentoIntegration.addOrderUpdateListener(this);
        }
    }

    @Override
    public synchronized SedoxProductSearchPage search(SedoxSearch search) {
        return sedoxSearchEngine.getSearchResult(products, search);
    }

    @Override
    public synchronized void sync(String option) throws ErrorException {
        if (!getStore().id.equals("608afafe-fd72-4924-aca7-9a8552bc6c81")) {
            throw new ErrorException(26);
        }
        
        if (option != null && option.equals("magento")) {
            updateAllUsers();
            return;
        }

        deleteAllProducts();
        deleteAllUsers();

        try {
            SedoxMysqlImporter productImporter = new SedoxMysqlImporter();
            for (SedoxProduct product : productImporter.getProducts()) {
                product.storeId = this.storeId;
                databaseSaver.saveObject(product, credentials);
                products.add(product);
            }

            List<SedoxUser> accounts = productImporter.getCreditAccounts();
            for (SedoxUser user : accounts) {
                saveUser(user);
            }
        } catch (ErrorException | SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        System.out.println("Sync done");
    }

    @Override
    public synchronized SedoxUser getSedoxUserAccount() throws ErrorException {
        String id = getSession().currentUser.id;
        updateUserFromMagento(id, false);
        SedoxUser user = getSedoxUserById(id);
        return user;
    }

    @Override
    public synchronized List<SedoxProduct> getProductsFirstUploadedByCurrentUser() throws ErrorException {
        Set<SedoxProduct> retProducts = new TreeSet();

        String userId = getSession().currentUser.id;
        for (SedoxProduct product : products) {
            if (product.firstUploadedByUserId != null && product.firstUploadedByUserId.equals(userId)) {
                retProducts.add(product);
            }
        }

        return new ArrayList(retProducts);
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

        for (SedoxProduct product : products) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(product.rowCreatedDate);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            if (dayOfYear == nowDayOfYear && nowYear == calendar.get(Calendar.YEAR)) {
                retProducts.add(product);
            }
        }

        Collections.sort(retProducts, new SortByDateSedoxProductComperable());

        return retProducts;
    }

    @Override
    public synchronized SedoxProduct getProductById(String id) throws ErrorException {
        for (SedoxProduct product : products) {
            if (product.id.equals(id)) {
                return product;
            }
        }

        return null;
    }

    @Override
    public synchronized void createSedoxProduct(SedoxProduct sedoxProduct, String base64EncodeString, String originalFileName, String forSlaveId) throws ErrorException {
        if (forSlaveId != null && !forSlaveId.equals("")) {
            checkIfMasterOfSlave(forSlaveId);
        }
        
        sedoxProduct.id = getNextProductId();
        sedoxProduct.firstUploadedByUserId = forSlaveId != null && !forSlaveId.equals("") ? forSlaveId : getSession().currentUser.id;
        sedoxProduct.storeId = storeId;
        sedoxProduct.rowCreatedDate = new Date();

        products.add(sedoxProduct);
        
        SedoxBinaryFile cmdEncryptedFile = saveCmdEncryptedFile(base64EncodeString, originalFileName);
        if (cmdEncryptedFile != null) {
            sedoxProduct.isCmdEncryptedProduct = true;
            sedoxProduct.saleAble = false;
            sedoxProduct.binaryFiles.add(cmdEncryptedFile);
        }
        
        SedoxBinaryFile originalFile = getOriginalBinaryFile(base64EncodeString, originalFileName);
        sedoxProduct.binaryFiles.add(originalFile);
        
        if (cmdEncryptedFile != null) {
            cmdEncryptedFile.cmdFileType = originalFile.cmdFileType;
            originalFile.cmdFileType = null;
        }

        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);
        sedoxProduct.softwareSize = fileData.length / 1024;

        databaseSaver.saveObject(sedoxProduct, credentials);
        sendFileCreatedEmail(sedoxProduct);
    }

    @Override
    public synchronized SedoxProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException {
        for (SedoxProduct product : products) {
            for (SedoxBinaryFile binaryFile : product.binaryFiles) {
                if (binaryFile.md5sum.equals(md5sum) && product.saleAble) {
                    return product;
                }
            }
        }

        return null;
    }

    @Override
    public synchronized void requestSpecialFile(String productId, String comment) throws ErrorException {
        System.out.println("Requesting special file for : " + productId + " Comment: " + comment);
    }

    @Override
    public synchronized void addFileToProduct(String base64EncodedFile, String fileName, String fileType, String productId) throws ErrorException {
        SedoxBinaryFile sedoxBinaryFile = getOriginalBinaryFile(base64EncodedFile, fileName);
        sedoxBinaryFile.id = getNextFileId();
        sedoxBinaryFile.fileType = fileType;
        sedoxBinaryFile.updateParametersFromFileName(fileName);

        SedoxProduct sedoxProduct = getProductById(productId);
        sedoxProduct.binaryFiles.add(sedoxBinaryFile);
        sedoxProduct.setParametersBasedOnFileString(fileName);
        databaseSaver.saveObject(sedoxProduct, credentials);
    }

    private void purchaseProductInternalForUser(String productId, SedoxUser user, List<Integer> files) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxOrder order = getOrder(product, files, user);
        
        if (order.creditAmount <= 0) {
            // Dont register if product is free.
            return;
        }
        
        checkCreditLimit(user, order.creditAmount);
        user.orders.add(order);
        int nextTransactionalId = getNextTransactionId();
        user.creditAccount.addOrderToCreditHistory(order, product, nextTransactionalId);
        databaseSaver.saveObject(user, credentials);
        users.put(user.id, user);
    }
    
    @Override
    public synchronized String purchaseProduct(String productId, List<Integer> files) throws ErrorException {
        SedoxUser user = users.get(getSession().currentUser.id);

        if (user == null) {
            throw new ErrorException(26);
        }

        purchaseProductInternalForUser(productId, user, files);
        
        SedoxProduct product = getProductById(productId);
        String base64ZipFile;
        try {
            base64ZipFile = getZipfileAsBase64(product, files);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ErrorException(1026);
        }

        return base64ZipFile;
    }

    private void updateUserFromMagento(String id, boolean force) throws ErrorException {
        UserManager userManager = getManager(IUserManager.class);
        User user = userManager.getUserById("" + id);
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
            databaseSaver.saveObject(sedoxUser, credentials);
            
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
    
    private byte[] getByteContentOfProduct(SedoxProduct product, List<Integer> files) throws IOException  {
        byte[] zippedContent;

        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(); ZipOutputStream out = new ZipOutputStream(byteOutputStream)) {
            for (Integer fileId : files) {
                SedoxBinaryFile binFile = product.getFileById(fileId);
                String filePathString = "/opt/files/" + binFile.md5sum;
                
                File f = new File(filePathString);
                if (!f.exists()) {
                    f.createNewFile();
                }
                
                try (FileInputStream in = new FileInputStream(f)) {
                    out.putNextEntry(new ZipEntry(binFile.fileType));

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

    private String getZipfileAsBase64(SedoxProduct product, List<Integer> files) throws IOException {
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

        double totalPrice = 40;
        double mostExpensive = 0;
        for (int fileId : files) {
            SedoxBinaryFile binFile = sedoxProduct.getFileById(fileId);
            if (binFile != null && binFile.getPrice() > mostExpensive) {
                mostExpensive = binFile.getPrice();
            }
        }

        totalPrice += mostExpensive;
        double alreadySpentOnProduct = getAlreadySpentOnProduct(sedoxProduct, user);
        totalPrice = totalPrice - alreadySpentOnProduct;
        
        SedoxOrder order = new SedoxOrder();
        order.productId = sedoxProduct.id;
        order.creditAmount = totalPrice;
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

    private int getNextFileId() {
        int i = 0;
        for (SedoxProduct sedoxProduct : products) {
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
            SedoxBinaryFile cmdEncryptedFile =  new SedoxBinaryFile();
            cmdEncryptedFile.fileType = "CmdEncrypted";
            cmdEncryptedFile.id = getNextFileId();
            cmdEncryptedFile.md5sum = getMd5Sum(fileData);
            cmdEncryptedFile.orgFilename = originalFileName+"-cmd-encrypted";
            
            try {
                FileOutputStream fw = new FileOutputStream("/opt/files/" + cmdEncryptedFile.md5sum);
                BufferedOutputStream bw = new BufferedOutputStream(fw);
                bw.write(fileData);
                bw.close();
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return cmdEncryptedFile;
        }
        
        return null;
    }

    private SedoxBinaryFile getOriginalBinaryFile(String base64EncodeString, String originalFileName) throws ErrorException {
        // Check if needs to be decrypted.
        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);
        
        SedoxBinaryFile originalFile = new SedoxBinaryFile();
        
        if (isCmdFile(fileData)) {
            fileData = doCMDEncryptedFile(fileData, originalFile);
        }
        
        originalFile.fileType = "Original";
        originalFile.id = getNextFileId();
        originalFile.md5sum = getMd5Sum(fileData);
        originalFile.orgFilename = originalFileName;

        try {
            FileOutputStream fw = new FileOutputStream("/opt/files/" + originalFile.md5sum);
            BufferedOutputStream bw = new BufferedOutputStream(fw);
            bw.write(fileData);
            bw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return originalFile;
    }

    private void sendFileCreatedEmail(SedoxProduct sedoxProduct) throws ErrorException {
        for (SedoxUser developer : getDevelopers()) {
            UserManager userManager = getManager(UserManager.class);
            User user = userManager.getUserById(developer.id);
            if (developer.isActiveDelevoper) {
                sendAirGramMessage(user.emailAddress, sedoxProduct);
                sendNotificationEmail("files@tuningfiles.com", sedoxProduct);
            }
        }
    }
    
    private void sendAirGramMessage(String emailAddress, SedoxProduct sedoxProduct) throws ErrorException {
        UserManager userManager = getManager(UserManager.class);
        User user = userManager.getUserById(sedoxProduct.firstUploadedByUserId);
        SedoxUser sedoxUser = getSedoxUserAccountById(user.id);
        
        String message = "File: " + sedoxProduct.toString();
        message += " | User: " + user.fullName;
        message += " | Credit: " + sedoxUser.creditAccount.getBalance();
        sedoxAirgram.sendMessage(emailAddress, message);
    }

    private String getNextProductId() {
        int lowest = 0;
        for (SedoxProduct sedoxProduct : products) {
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

    private void deleteAllProducts() throws ErrorException {
        for (SedoxProduct product : products) {
            database.delete(product, credentials);
        }
    }

    private void deleteAllUsers() throws ErrorException {
        for (SedoxUser user : users.values()) {
            database.delete(user, credentials);
        }

        IUserManager userManager = getManager(UserManager.class);
        List<User> users = userManager.getAllUsers();
        for (User user : users) {
            if (user.type >= 50) {
                continue;
            }

            userManager.deleteUser(user.id);
        }
    }

    private void saveUser(SedoxUser user) throws ErrorException {
        String tmpPassword = "abcd1234-56789ss!";

        IUserManager userManager = getManager(UserManager.class);
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
        databaseSaver.saveObject(user, credentials);
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
            System.out.println(i +"/"+users.size());
            try {
                updateUserFromMagento(user.id, true);
            } catch (ErrorException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<User> searchForUsers(String searchString) throws ErrorException {
        UserManager manager = getManager(IUserManager.class);
        
        Set<User> retUsers = new HashSet();
        
        for (String searchStringI : searchString.split(":")) {
            searchStringI = searchStringI.toLowerCase();
            for (User user : manager.getAllUsers()) {
                if (user.fullName != null && user.fullName.toLowerCase().contains(searchStringI)) {
                    retUsers.add(user);
                }
            }
        }
        
        return new ArrayList(retUsers);
    }

    @Override
    public SedoxUser getSedoxUserAccountById(String userid) throws ErrorException {
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
            databaseSaver.saveObject(user, credentials);
            users.put(user.id, user);
        }
    }

    @Override
    public User login(String emailAddress, String password) throws ErrorException {
        String loggedUserId = sedoxMagentoIntegration.login(emailAddress, password);
        
        if (loggedUserId != null) {
            UserManager userManager = getManager(IUserManager.class);
            return userManager.forceLogon(loggedUserId);
        } else {
            throw new ErrorException(13);
        }
    }

    @Override
    public void setChecksum(String productId, String checksum) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        product.originalChecksum = checksum;
        databaseSaver.saveObject(product, credentials);
    }

    @Override
    public void purchaseOnlyForCustomer(String productId, List<Integer> files) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxUser user = getSedoxUserById(product.firstUploadedByUserId);
        purchaseProductInternalForUser(productId, user, files);
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
        IUserManager manager = getManager(IUserManager.class);
        User getshopUser = manager.getUserById(id);
        if (getshopUser == null || getshopUser.emailAddress == null) {
            updateUserFromMagento(id, true);
        }
        return getshopUser;
    }
    @Override
    public void notifyForCustomer(String productId, String extraText) throws ErrorException {
        SedoxProduct product = getProductById(productId);
        SedoxUser user = getSedoxUserById(product.firstUploadedByUserId);
        User getshopUser = getGetshopUser(user.id);
        String content = getMailContent(extraText, productId);
        mailFactory.send("files@tuningfiles.com", getshopUser.emailAddress, product.toString(), content);
    }

    @Override
    public void sendProductByMail(String productId, String extraText, List<Integer> files) throws ErrorException {
        purchaseOnlyForCustomer(productId, files);
        
        
        SedoxProduct product = getProductById(productId);
        
        HashMap<String, String> fileMap = new HashMap();
        if (product.isCmdEncryptedProduct) {
            String fileName = encryptProductAndZipToTmpFolder(product, files);
            fileMap.put(fileName, product.toString()+".mod");    
        } else {
            String fileName = zipProductToTmpFolder(product, files);
            fileMap.put(fileName, product.toString()+".zip");
        }
        
        String content = getMailContent(extraText, productId);
        
        User getshopUser = getGetshopUser(product.firstUploadedByUserId);
        mailFactory.sendWithAttachments("files@tuningfiles.com", getshopUser.emailAddress, product.toString(), content, fileMap, true);
    }
    
    private String zipProductToTmpFolder(SedoxProduct sedoxProduct, List<Integer> files) {
        try {
            byte[] zippedContent = getByteContentOfProduct(sedoxProduct, files);
            
            String fileName = "/tmp/"+ UUID.randomUUID().toString();
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(zippedContent);
            fos.close();
            
            return fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private String getMailContent(String extraText, String productId) {
        String content = "Your file is ready! :)";
        if (!extraText.equals("")) {
            content += "<br>";
            content += "<br> Please note:";
            content += "<br> " + extraText.replace("\n", "<br/>");
        }
        content += "</br><br/>Link to product <a href='http://databank.tuningfiles.com/index.php?page=productview&productId=76972'>http://databank.tuningfiles.com/index.php?page=productview&productId="+productId+"</a>";
        content += getSignature();
        return content;
    }

    @Override
    public void changeDeveloperStatus(String userId, boolean disabled) throws ErrorException {
        SedoxUser user = getSedoxUserAccountById(userId);
        user.isActiveDelevoper = disabled;
        users.put(user.id, user);
        databaseSaver.saveObject(user, credentials);
    }
    
    @Override
    public List<SedoxUser> getDevelopers() throws ErrorException {
        UserManager userManager = getManager(UserManager.class);
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

    private void sendNotificationEmail(String emailAddress, SedoxProduct sedoxProduct) {
        List<Integer> files = new ArrayList();
        
        for (SedoxBinaryFile binFile : sedoxProduct.binaryFiles) {
            files.add(binFile.id);
        }
        
        String fileName = zipProductToTmpFolder(sedoxProduct, files);
        
        HashMap<String, String> fileMap = new HashMap();
        fileMap.put(fileName, sedoxProduct.toString()+".zip");
        
        String content = getMailContent(sedoxProduct.comment, sedoxProduct.id);
        mailFactory.sendWithAttachments("files@tuningfiles.com", emailAddress, "File needs to be processed", content, fileMap, true);
    }

    private double getAlreadySpentOnProduct(SedoxProduct sedoxProduct, SedoxUser user) {
        double spent = 0;
        
        for (SedoxOrder order : user.orders) {
            if (sedoxProduct.id != null && sedoxProduct.id.equals(""+order.productId)) {
                spent += order.creditAmount;
            }
        }
        
        return spent;
    }

    @Override
    public void removeBinaryFileFromProduct(String productId, int fileId) throws ErrorException {
        SedoxProduct sedoxProduct = getProductById(productId);
        
        if (sedoxProduct != null) {
            sedoxProduct.removeBinaryFile(fileId);
            databaseSaver.saveObject(sedoxProduct, credentials);
        }
    }

    @Override
    public void toggleAllowNegativeCredit(String userId, boolean allow) throws ErrorException {
        SedoxUser sedoxUser = getSedoxUserAccountById(userId);
        sedoxUser.creditAccount.allowNegativeCredit = allow;
        databaseSaver.saveObject(sedoxUser, credentials);
    }
    
    @Override
    public void toggleAllowWindowsApp(String userId, boolean allow) throws ErrorException {
        SedoxUser sedoxUser = getSedoxUserAccountById(userId);
        sedoxUser.canUseExternalProgram = allow;
        databaseSaver.saveObject(sedoxUser, credentials);
    }

    /**
     * Checks if the binary data contains the acscii chars CMD in beginning of array
     * 
     * @param fileData
     * @return 
     */
    private boolean isCmdFile(byte[] fileData) {
        if (fileData.length > 3 ) {
            String check = ""+fileData[0]+fileData[1]+fileData[2];;
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
            
            for (Integer sedoxBinaryFileId : files) {
                SedoxBinaryFile file = product.getFileById(sedoxBinaryFileId);
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
            String fileName = "/tmp/"+product.toString()+".mod";
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
        if (sedoxUser.masterUserId != null && !sedoxUser.masterUserId.equals("") &&  sedoxCreditOrder.amount > 0) {
            double creditToAdd = sedoxCreditOrder.amount/100 * sedoxUser.slaveIncome;
            if (creditToAdd > 0) {
                UserManager userManager = getManager(UserManager.class);
                User masterGetshopUser = userManager.getUserById(sedoxUser.id);
                SedoxCreditOrder kickbackCreditOrder = new SedoxCreditOrder();
                kickbackCreditOrder.amount = order.credit;
                kickbackCreditOrder.magentoOrderId = order.orderId;
                
                SedoxUser master = getSedoxUserAccountById(sedoxUser.masterUserId);
                master.addCreditOrderUpdate(kickbackCreditOrder, "Added "+kickbackCreditOrder.amount+" credit to your account, your partner " + masterGetshopUser.fullName + " placed order for " + order.credit + " credits");
                
                databaseSaver.saveObject(master, credentials);
            }
        }
    }
}
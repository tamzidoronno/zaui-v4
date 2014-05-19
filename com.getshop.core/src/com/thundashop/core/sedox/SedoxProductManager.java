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
import com.thundashop.core.usermanager.IUserManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public SedoxProductManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof SedoxProduct) {
                products.add((SedoxProduct)dataCommon);
            }
            if (dataCommon instanceof SedoxUser) {
                SedoxUser user = (SedoxUser)dataCommon;
                users.put(user.id, user);
            }
        }
    }
    
    @Override
    public SedoxProductSearchPage search(String searchString) {
        return sedoxSearchEngine.getSearchResult(products, searchString);
    }

    @Override
    public void sync() throws ErrorException {
        if (!getStore().id.equals("608afafe-fd72-4924-aca7-9a8552bc6c81")) {
            throw new ErrorException(26);
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
        } catch (ErrorException | SQLException | ClassNotFoundException ex ) {
            ex.printStackTrace();
        }
        
        System.out.println("Sync done");
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

    @Override
    public SedoxUser getSedoxUserAccount() throws ErrorException {
        String id = getSession().currentUser.id;
        SedoxUser user = users.get(id);
        
        if (user == null) {
            user = new SedoxUser();
            user.magentoId = id;
            saveUser(user);
        }
        return user;
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

    @Override
    public List<SedoxProduct> getProductsFirstUploadedByCurrentUser() throws ErrorException {
        List<SedoxProduct> retProducts = new ArrayList();
        
        String userId = getSession().currentUser.id;
        for (SedoxProduct product : products) {
            if (product.firstUploadedByUserId != null && product.firstUploadedByUserId.equals(userId)) {
                retProducts.add(product);
            }
        }
        
        return retProducts;
    }

    @Override
    public List<SedoxUser> getAllUsersWithNegativeCreditLimit() throws ErrorException {
        List<SedoxUser> retUsers = new ArrayList();
        
        for (SedoxUser user : users.values()) {
            if (user.creditAccount.balance < 0) {
                retUsers.add(user);
            }
        }
        
        return retUsers;
    }

    @Override
    public List<SedoxUser> getFileDevelopers() throws ErrorException {
        // TODO Return administrators.
        
        List<SedoxUser> developers = new ArrayList();
        return developers;
    }

    @Override
    public List<SedoxProduct> getProductsByDaysBack(int daysBack) throws ErrorException {
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
    public SedoxProduct getProductById(String id) throws ErrorException {
        for (SedoxProduct product : products) {
            if (product.id.equals(id)) {
                return product;
            }
        }
        
        return null;
    }

    private String getMd5Sum(byte[] data) throws ErrorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] array = digest.digest(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ErrorException(1024);
        }
    }
    
    @Override
    public void createSedoxProduct(SedoxProduct sedoxProduct, String base64EncodeString, String originalFileName) throws ErrorException {
        sedoxProduct.id = getNextProductId();
        sedoxProduct.firstUploadedByUserId = getSession().currentUser.id;
        sedoxProduct.storeId = storeId;
        sedoxProduct.rowCreatedDate = new Date();
        
        SedoxBinaryFile originalFile = getOriginalBinaryFile(base64EncodeString, originalFileName);
        sedoxProduct.binaryFiles.add(originalFile);
        
        databaseSaver.saveObject(sedoxProduct, credentials);
        products.add(sedoxProduct);
        sendFileCreatedEmail(sedoxProduct);
    }
    
    private SedoxBinaryFile getOriginalBinaryFile(String base64EncodeString, String originalFileName) throws ErrorException {
        // Check if needs to be decrypted.
        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);
        
        SedoxBinaryFile originalFile = new SedoxBinaryFile();
        originalFile.fileType = "Original";
        originalFile.md5sum = getMd5Sum(fileData);
        originalFile.orgFilename = originalFileName;
        return originalFile;
    }

    private void sendFileCreatedEmail(SedoxProduct sedoxProduct) {
        // TODO
        // NEED DEVELOPERS.
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
        return ""+lowest;
    }

    @Override
    public SedoxProduct getSedoxProductByMd5Sum(String md5sum) throws ErrorException {
        for (SedoxProduct product : products) {
            for (SedoxBinaryFile binaryFile : product.binaryFiles) {
                if (binaryFile.md5sum.equals(md5sum)) {
                    return product;
                }
            }
        }
        
        return null;
    }

    @Override
    public void requestSpecialFile(String productId, String comment) throws ErrorException {
        System.out.println("Requesting special file for : " + productId + " Comment: " + comment);
    }
}

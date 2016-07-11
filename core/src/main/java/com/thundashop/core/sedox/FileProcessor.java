/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.sedox.autocryptoapi.FilesMessage;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author ktonder
 */
public class FileProcessor extends GetShopSchedulerBase {
    private SedoxCMDEncrypter sedoxCMDEncrypter;
    private String type;
    private String fileType;
    private String fileName;
    private String base64EncodedFile;
    private String productId;
    private String reference;
    private SedoxBinaryFileOptions options;
    private String useCredit;
    private String comment;
    private String origin;
    private String forSlaveId;
    private String originalFileName;
    private String base64EncodeString;
    private SedoxSharedProduct sharedProduct;
    private String currentUserId;

    
    public FileProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    public FileProcessor(SedoxSharedProduct sharedProduct, 
            String base64EncodeString, 
            String originalFileName, 
            String forSlaveId, 
            String origin, 
            String comment, 
            String useCredit, 
            SedoxBinaryFileOptions options, 
            String reference, 
            SedoxCMDEncrypter sedoxCMDEncrypter,
            String currentUserId) {
        this.sedoxCMDEncrypter = sedoxCMDEncrypter;
        this.sharedProduct = sharedProduct;
        this.base64EncodeString = base64EncodeString;
        this.originalFileName = originalFileName;
        this.forSlaveId = forSlaveId;
        this.origin = origin;
        this.comment = comment;
        this.useCredit = useCredit;
        this.options = options;
        this.reference = reference;
        this.currentUserId = currentUserId;
        this.type = "createProduct";
    }
    
    public FileProcessor(String base64EncodedFile, String fileName, String fileType, String productId) {
        this.type = "addFile";
        this.base64EncodedFile = base64EncodedFile;
        this.fileName = fileName;
        this.fileType = fileType;
        this.productId = productId;
    }
    
    private SedoxBinaryFile getOriginalBinaryFile(String base64EncodeString, String originalFileName) throws ErrorException, Exception {
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
        originalFile.id = getApi().getSedoxProductManager().getNextFileId(); // API
        originalFile.md5sum = getMd5Sum(fileData);
        originalFile.orgFilename = originalFileName;

        writeFile(fileData);

        return originalFile;
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

    private SedoxBinaryFile saveCmdEncryptedFile(String base64EncodeString, String originalFileName) throws ErrorException, Exception {
        byte[] fileData = DatatypeConverter.parseBase64Binary(base64EncodeString);

        if (isCmdFile(fileData)) {
            SedoxBinaryFile cmdEncryptedFile = new SedoxBinaryFile();
            cmdEncryptedFile.fileType = "CmdEncrypted";
            cmdEncryptedFile.id =  getApi().getSedoxProductManager().getNextFileId();
            cmdEncryptedFile.md5sum = getMd5Sum(fileData);
            cmdEncryptedFile.orgFilename = originalFileName + "-cmd-encrypted";

            writeFile(fileData);

            return cmdEncryptedFile;
        }

        return null;
    }
    
    @Override
    public void execute() throws Exception {
        if (type.equals("addFile")) {
            addFile();
        }
        
        if (type.equals("createProduct")) {
            createProduct();
        }
    }

    private void addFile() throws Exception {
        SedoxBinaryFile sedoxBinaryFile = getOriginalBinaryFile(base64EncodedFile, fileName);
        getApi().getSedoxProductManager().addFileToProductAsync(sedoxBinaryFile, fileType, fileName, productId); // API
    }

    private void createProduct() throws Exception {
        SedoxBinaryFile originalFile = getOriginalBinaryFile(base64EncodeString, originalFileName);
        SedoxBinaryFile cmdEncryptedFile = saveCmdEncryptedFile(base64EncodeString, originalFileName);
        getApi().getSedoxProductManager().finishUpload(forSlaveId, sharedProduct, useCredit, comment, originalFile, cmdEncryptedFile, options, base64EncodeString, originalFileName, origin, currentUserId, reference); 
    }
    
}

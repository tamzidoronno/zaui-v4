/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.sedox.autocryptoapi.FilesMessage;
import com.thundashop.core.sedox.autocryptoapi.Webservice;
import com.thundashop.core.sedox.autocryptoapi.WebserviceServiceLocator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxCMDEncrypter {
    public FilesMessage decrypt(byte[] data) {
        try {
            WebserviceServiceLocator locator = new WebserviceServiceLocator();
            Webservice service = locator.getWebservicePort();

            String base64 = DatatypeConverter.printBase64Binary(data);

            FilesMessage fileMessage = new FilesMessage();
            fileMessage.setFile_encryptedFile(base64);

            return service.decryptFile(fileMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public byte[] decrypt(SedoxBinaryFile originalFile, SedoxBinaryFile tuningFile) throws ErrorException {
        try {
            WebserviceServiceLocator locator = new WebserviceServiceLocator();
            Webservice service = locator.getWebservicePort();

            FilesMessage fileMessage = new FilesMessage();
            String cmdEncryptedBase64 = getByteData(originalFile);
            String tuneBase64 = getByteData(tuningFile);

            fileMessage.setFile_encryptedFile(cmdEncryptedBase64);
            
            if (originalFile.cmdFileType.equals("29Bl28Fm58W")) {
                fileMessage.setFile_29Bl28Fm58W(tuneBase64);
            }
            if (originalFile.cmdFileType.equals("mpc5Xxflash")) {
                fileMessage.setFile_mpc5Xxflash(tuneBase64);
            }
            if (originalFile.cmdFileType.equals("seriale2Prom")) {
                fileMessage.setFile_seriale2Prom(tuneBase64);
            }

            FilesMessage message = service.encryptFile(fileMessage);
            if (message.getFile_encryptedFile() != null) {
                System.out.println("Decrypted file :D");
                return DatatypeConverter.parseBase64Binary(message.getFile_encryptedFile());
            }
            
            throw new ErrorException(2000002);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
        
    }

    private String getByteData(SedoxBinaryFile tuningFile) throws IOException {
        String tuneFilePathString = "/opt/files/" + tuningFile.md5sum;
        Path tunepath = Paths.get(tuneFilePathString);
        String tuneBase64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(tunepath));
        return tuneBase64;
    }   
}
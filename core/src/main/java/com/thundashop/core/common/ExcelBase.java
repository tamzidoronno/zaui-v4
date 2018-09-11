/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.thundashop.core.pdf.InvoiceManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ktonder
 */
public class ExcelBase {
    public String tmpFileName = "/tmp/"+UUID.randomUUID().toString()+".xlsx";
    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    public ExcelBase(String sheetName) {
        createSheet(sheetName);
    }
    
    
    private void createSheet(String sheetName) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
    }
    
    private void writeFile() {
        try {
            FileOutputStream out = new FileOutputStream(new File(tmpFileName));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void writeFile(String fileName) {
        try {
            FileOutputStream out = new FileOutputStream(new File(fileName));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getBase64Encoded() {
        writeFile();
        
        File file = new File(tmpFileName);
        
        byte[] bytes;
        try {
            bytes = InvoiceManager.loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);
            file.delete();
            return encodedString;
        } catch (IOException ex) {
            throw new ErrorException(1033);
        }
        
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author ktonder
 */
public class PdfKitchenNote {
    private final ProductManager productManager;
    private final TableSession session;
    private String generatedText = "=================";

    public PdfKitchenNote(TableSession session, ProductManager productManager) {
        this.session = session;
        this.productManager = productManager;      
    }
    
    public String createFile(String tableName, User user) throws IOException, COSVisitorException {
        writeHeader(tableName, user);
        writeContent(tableName);
        
        return generatedText;
    }
    

    private void writeText( String text, int x, int y, boolean bold, int fontSize, boolean alignRight) throws IOException {
        generatedText += text+"\\n";
    }
    
    private void writeText( String text, int x, int y, boolean bold, int fontSize) throws IOException {
        if (text == null) {
            generatedText += "\\n";
        }
        
        writeText(text, x, y, bold, fontSize, false);
    }

    private void writeContent(String tableName) throws IOException {
        writeText(tableName, 20, 820, false, 14);
        
        int i = 0;
        int lineHeight = 8;
        int padding = 3;
        int start = 800;
        
        for (int j=0; j<=getMaxPersonSize(); j++) {
            if (!hasAnyForPerson(j)) {
                continue;
            }
            
            start -= 10;
            int pos = start - (i*lineHeight) - (i*padding);
            String personText = j == 0 ? "Generel" : "Person " + j;
            writeText("\\n"+personText, 20, pos, false, lineHeight);
            
            i++;
            
            for (ResturantCartItem cartItem : session.getCartItems()) {
                if (cartItem.tablePersonNumber != j)
                    continue;
                
                pos = start - (i*lineHeight) - (i*padding);
                Product product = productManager.getProduct(cartItem.productId);
                if (!product.isFood) {
                    continue;
                }
                
                String productName = product.name; 
                if (cartItem.options.size() > 0) {
                    for (String key : cartItem.options.keySet()) {
                        TreeNode variation = product.variations.getNode(key);
                        productName += " ( " + variation.text + ": " + variation.getNode(cartItem.options.get(key)).text + " )";
                    }   
                }
                
                writeText(productName, 20, pos, false, lineHeight);
                i++;
            }
        }
    }

    private int getMaxPersonSize() {
        int i = 0;
        for (ResturantCartItem cartItem : session.getCartItems()) {
            if (cartItem.tablePersonNumber > i)
                i = cartItem.tablePersonNumber;
        }
        
        return i;
    }

    private boolean hasAnyForPerson(int j) {
        for (ResturantCartItem cartItem : session.getCartItems()) {
            if (cartItem.tablePersonNumber == j && productManager.getProduct(cartItem.productId).isFood)
                return true;
        }
        
        return false;
    }    

    private void writeHeader(String tableName, User user) throws IOException {
        SimpleDateFormat smf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String date = smf.format(new Date());
        
        writeText("\\nBor: " + tableName, 0, 0, true, 0);
        writeText("Dato: " + date, 0, 0, true, 0);
        if (user != null) {
            writeText("Servitør: " + user.fullName, 0,0,true,0);
        }
        
    }
}
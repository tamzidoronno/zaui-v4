/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.pdf.InvoiceGeneratorCartItem;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductList;
import com.thundashop.core.usermanager.data.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.pdfbox.exceptions.COSVisitorException;

/**
 *
 * @author ktonder
 */
public class PdfKitchenNote {
    private final ProductManager productManager;
    private final TableSession session;
    private String generatedText = "=================";
    private final List<CartItem> cartItems;

    public PdfKitchenNote(TableSession session, ProductManager productManager, List<CartItem> cartItems) {
        this.cartItems = cartItems;
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
                
        Map<String, List<CartItem>> groupedList = cartItems.stream()
            .collect(
                Collectors.groupingBy(e -> productManager.getFirstProductList(e.getProduct().id), Collectors.toList())
            );

        for (String listId : groupedList.keySet()) {
            ProductList list = productManager.getProductList(listId);
            
            if (list != null) {
                writeText("\\n"+list.listName+":", 20, 20, false, 20);
            } else {
                writeText("\\nUnkown:", 20, 20, false, 20);
            }
            
            groupedList.get(listId).sort((o1, o2) -> {
                return o1.getProduct().name.compareTo(o2.getProduct().name);
            });
            
            for (CartItem cartItem : groupedList.get(listId)) {
                Product product = cartItem.getProduct();
                
                if (!product.isFood) {
                    continue;
                }

                String productName = cartItem.getCount() + " x " + product.name; 
                if (cartItem.getVariations() != null && cartItem.getVariations().size() > 0) {
                    for (String key : cartItem.getVariations().keySet()) {
                        TreeNode variation = product.variations.getNode(key);
                        productName += " ( " + variation.text + ": " + variation.getNode(cartItem.getVariations().get(key)).text + " )";
                    }
                }

                writeText(productName, 20, 20, false, 20);
            }
        }
        
    }

    private void writeHeader(String tableName, User user) throws IOException {
        SimpleDateFormat smf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String date = smf.format(new Date());
        
        writeText("\\n", 0, 0, true, 0);
        writeText("\\n", 0, 0, true, 0);
        writeText("\\n", 0, 0, true, 0);
        writeText("\\n", 0, 0, true, 0);
        writeText("\\nBor: " + tableName, 0, 0, true, 0);
        writeText("Dato: " + date, 0, 0, true, 0);
        if (user != null) {
            writeText("Servitør: " + user.fullName, 0,0,true,0);
        }
        
    }
}
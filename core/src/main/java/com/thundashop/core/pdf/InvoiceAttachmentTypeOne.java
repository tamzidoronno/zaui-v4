/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.joda.time.DateTime;

/**
 *
 * @author ktonder
 */
public class InvoiceAttachmentTypeOne {
    private List<CartItem>  cartItems;
    private int cornerSize = 7;
    private PDPageContentStream attachment;
    private final AccountingDetails details;
    private final PDDocument document;

    public InvoiceAttachmentTypeOne(List<CartItem> cartItems, AccountingDetails details, PDDocument document) {
        this.cartItems = cartItems;
        this.details = details;
        this.document = document;
    }
    
    public void createInvoice() throws IOException, COSVisitorException {
        PDPage page2 = new PDPage(PDPage.PAGE_SIZE_A4);
        document.addPage(page2);
        attachment = new PDPageContentStream(document, page2);
        addDescriptions();
        attachment.close();
    }
    
    private void writeText( String text, int x, int y, boolean bold, int fontSize, boolean alignRight) throws IOException {
        
        PDPageContentStream stream = attachment;
        
        PDType1Font font = bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
        stream.beginText();
        stream.setFont(font, fontSize);
        if (alignRight) {
            x = x - (int) (font.getStringWidth(text) / 1000 * 9);
        }
        stream.moveTextPositionByAmount(x, y);
        stream.drawString(text);
        stream.endText();
    }
    
    private void writeText( String text, int x, int y, boolean bold, int fontSize) throws IOException {
        if (text == null) {
            text = "";
        }
        writeText(text, x, y, bold, fontSize, false);
    }

    private void addDescriptions() throws IOException {
        int lineHeight = 8;
        int i = 0;
        int start = 750;
        int padding = 3;
        
        attachment.setNonStrokingColor(new Color(0, 0, 0));
        attachment.drawLine(40, 760, 560, 760);
        writeText("BESKRIVELSE", 45, 765, true, 8);
        writeText("PRIS", 335, 765, true, 8);
        writeText("ANTALL", 385, 765, true, 8);
        writeText("MVA", 475, 765, true, 8);
        writeText("BELØP", 525, 765, true, 8);

        
        for (CartItem cartItem : cartItems) {
            int pos = start - (i*lineHeight) - (i*padding);
            double price = cartItem.getProduct().price;
            
            if (cartItem.getProduct().taxGroupObject != null) {
                price = price/((cartItem.getProduct().taxGroupObject.taxRate/100)+1);
                writeText(cartItem.getProduct().taxGroupObject.taxRate+"%", 498, pos, false, 8, true);
            }
            
            writeText(String.format("%.2f", price), 360, pos, false, 8, true);
            writeText(cartItem.getCount()+"", 385, pos, false, 8);
            
            writeText(String.format("%.2f", (cartItem.getCount() * cartItem.getProduct().price))+"", 560, pos, false, 8, true);
            
            String name = createLineText(cartItem);
            
            int linebreakchars = 80;
            if (name.length() > linebreakchars) {
                int rounds = name.length() / linebreakchars + 1;
                for (int j=0; j<rounds; j++) {
                    int end = (j+1)*linebreakchars;
                    if (end > name.length()) {
                        end = name.length();
                    }
                    String subString = name.substring(j*linebreakchars, end);
                    writeText(subString, 45, pos, false, 8);
                    i++;
                    pos = pos - lineHeight;
                } 
            } else {
                writeText(name, 45, pos, false, 8);
                i++;
            }
        }
    }
    
    private String createLineText(CartItem item) {
        if(item == null) {
            return "";
        }
        
        String lineText = "";
        String startDate = "";
        if(item.startDate != null) {
            DateTime start = new DateTime(item.startDate);
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.endDate != null) {
            DateTime end = new DateTime(item.endDate);
            endDate = end.toString("dd.MM.yy");
        }
        
        lineText = "";
        if(item.getProduct() != null && 
                item.getProduct().additionalMetaData != null && 
                !item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().additionalMetaData; 
        }
        
        lineText += " " + item.getProduct().name;
        
        if(item.getProduct() != null && item.getProduct().metaData != null && !item.getProduct().metaData.isEmpty()) {
            lineText += " " + item.getProduct().metaData;
        }
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
            lineText += " (" + startDate + " - " + endDate + ")";
        }
        
        return lineText;
    }

       
}

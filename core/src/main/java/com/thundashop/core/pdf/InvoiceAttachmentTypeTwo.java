/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

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
 * With this attachment page you get all the cartitems without displaying Taxes.
 *
 * @author ktonder
 */
public class InvoiceAttachmentTypeTwo {
    private List<InvoiceGeneratorCartItem>  cartItems;
    private int cornerSize = 7;
    private PDPageContentStream attachment;
    private final AccountingDetails details;
    private final PDDocument document;

    public InvoiceAttachmentTypeTwo(List<InvoiceGeneratorCartItem> cartItems, AccountingDetails details, PDDocument document) {
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
        writeText("PRIS", 425, 765, true, 8);
        writeText("ANTALL", 475, 765, true, 8);
        writeText("BELØP", 525, 765, true, 8);

        
        for (InvoiceGeneratorCartItem cartItem : cartItems) {
            int pos = start - (i*lineHeight) - (i*padding);
            double price = cartItem.getPrice();
            writeText(String.format("%.2f", price), 448, pos, false, 8, true);
            writeText(cartItem.getCount()+"", 475, pos, false, 8);
            
            writeText(String.format("%.2f", (cartItem.getTotal()))+"", 560, pos, false, 8, true);
            
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
    
    private String createLineText(InvoiceGeneratorCartItem item) {
        if(item == null) {
            return "";
        }
        
        String lineText = "";
        String startDate = "";
        if(item.getStartDate() != null) {
            DateTime start = new DateTime(item.getStartDate());
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.getEndDate() != null) {
            DateTime end = new DateTime(item.getEndDate());
            endDate = end.toString("dd.MM.yy");
        }
        
        lineText = "";
        if(!item.getMetaDataText().isEmpty()) {
            lineText = item.getMetaDataText(); 
        }
        
        lineText += " " + item.getName();
        
        if(!item.getProductMetaData().isEmpty()) {
            lineText += " " + item.getProductMetaData();
        }
        if(!startDate.isEmpty() && !endDate.isEmpty()) {
            lineText += " (" + startDate + " - " + endDate + ")";
        }
        
        return lineText;
    }

       
}

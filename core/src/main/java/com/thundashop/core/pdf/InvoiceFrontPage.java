/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.usermanager.data.Address;
import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class InvoiceFrontPage {
    
    private final Order order;
    private int cornerSize = 7;
    private PDPageContentStream contentStream;
    private final AccountingDetails details;
    private final boolean useAttachment;
    private final PDDocument document;
    
    public InvoiceFrontPage(Order order, AccountingDetails details, boolean useAttachment, PDDocument document) {
        this.order = order;
        this.details = details;
        this.useAttachment = useAttachment;
        this.document = document;
    }
    
    private void addInvoiceNote() throws IOException {
        writeText("Note", 45,413, true, 8);
        String[] lines = order.invoiceNote.split("\n");
        int i = 0;
        for(String line : lines) {
            if(line.isEmpty()) {
                continue;
            }
            writeText(line,  45,400-(i*10), false, 8);
            i++;
        }
    }
    
    public void createInvoice() throws IOException, COSVisitorException {
        PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
        
        document.addPage(page);

        contentStream = new PDPageContentStream(document, page);
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            addFooterLines();
            generateYellowLines(page);
        }
        addTexts();
        addOrderText();
        addSummary();
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            int startx = 33;
            contentStream.drawLine(20, startx, 20, 77);
            contentStream.drawLine(220, startx, 220, 77);

            contentStream.setStrokingColor(new Color(255, 246, 133));
            contentStream.drawLine(300, startx, 300, 77);
        }
        
        drawLines();
        writeText("OPPSUMMERING", 443, 420, true, 8);
        if(order.cart.getItems().size() <= 19) {
            addDescriptions();
        } else {
            writeText("Se vedlegg for detaljert informasjon", 40, 600, true, 16);
        }
        
        contentStream.close();

    }
    
    private void generateYellowLines(PDPage page) throws IOException {
        contentStream.setNonStrokingColor(new Color(255, 246, 133));
        
        // top yellow backgorund
        contentStream.fillRect(0, 295, page.getMediaBox().getWidth(), 60);
        
        // Line over prices backgrond
        contentStream.fillRect(0, 80, page.getMediaBox().getWidth(), 25);
        
        // Bottom backgorund yellow
        contentStream.fillRect(0, 20, page.getMediaBox().getWidth(), 12);
        
        // white boxes
        contentStream.setNonStrokingColor(new Color(255, 255, 255));
        contentStream.fillRect(220, 300, 100, 25);
        contentStream.fillRect(350, 300, 130, 25);
        
        // white boxes bottom
        for (int i = 0; i < 11; i++) {
            contentStream.fillRect(110+(i*18), 82, 15, 20);
        }
    }

    private void addFooterLines() throws IOException {
        int x = 10;
        int y = 110;
        drawCornerRightTop(contentStream, 270, 180);
        drawCornerLeftBottom(contentStream, 30, 110);
        drawCornerLeftTop(contentStream, 30, 180);
        drawCornerRightBottom(contentStream, 270, 110);
        
        drawCornerRightTop(contentStream, 570, 180);
        drawCornerLeftBottom(contentStream, 300, 110);
        drawCornerLeftTop(contentStream, 300, 180);
        drawCornerRightBottom(contentStream, 570, 110);
        
        drawCornerRightTop(contentStream, 570, 260);
        drawCornerLeftBottom(contentStream, 300, 190);
        drawCornerLeftTop(contentStream, 300, 260);
        drawCornerRightBottom(contentStream, 570, 190);
        
        
        drawCornerRightTop(contentStream, 570, 285);
        drawCornerLeftBottom(contentStream, 493, 270);
        drawCornerLeftTop(contentStream, 493, 285);
        drawCornerRightBottom(contentStream, 570, 270);
    }
    
    private void drawCornerLeftBottom(PDPageContentStream contentStream, int x, int y) throws IOException {
        contentStream.drawLine(x, y, x, y+cornerSize);
        contentStream.drawLine(x, y, x+cornerSize, y);
    }
    
    private void drawCornerRightTop(PDPageContentStream contentStream, int x, int y) throws IOException {
        contentStream.drawLine(x, y, x, y-cornerSize);
        contentStream.drawLine(x, y, x-cornerSize, y);
    }
    
    private void drawCornerLeftTop(PDPageContentStream contentStream, int x, int y) throws IOException {
        contentStream.drawLine(x, y, x+cornerSize, y);
        contentStream.drawLine(x, y, x, y-cornerSize);
    }
    
    private void drawCornerRightBottom(PDPageContentStream contentStream, int x, int y) throws IOException {
        contentStream.drawLine(x, y, x-cornerSize, y);
        contentStream.drawLine(x, y, x, y+cornerSize);
    }

    private void addTexts() throws IOException {
        String INVOICEORRECEIPT = order.status == Order.Status.PAYMENT_COMPLETED ? "KVITTERING" : "FAKTURA";
        contentStream.setNonStrokingColor(new Color(0, 0, 0));
        
        
        writeText(INVOICEORRECEIPT, 485, 810, true, 15);
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText(details.accountNumber, 40, 303, false, 12);
            writeText("Innbetalt til konto", 40, 320, true, 8);
            writeText("Øre", 310, 68, true, 8);
            writeText("Til konto", 400, 68, true, 8);
            writeText("Kroner", 230, 68, true, 8);
            writeText("Kundeidentifikasjon (KID)", 28, 68, true, 8);
            writeText("Betalt av", 40, 180, true, 8);
            writeText("Betalingsinformasjon", 40, 280, true, 8);
            writeText("Kvittering", 40, 333, false, 12);
            writeText("Betalerens kontonummer", 360, 328, true, 8);
            writeText("Betalings-", 450, 280, true, 8);
            writeText("frist", 450, 270, true, 8);
            writeText("Betalt til", 310, 180, true, 8);
            writeText("Beløp", 230, 328, true, 8);
        }
    }
    
    private void writeText( String text, int x, int y, boolean bold, int fontSize, boolean alignRight) throws IOException {
        PDPageContentStream stream = contentStream;

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

    private void addOrderText() throws IOException {
        int lineHeight = 13;
        int startTop = 790;
        // Name and address top
        if(order.cart == null) {
            order.cart = new Cart();
        }
        if(order.cart.address == null) {
            order.cart.address = new Address();
        }
        
        writeText(order.cart.address.fullName, 45, startTop, false, 11);
        writeText(order.cart.address.address, 45, startTop-lineHeight, false, 11);
        writeText(order.cart.address.postCode + " " + order.cart.address.city, 45, startTop-lineHeight*2, false, 11);
        
        // Name bottom
        lineHeight = 13;
        int topLine = 160;
        int fontSize = 11;
        int left = 40;
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText(order.cart.address.fullName, left, topLine, false, fontSize);
            writeText(order.cart.address.address, left, topLine-lineHeight, false, fontSize);
            writeText(order.cart.address.postCode + " " + order.cart.address.city, left, topLine-lineHeight*2, false, fontSize);
        }
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            // Price bottom
            Double total = order.cart.getTotal(true)+order.cart.getShippingCost();
            String totalString = String.format("%.2f", total);

            if(totalString.contains(".")) {
                writeText(totalString.split("\\.")[0], 230, 45, false, 12);
                writeText(totalString.split("\\.")[1], 310, 45, false, 12);
            } else if(totalString.contains(",")) {
                writeText(totalString.split(",")[0], 230, 45, false, 12);
                writeText(totalString.split(",")[1], 310, 45, false, 12);
            } else {
                writeText(totalString, 230, 45, false, 12);
                writeText("00", 310, 45, false, 12);
            }
            writeText(totalString, 230, 308, false, 12);
        }
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText(details.accountNumber, 400, 45, false, 12);
        }
        
        
        
        // Payment infomration
        lineHeight = 13;
        topLine = 250;
        fontSize = 11;
        left = 40;
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText("Fakturadato: " + order.getDateCreated().replace("/", "."), left, topLine, false, fontSize);
            writeText("Fakturanr: " + order.incrementOrderId, left, topLine-lineHeight, false, fontSize);
        }

        // Top company information
        lineHeight = 14;
        topLine = 790;
        fontSize = 11;
        left = 345;
        writeText(details.companyName, left, topLine, true, fontSize);
        writeText("Org.nr.: " + details.vatNumber, left, topLine-lineHeight, false, fontSize);
        writeText(details.address, left, topLine-lineHeight*2, false, fontSize);
        writeText(details.postCode + " " + details.city, left, topLine-lineHeight*3, false, fontSize);
        
        
        writeText(details.contactEmail, left, 725, false, 8);
        writeText(details.webAddress, left+120, 725, false, 8);
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText("Faktura dato: " + order.getDateCreated().split(" ")[0].replace("/", "."), left, 710, false, 8);
            writeText("Faktura nr: " + order.incrementOrderId, left+120, 710, false, 8);
            writeText("Forfallsdato: " + getDueDate(), left, 698, false, 8);
            writeText("Til konto: " + details.accountNumber, left+120, 698, false, 8);
        } else {
            writeText("Bestillingsdato: " + order.getDateCreated().split(" ")[0].replace("/", "."), left, 710, false, 8);
            writeText("Ordrenr: " + order.incrementOrderId, left+120, 710, false, 8);
        }
        
        // Betal til
        lineHeight = 13;
        topLine = 160;
        fontSize = 11;
        left = 310;
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            writeText(details.companyName, left, topLine, false, fontSize);
            writeText(details.address, left, topLine-lineHeight, false, fontSize);
            writeText(details.postCode + " " + details.city, left, topLine-lineHeight*2, false, fontSize);
        }
        
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            // Betalings frist
            writeText(getDueDate(), 499, 273, false, 12);   
        }
    }
    
    private String getDueDate() {
        if (order.status == Order.Status.PAYMENT_COMPLETED) {
            return "BETALT";
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(this.order.createdDate);
        if(order.dueDays != null) {
            c.add(Calendar.DATE, order.dueDays);
        } else {
            c.add(Calendar.DATE, details.dueDays);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(c.getTime());
    }

    private void drawLines() throws IOException {
        contentStream.setStrokingColor(Color.GRAY);
        contentStream.drawLine(340, 805, 560, 805);
        contentStream.drawLine(340, 720, 560, 720);
        contentStream.drawLine(443, 416, 560, 416);
    }
    
    private boolean addShippingLine(int start, int i, int lineHeight, int padding) throws IOException {
        if (order.cart.getShippingCost() > 0) {
            int pos = start - (i*lineHeight) - (i*padding);
            writeText("Forsendelse", 45, pos, false, 8);
            i = 1;

            double shippingCost = order.cart.getShippingCost();
            writeText("1", 385, pos, false, 8);
            for (CartTax cartTax : order.cart.getCartTaxes()) {
                if (cartTax.taxGroup.groupNumber == 0 && cartTax.sum > 0) {
                    writeText(cartTax.taxGroup.taxRate+"%", 498, pos, false, 8, true);
                    shippingCost = shippingCost - cartTax.sum;
                }
            }

            writeText(String.format("%.2f", shippingCost), 360, pos, false, 8, true);
            writeText(String.format("%.2f", order.cart.getShippingCost()), 560, pos, false, 8, true);
            return true;
        }
        
        return false;
    }

    private void addDescriptions() throws IOException {
        int lineHeight = 8;
        int i = 0;
        int start = 628;
        int padding = 3;
        addInvoiceNote();
        if (addShippingLine(start, i, lineHeight, padding)) {
            i = 1;
        }
        if(!useAttachment) {
            contentStream.setNonStrokingColor(new Color(0, 0, 0));
            contentStream.drawLine(40, 640, 560, 640);
            writeText("BESKRIVELSE", 45, 645, true, 8);
            writeText("PRIS", 335, 645, true, 8);
            writeText("ANTALL", 385, 645, true, 8);
            writeText("MVA", 475, 645, true, 8);
            writeText("OPPSUMMERING", 443, 420, true, 8);
        }
        
        
        for (CartItem cartItem : order.cart.getItems()) {
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

    private double getNetto() {
        double total = 0;
        for (CartItem cartItem : order.cart.getItems()) {
            if (cartItem.getProduct().taxGroupObject != null) {
                total += cartItem.getCount() * (cartItem.getProduct().price / ((cartItem.getProduct().taxGroupObject.taxRate/100) + 1));
            }
        }
        return total;
    }
    
    private void addSummary() throws IOException {
        writeText("Netto beløp", 443, 405, false, 9);
        writeText(String.format("%.2f", getNetto()), (int) 560, 405, false, 9, true);
        
        int i = 0;
        
        for (CartTax cartTax : order.cart.getCartTaxes()) {
            i++;
            writeText("Mva " + cartTax.taxGroup.taxRate + "%", 443, 404-(i*11), false, 9);
            String sum = String.format("%.2f", cartTax.sum);
            writeText(sum, (int) 560, 404-(i*11), false, 9, true);
        }
        
        i++;
        if (order.status == Order.Status.PAYMENT_COMPLETED) {
            writeText("Betalt", 443, 404-(i*11), false, 9);
        } else {
            writeText("Å Betale", 443, 404-(i*11), false, 9);
        }
        
        double total = order.cart.getShippingCost() + order.cart.getTotal(true);
        writeText(String.format("%.2f", (total)), (int) 560, 404-(i*11), false, 9, true);
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

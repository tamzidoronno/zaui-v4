/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author ktonder
 */
public class InvoiceGenerator {

    private final Order order;
    private final AccountingDetails details;

    public InvoiceGenerator(Order order, AccountingDetails details) {
        this.order = order;
        this.details = details;
    }

    public String createInvoice() throws IOException, COSVisitorException {
        PDDocument document = new PDDocument();

        InvoiceFrontPage frontPage = new InvoiceFrontPage(this.order, details, true, document);
        frontPage.createInvoice();

        if (details.isTypeOne()) {
            InvoiceAttachmentTypeOne attachment = new InvoiceAttachmentTypeOne(order.cart.getItems(), details, document);
            attachment.createInvoice();
        }

        if (details.isTypeTwo()) {
            new InvoiceAttachmentTypeTwoPages(document, order, details);
        }

        String fileName = "/tmp/" + order.id + ".pdf";
        document.save(fileName);
        document.close();

        return fileName;
    }
    
}

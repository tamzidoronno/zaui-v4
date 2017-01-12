/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import java.io.IOException;
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
        
        boolean showAttachments = order.cart.getItems().size() > 19;

        InvoiceFrontPage frontPage = new InvoiceFrontPage(this.order, details, true, document);
        frontPage.createInvoice();

        if (details.isTypeOne() && showAttachments) {
            InvoiceAttachmentTypeOne attachment = new InvoiceAttachmentTypeOne(order.cart.getItems(), details, document);
            attachment.createInvoice();
        }

        if (details.isTypeTwo() && showAttachments) {
            new InvoiceAttachmentTypeTwoPages(document, order, details);
        }

        String fileName = "/tmp/" + order.id + ".pdf";
        document.save(fileName);
        document.close();

        return fileName;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

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
public class InvoiceAttachmentTypeTwoPages  {

    private final Order order;
    private final AccountingDetails details;

    InvoiceAttachmentTypeTwoPages(PDDocument document, Order order, AccountingDetails details) throws IOException, COSVisitorException {
        this.order = order;
        this.details = details;
        generateAttachment(document);
    }
    
    
    private void generateAttachment(PDDocument document) throws IOException, COSVisitorException {
        List<InvoiceGeneratorCartItem> items = order.cart.getItems()
                .stream()
                .map(item -> new InvoiceGeneratorCartItem(item))
                .collect(Collectors.toList());
        
        items = groupedItems(items);

        int pageSize = 60;
        int ret = items.size() % pageSize;

        for (int i = 0; i < ret; i++) {
            int start = i * pageSize;
            int end = (i + 1) * pageSize;
            int max = items.size();

            if (end > max) {
                end = max;
            }

            if (start > max) {
                break;
            }

            List<InvoiceGeneratorCartItem> subList = items.subList(start, end);

            InvoiceAttachmentTypeTwo attachment = new InvoiceAttachmentTypeTwo(subList, details, document);
            attachment.createInvoice();
        }
    }

  

    private List<InvoiceGeneratorCartItem> groupedItems(List<InvoiceGeneratorCartItem> items) {
        int i = 0;
        for (InvoiceGeneratorCartItem item : items) {
            i++;
            item.sequence = i;
        }
        
        Map<String, List<InvoiceGeneratorCartItem>> result = items.stream()
            .collect(
                Collectors.groupingBy(e -> e.getGroupId(), Collectors.toList())
            );

        List<InvoiceGeneratorCartItem> grouped = new ArrayList();
        for (String groupById : result.keySet()) {
            
            List<InvoiceGeneratorCartItem> groupedItems = result.get(groupById);
            InvoiceGeneratorCartItem cartItem = mergList(groupedItems);
            grouped.add(cartItem);
        }
        
        grouped = grouped.stream().sorted((o1,o2) -> {
            return o1.sequence.compareTo(o2.sequence);
        }).collect(Collectors.toList());

        return grouped;

    }

    private InvoiceGeneratorCartItem mergList(List<InvoiceGeneratorCartItem> groupedItems) {
        InvoiceGeneratorCartItem firstItem = groupedItems.get(0);
       
        if (groupedItems.size() == 1)
            return firstItem;
       
        InvoiceGeneratorCartItem mergedItem = new InvoiceGeneratorCartItem(firstItem);
        
        boolean first = true;
        for (InvoiceGeneratorCartItem item : groupedItems) {
            if (first) {
                first = false;
                continue;
            }
            
            mergedItem.mergeWith(item);
        }
        
        return mergedItem;
    }
    
}

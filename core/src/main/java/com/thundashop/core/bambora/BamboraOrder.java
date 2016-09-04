package com.thundashop.core.bambora;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

public class BamboraOrder implements Serializable {
    public BamboraAddress billingaddress;
    public BamboraAddress shippingaddress;
    public BamboraUrls url;
    
    public String currency = "";
    public List<BamboraOrderLine> lines = new ArrayList();
    public String ordernumber;
    public long total;
    public long vatamount;

    void setOrder(Order orderToAdd) {
        int lineNumber = 0;
        ordernumber = orderToAdd.incrementOrderId + "";
        currency = "NOK";
        
        total = 0;
        for(CartItem item : orderToAdd.cart.getItems()) {
            BamboraOrderLine line = new BamboraOrderLine();
            line.id = item.getCartItemId();
            line.linenumber = lineNumber + "";
            line.quantity = item.getCount();
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
            String text = item.getProduct().name + " " + item.getProduct().metaData + " (" + startDate + " - " + endDate + ")";
            line.text = text;
            line.unitprice = new Double(item.getProduct().priceExTaxes * 100).intValue();
            line.unitpriceinclvat = new Double(item.getProduct().price * 100).intValue();
            line.unitpricevatamount = line.unitpriceinclvat - line.unitprice;
            
            line.totalprice = line.unitprice * (int)line.quantity;
            line.totalpriceinclvat = line.unitpriceinclvat * (int)line.quantity;
            line.totalpricevatamount = line.unitpricevatamount * (int)line.quantity;
            
            line.vat = item.getProduct().taxGroupObject.taxRate.intValue();
            line.unit = "Number of rooms";
            lines.add(line);
            lineNumber++;
        }
    }

    
}

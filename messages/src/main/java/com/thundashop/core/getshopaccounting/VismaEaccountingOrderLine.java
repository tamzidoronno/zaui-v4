
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.cartmanager.data.CartItem;
import java.text.DecimalFormat;

public class VismaEaccountingOrderLine {
    public Integer LineNumber;
    public String ArticleNumber;
    public String Text;
    public Double UnitPrice;
    public Integer Quantity;
    public Boolean IsTextRow = true;
    public Boolean IsWorkCost = false;
    public Boolean EligibleForReverseChargeOnVat = false;

    VismaEaccountingOrderLine(CartItem item, int lineNumber, String text, String articleNumber) {
        DecimalFormat df = new DecimalFormat("#.##"); 
        UnitPrice = round(item.getProduct().priceExTaxes,2);
        Quantity = item.getCount();
        Text = text;
        this.LineNumber = lineNumber;
        this.ArticleNumber = articleNumber;
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

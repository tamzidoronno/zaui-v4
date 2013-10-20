/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.Session;
import com.thundashop.core.common.StoreHandler;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceGenerator;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.usermanager.data.User;
import java.io.IOException;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public class GenerateInvoiceTest {

    public static void main(String args[]) throws ErrorException, IOException, COSVisitorException {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        Logger log = context.getBean(Logger.class);
        
        StorePool storePool = new StorePool();
        AppContext.storePool = storePool;

        StoreHandler storeHandler = AppContext.storePool.getStorePool("c4f1cd2b-bc04-44c3-90b1-bdc4cd165577");
        OrderManager orderManager = storeHandler.getManager(OrderManager.class);
        orderManager.session = new Session();
        orderManager.getSession().currentUser = new User();
        orderManager.getSession().currentUser.type = 100;
        Order order = orderManager.getOrder("4dbbc71d-b8d4-41a4-a775-99d6aee8ceda");
        InvoiceGenerator generator = new InvoiceGenerator(order, new AccountingDetails());
        generator.createInvoice();
        System.exit(1);
    }
}

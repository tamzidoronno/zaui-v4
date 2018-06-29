package com.thundashop.core.verifonemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.SettingsRow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import no.point.paypoint.PayPoint;
import no.point.paypoint.PayPointEvent;
import no.point.paypoint.PayPointListener;
import no.point.paypoint.PayPointResultEvent;
import no.point.paypoint.PayPointStatusEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class VerifoneManager extends ManagerBase implements IVerifoneManager {

    @Autowired
    OrderManager orderManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    private Order orderToPay;

    private VerifoneTerminalListener verifoneListener;

    @Autowired
    public WebSocketServerImpl webSocketServer;

    @Autowired
    public FrameworkConfig frameworkConfig;
    
    HashMap<String, VerifonePaymentApp> activePaymentApps = new HashMap();
    
    @Override
    public void chargeOrder(String orderId, String terminalId) {
        if(orderToPay != null) {
            //Only one order at a time.
            printFeedBack("A payment is already being processed");
            return;
        }
        Order order = orderManager.getOrderSecure(orderId);
        order.payment.paymentType = "ns_6dfcf735_238f_44e1_9086_b2d9bb4fdff2\\VerifoneTerminal";
        orderManager.saveOrder(order);
        System.out.println("Start charging: " + order.payment.paymentType);
        this.orderToPay = order;

        if(!storeManager.isProductMode()) {
            order.status = Order.Status.PAYMENT_COMPLETED;
            saveOrderSomeHow(orderToPay);
            orderToPay = null;
            return;
        }

        Double total = orderManager.getTotalAmount(order) * 100;
        Integer amount = total.intValue();
        
        VerifonePaymentApp app = null;
        if(!activePaymentApps.containsKey(terminalId)) {
            app = new VerifonePaymentApp();
            if (verifoneListener == null) {
                createListener();
            }

            activePaymentApps.put(terminalId, app);
        } else {
            app = activePaymentApps.get(terminalId);
        }
        
        List<Application> activePaymentMethods = storeApplicationPool.getActivatedPaymentApplications();
        Application selectedApp = null;
        for(Application tmpApp : activePaymentMethods) {
            if(tmpApp.id.equals("6dfcf735-238f-44e1-9086-b2d9bb4fdff2")) {
                selectedApp = tmpApp;
            }
        }
        
        String ip = "192.168.1.107";
        if(selectedApp != null) {
            for(String key : selectedApp.settings.keySet()) {
                if(key.equals("ipaddr" + terminalId)) {
                    ip = selectedApp.settings.get(key).value;
                }
            }
        }        
        
        app.openCom(ip, verifoneListener);
        app.performTransaction(PayPoint.TRANS_CARD_PURCHASE, amount, amount);
    }

    public void getPayPointEvent(PayPointEvent event) {
        switch(event.getEventType()) {
        case PayPointEvent.STATUS_EVENT:
            PayPointStatusEvent statusEvent = (PayPointStatusEvent)event;
            if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_DISPLAY) {
                printFeedBack(statusEvent.getStatusData());
            } else if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_CARD_INFO) {
                printFeedBack("UNKNOWN STATUS (carddata) :" + statusEvent.getStatusData());
            } else if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_READY_FOR_TRANS){
                printFeedBack("Status readyfor trans:" + statusEvent.getStatusData());
                if(statusEvent.getStatusData().compareTo("1")==0) {
                    printFeedBack("Terminal ready for trans\n");
                } else {
                    printFeedBack("Terminal not ready for trans\n");
                }
            }
                break;
        case PayPointEvent.RESULT_EVENT:
                PayPointResultEvent resultEvent = (PayPointResultEvent)event;
                String resultText = "Result:        " + 
                        resultEvent.getResult() + "\n" +
                        "Accumulator:   " + resultEvent.getAccumulator() + "\n" +
                        "Issuer:        " + resultEvent.getIssuerId();
                if(resultEvent.getLocalModeData().compareTo("")!=0){
                        resultText = resultText + "\n" + 
                            "Data:          " + resultEvent.getLocalModeData();
                } 
                if(resultEvent.getResult()==0x20 && resultEvent.getAccumulator()==0x30){
                        if(resultEvent.getReportHeader().compareTo("")!=0){
                            resultText = resultText + "\n" +
                            "Header:        " + resultEvent.getReportHeader();
                        }
                        if(resultEvent.isReportDataAvaliable()){
                            resultText = resultText + "\n" +
                            "Report data:   " + resultEvent.getReportData();
                        }
                        if(resultEvent.isExtendedReportDataAvaliable()){
                            resultText = resultText + "\n" +
                            "Extended data: " + resultEvent.getExtendedReportData();
                        }								
                }
                addToOrder(resultText);
                handleResultEvent(resultEvent);
                break;
        } 
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Action performed: " + e);
    }

    private void printFeedBack(String string) {
        VerifoneFeedback feedBack = new VerifoneFeedback();
        feedBack.msg = string;
        webSocketServer.sendMessage(feedBack);
        orderToPay.payment.transactionLog.put(System.currentTimeMillis(), string);
        System.out.println("\t" + string);
        
    }

    private void addToOrder(String resultText) {
        orderToPay.payment.transactionLog.put(System.currentTimeMillis()-1, resultText);
        saveOrderSomeHow(orderToPay);
    }

    private void handleResultEvent(PayPointResultEvent resultEvent) {
        System.out.println("Result event: " + resultEvent);
        if(resultEvent.getResult() == 32) {
            System.out.println("Card succesfully paid");
            printFeedBack("completed");
            orderManager.markAsPaid(orderToPay.id, new Date(), orderToPay.getTotalAmount());
        } else {
            System.out.println("Failed to pay");
            orderToPay.status = Order.Status.PAYMENT_FAILED;
            printFeedBack("payment failed");
        }
        saveOrderSomeHow(orderToPay);
        orderToPay = null;
    }
    
    private void saveOrderSomeHow(Order orderToPay) {
        System.out.println("############ NEED TO SAVE THIS ORDER HOWEVER WE HAVE LOST THE ORDERMANAGER #################");
        orderManager.saveOrderInternal(orderToPay);
    }

    private void createListener() {
        this.verifoneListener = new VerifoneTerminalListener(storeId, null, null, this);
    }

    @Override
    public void cancelPaymentProcess(String terminalId) {
        VerifonePaymentApp app = activePaymentApps.get(terminalId);
        app.closeCom();
    }
    
}

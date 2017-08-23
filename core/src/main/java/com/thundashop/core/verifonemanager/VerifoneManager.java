package com.thundashop.core.verifonemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.socket.WebSocketServerImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    private Order orderToPay;

    private VerifoneTerminalListener verifoneListener;

    @Autowired
    public WebSocketServerImpl webSocketServer;

    @Override
    public void chargeOrder(String orderId, Integer terminalNumber) {
        if(orderToPay != null) {
            //Only one order at a time.
            printFeedBack("A payment is already being processed");
            return;
        }
        
        System.out.println("Start charging");
        Order order = orderManager.getOrderSecure(orderId);
        this.orderToPay = order;
        Double total = orderManager.getTotalAmount(order)* 100;
        Integer amount = total.intValue();
        
        VerifonePaymentApp app = new VerifonePaymentApp();
        if (verifoneListener == null) {
            createListener();
        }
        app.openCom("192.168.1.107", verifoneListener);
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
            orderToPay.status = Order.Status.PAYMENT_COMPLETED;
        } else {
            System.out.println("Failed to pay");
            orderToPay.status = Order.Status.PAYMENT_FAILED;
        }
        printFeedBack("completed");
        saveOrderSomeHow(orderToPay);
        orderToPay = null;
    }

    private void saveOrderSomeHow(Order orderToPay) {
        System.out.println("############ NEED TO SAVE THIS ORDER HOWEVER WE HAVE LOST THE ORDERMANAGER #################");
    }

    private void createListener() {
        this.verifoneListener = new VerifoneTerminalListener(storeId, null, null, this);
    }
    
}

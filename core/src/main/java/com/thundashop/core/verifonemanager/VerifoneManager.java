package com.thundashop.core.verifonemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.socket.WebSocketServerImpl;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.SettingsRow;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    
    @Autowired
    UserManager userManager;
    
    private Order orderToPay;

    private VerifoneTerminalListener verifoneListener;

    @Autowired
    public FrameworkConfig frameworkConfig;
    
    HashMap<String, VerifonePaymentApp> activePaymentApps = new HashMap();
    
    private List<String> terminalMessages = new ArrayList();
    
    @Override
    public void chargeOrder(String orderId, String terminalId, boolean overrideDevMode) {
        if(orderToPay != null) {
            //Only one order at a time.
            printFeedBack("A payment is already being processed");
            return;
        }
        
        clearMessages();
        
        printFeedBack("Starting payment process");
        Order order = orderManager.getOrderSecure(orderId);
        
        if (order.isFullyPaid() || order.status == Order.Status.PAYMENT_COMPLETED) {
            printFeedBack("completed");
            return;
        }
        
        order.payment.paymentType = "ns_6dfcf735_238f_44e1_9086_b2d9bb4fdff2\\VerifoneTerminal";
        orderManager.saveOrder(order);
        logPrint("Start charging: " + order.payment.paymentType);
        this.orderToPay = order;

        Double total = orderManager.getTotalAmount(order) * 100;
        Integer amount = total.intValue();
        
        VerifonePaymentApp app = getPayPoint(terminalId);
        
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
        
        Double cashWithdrawal = order.cashWithdrawal * 100;
        
        amount = amount + cashWithdrawal.intValue();
        app.openCom(ip, verifoneListener);
        
        if (amount < 0) {
            amount = amount * -1;
            app.performTransaction(PayPoint.TRANS_RETURN_GOODS, amount, amount);
        } else {
            app.performTransaction(PayPoint.TRANS_CARD_PURCHASE, amount, amount);
        }
        
    }

    private VerifonePaymentApp getPayPoint(String terminalId) {
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
        return app;
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
        logPrint("Action performed: " + e);
    }

    private void printFeedBack(String string) {
        VerifoneFeedback feedBack = new VerifoneFeedback();
        feedBack.msg = string;
        terminalMessages.add(string);
        if(orderToPay != null && orderToPay.payment != null) {
            orderToPay.payment.transactionLog.put(System.currentTimeMillis(), string);
        }
        logPrint("\t" + string);
        
    }

    private void addToOrder(String resultText) {
        if(orderToPay != null && orderToPay.payment != null) {
            orderToPay.payment.transactionLog.put(System.currentTimeMillis()-1, resultText);
            saveOrderSomeHow(orderToPay);
        }
    }

    private void handleResultEvent(PayPointResultEvent resultEvent) {
        logPrint("Result event: " + resultEvent);
        if(resultEvent.getResult() == 32) {
            logPrint("Card succesfully paid");
            printFeedBack("completed");
            if(orderToPay != null) {
                logPrint("Found order to pay: " + orderToPay.id);
                double paidAmount = orderToPay.getTotalAmount() + orderToPay.cashWithdrawal;
                logPrint("Total amount to mark as paid: " + paidAmount);
                try {
                    try {
                        boolean startedImpersonation = false;
                        
                        // Start impersonation
                        if (getSession().currentUser == null) {
                            User user = userManager.getInternalApiUser();
                            userManager.startImpersonationUnsecure(user.id);
                            getSession().currentUser = user;
                            startedImpersonation = true;
                        }
                        
                        orderManager.markAsPaid(orderToPay.id, new Date(), paidAmount);
                        
                        // Stop impersonation
                        if (startedImpersonation) {
                            userManager.cancelImpersonating();
                            getSession().currentUser = null;
                        }
                        
                    }catch(Exception a) {
                        logPrint("Exception occured: " + a.getMessage());
                        logPrintException(a);
                    }
                }catch(ErrorException b) {
                    logPrint("Error exception occured: " + b.getMessage());
                }
                logPrint("Marked order " + orderToPay.incrementOrderId + " as completed with paid amount: " + paidAmount);
            } else {
                logPrint("Warning! Order is null, cant mark as completed... Why is it null?");
            }
        } else {
            logPrint("Failed to pay");
            if(orderToPay != null && orderToPay.status != Order.Status.PAYMENT_COMPLETED) {
                orderToPay.status = Order.Status.PAYMENT_FAILED;
            }
            printFeedBack("payment failed");
        }
        saveOrderSomeHow(orderToPay);
        orderToPay = null;
    }
    
    private void saveOrderSomeHow(Order orderToPay) {
        if(orderToPay!= null) {
            orderManager.saveOrderInternal(orderToPay);
        }
    }

    private void createListener() {
        this.verifoneListener = new VerifoneTerminalListener(storeId, null, null, this);
    }

    @Override
    public void cancelPaymentProcess(String terminalId) {
        printFeedBack("payment failed");
        VerifonePaymentApp app = activePaymentApps.get(terminalId);
        app.closeCom();
        orderToPay = null;
    }

    @Override
    public List<String> getTerminalMessages() {
        
        for (String msg : terminalMessages) {
            if (msg != null && msg.equals("completed") && orderToPay != null && orderToPay.status != Order.Status.PAYMENT_COMPLETED) {
                markOrderInProgressAsPaid();
            }
        }
        
        return terminalMessages;
    }

    public void markOrderInProgressAsPaid() {
        double paidAmount = orderToPay.getTotalAmount() + orderToPay.cashWithdrawal;
        orderManager.markAsPaid(orderToPay.id, new Date(), paidAmount);
    }

    @Override
    public void clearMessages() {
        terminalMessages.clear();
    }

    @Override
    public Boolean isPaymentInProgress() {
        return orderToPay != null;
    }

    @Override
    public String getCurrentPaymentOrderId() {
        if (orderToPay != null) {
            return orderToPay.id;
        }
        
        return null;
    }

    public void removeOrderToPay() {
        orderToPay = null;
    }

    @Override
    public void doZreport(String terminalId) {
        VerifonePaymentApp paypoint = getPayPoint(terminalId);
        paypoint.performAdmin(PayPoint.ADM_Z_REPORT, true);
    }

    @Override
    public void doXreport(String terminalId) {
        VerifonePaymentApp paypoint = getPayPoint(terminalId);
        paypoint.performAdmin(PayPoint.ADM_X_REPORT, true);
    }

}

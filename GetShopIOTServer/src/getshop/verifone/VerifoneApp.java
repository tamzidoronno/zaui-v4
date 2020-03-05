/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshop.verifone;

import com.thundashop.core.gsd.TerminalResponse;
import getshop.nets.GetShopNetsApp;
import static getshop.nets.GetShopNetsApp.RESULT_ADMINISTRATIVE_TRANSACTION_OK;
import static getshop.nets.GetShopNetsApp.RESULT_FINANCIAL_TRANSACTION_OK;
import static getshop.nets.GetShopNetsApp.RESULT_TRANSACTION_IS_LOYALTY;
import getshopiotserver.GetShopIOTOperator;
import getshopiotserver.PaymentOperator;
import java.awt.event.ActionEvent;
import java.util.Date;
import no.point.paypoint.PayPoint;
import no.point.paypoint.PayPointEvent;
import no.point.paypoint.PayPointResultEvent;
import no.point.paypoint.PayPointStatusEvent;

/**
 *
 * @author boggi
 */
public class VerifoneApp implements PaymentOperator {

    private final GetShopIOTOperator operator;
    private boolean isInited = false;
    private VerifoneTerminalListener verifoneListener;
    private final VerifonePaymentApp verifonePaymentApp;
    private String orderId;
    
    public VerifoneApp(GetShopIOTOperator operator) {
        this.operator = operator;
        this.verifonePaymentApp = new VerifonePaymentApp();
    }

    @Override
    public void initialize() {
        System.out.println("Initing verifone");
        isInited = true;
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stopping verifone");
    }

    @Override
    public void startTransaction(Integer amount, String orderId) {
        System.out.println("starting transaction (verifone) amount : " + (amount/100));
        createListener();
        String ipAddr = operator.getSetupMessage().paymentterminalip;
        if(ipAddr == null || ipAddr.isEmpty()) {
            logPrint("Ip address not set in configuration object, please fill the paymentterminalip field");
            return;
        }
        verifonePaymentApp.openCom(ipAddr, verifoneListener);
        
        if (amount < 0) {
            amount = amount * -1;
            verifonePaymentApp.performTransaction(PayPoint.TRANS_RETURN_GOODS, amount, amount);
        } else {
            verifonePaymentApp.performTransaction(PayPoint.TRANS_CARD_PURCHASE, amount, amount);
        }
        this.orderId = orderId;
    }
    
    private void createListener() {
        if(verifoneListener == null) {
            this.verifoneListener = new VerifoneTerminalListener(this);
        }
    }

    @Override
    public void cancelTransaction() {
        if(verifonePaymentApp != null) {
            System.out.println("cancel verifone");
            verifonePaymentApp.closeCom();
        }
    }

    @Override
    public boolean isInitialized() {
        return isInited;
    }

    void actionPerformed(ActionEvent e) {
        System.out.println("Action performed: " + e);
        printToScreen("Action performed: " + e);
    }

    void getPayPointEvent(PayPointEvent event) {
        System.out.println("Get paypoint event: " + event);
        printToScreen("Get paypoint event: " + event);
        
         switch(event.getEventType()) {
        case PayPointEvent.STATUS_EVENT:
            PayPointStatusEvent statusEvent = (PayPointStatusEvent)event;
            if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_DISPLAY) {
                printToScreen(statusEvent.getStatusData());
            } else if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_CARD_INFO) {
                printToScreen("UNKNOWN STATUS (carddata) :" + statusEvent.getStatusData());
            } else if(statusEvent.getStatusType()==PayPointStatusEvent.STATUS_READY_FOR_TRANS){
                printToScreen("Status readyfor trans:" + statusEvent.getStatusData());
                if(statusEvent.getStatusData().compareTo("1")==0) {
                    printToScreen("Terminal ready for trans\n");
                } else {
                    printToScreen("Terminal not ready for trans\n");
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
                
                handleResultEvent(resultEvent);
                break;
        } 
    }
    
    private void logPrint(String text) {
        System.out.println(new Date() + " : " + text);
    }

    
    private TerminalResponse toTerminalResponse(PayPointResultEvent event) {

        byte result = event.getResult();
        byte accumulator = event.getAccumulator();
        byte issuerId = event.getIssuerId();

        TerminalResponse terminalResponse = new TerminalResponse();
        terminalResponse.setResultData(event.getLocalModeData());

        // region data
        // ...
        // 3// 01************00198-1;
        // 4// 20160404140955;
        // 5// 0;
        // 6// 081;
        // 7// 001456017271;
        // 8// 0000;
        // 9// 00000049600;
        //10// ;
        //11// ;
        //12// ;
        //13// 111010;
        //14// 71408677;
        //15// ;
        //16// ;
        //17// ;
        //18// ;
        //19// ;
        //20// ;
        //21// ;
        //22// ;
        //23// 000000;
        //24// 0;
        //25// BankAxept;
        //26// D5780000021010;
        //27// 017271;
        //28// ;
        //29// ;
        //30// ;
        //31// ;
        //32// DSAFE99999999999997;
        // endregion

        switch (result) {

            case 0x20: // indicates transaction OK

                terminalResponse.setResult(RESULT_ADMINISTRATIVE_TRANSACTION_OK);

                // 0x20 indicates standard update of accumulator
                // 0x22 indicates that transaction is approved offline (and sent automatically later)
                if (accumulator == 0x20 || accumulator == 0x22) {

                    terminalResponse.setResult(RESULT_FINANCIAL_TRANSACTION_OK);

                    // Field id 0 is reserved for Result code
                    // Field id 1 is reserved for Accumulator
                    // For approved transactions field 2 will contain Issuer id: 2 digit issuer number

                    // Approved financial transaction (Result code is RESULT_OK and Accumulator is ACCU_APPROVED_ONLINE or ACCU_APPROVED_OFFLINE)

                    // Issuer id: 2 digit issuer number
                    terminalResponse.setIssuerId(issuerId);

                    // 3 -> Primary account number. Variable field length, max 19 digits. Will be masked.
                    // 4 -> Transaction timestamp in format YYYYMMDDhhmmss
                    terminalResponse.setTimestamp(event.getLocalModeFieldById(4));

                    // 5 -> Verification indicator
                    //    0x30: no signature panel
                    //    0x31: signature panel
                    //    0x32: loyalty
                    terminalResponse.setVerificationMethod(Integer.parseInt(event.getLocalModeFieldById(5)));

                    // 6 -> Session number, 3 digits
                    terminalResponse.setSessionNumber(event.getLocalModeFieldById(6));

                    // 7 -> Retrieval reference number, 12 digits
                    // 8 -> 0000 (4 bytes, not used)

                    // 9 ->  Total amount including tip entered by the customer on the terminal, 11 digits
                    terminalResponse.setTotalAmount(Integer.parseInt(event.getLocalModeFieldById(9)));

                    // 10 -> ****, 4 bytes. Present only for approved pre-authorisations, else empty
                    // 11 -> ***, 3 bytes. Present only for approved pre-authorisations, else empty
                    // 12 ->  Authorisation flag. Present only for approved pre-authorisations, else empty
                    //    0x30: online authorisation
                    //    0x31: manual authorisation

                    // 13 ->  Retailer id, 6 numeric digits 111010
                    terminalResponse.setAcquirerMerchantID(event.getLocalModeFieldById(13));
                    // 14 ->  Terminal id, 8 bytes 71408677
                    terminalResponse.setTerminalID(event.getLocalModeFieldById(14));

                    // 15 -> Authorisation status. Present only for approved pre-authorisations, else empty
                    //    0x30: signature on adjustment
                    //    0x31: additional authorisation performed, signature on adjustment
                    //    0x32: online pin entered, no signature on adjustment
                    //    0x33: offline pin entered, no signature on adjustment

                    // 16 -> Encrypted card data. Present only for approved pre-authorisations, else empty
                    // 17 -> EMV data. Present only for approved pre-authorisations, else empty
                    // 18 -> Checksum, 4 digits. Present only for approved pre-authorisations, else empty
                    // 19 -> Card product. Possible content: ICA, NK, BANK, AMX or DINERS (1)
                    // 20 -> Financial institution. Possible content: ICA, AMX, DIN, SWE, SEB, SHB, NOR, Ã–EB, FRI, SVB (1)
                    // 21 -> ECR transaction id. Range "1"-"255" (1)

                    // 22 ->  Encrypted card number (1)
                    //    Length of encrypted PAN, 2 numeric digits
                    //    Encrypted PAN, variable size
                    //    Key serial number, 20 bytes

                    // 23 -> Processing code, 6 numeric digits (1)  000000
                    // 24 -> Transaction type, the same value that was used as input to startTransaction when the transaction was started (1)
                    // 25 -> Application Label or Application Preferred name for chip, card/issuer name for magnetic stripe (1)  BankAxept
                    terminalResponse.setCardIssuerName(event.getLocalModeFieldById(25));

                    // 26 -> Application Identifier (only present for chip transactions) (1) D5780000021010
                    terminalResponse.setApplicationIdentifier(event.getLocalModeFieldById(26));

                    // 27 -> Authorisation code, 6 digits
                    terminalResponse.setStanAuth(event.getLocalModeFieldById(27));

                    // 28 -> Character string containing, 3 bytes. Containing POS entry mode, verification method and card authorisation channel. Used in Sweden
                    // 29 -> VAT amount, 11 digits

                    // 30 -> Encrypted bonus number (1)
                    //    Length of encrypted bonus number, 2 numeric digits
                    //    Encrypted bonus number, variable size
                    //    Key serial number, 20 bytes

                    // 31 -> Bonus amount, 11 digits

                    // 32 -> Card holder ID, 19 alphanumeric. Only present when received from NETS, else empty. DSAFE99999999999997
                    terminalResponse.setOptionalData("Card holder ID:" + event.getLocalModeFieldById(32));
                }

                break;
            case 0x21: // indicates transaction/operation rejected
                terminalResponse.setResult(GetShopNetsApp.RESULT_TRANSACTION_REJECTED);

                // 3 -> Error type
                //    0x30: Unknown
                //    0x31: General terminal error
                //    0x32: Communication error
                //    0x33: Host rejected
                //    0x34: Cancelled by customer or operator
                //    0x35: Card removed
                //    0x36: Terminal busy
                //    0x37: Integration component error
                String errType = event.getLocalModeFieldById(3);

                if (errType != null) {
                    try {
                        terminalResponse.setRejectionSource(Integer.parseInt(errType));
                    } catch (Exception numex) {
                        logPrint("error occured whe translating response: " + numex.getMessage());
                        terminalResponse.setRejectionSource(0x30);
                    }
                }

                // todo check 4 -> Detailed error code. Variable size
                //terminalResponse.setRejectionReason(event.getLocalModeFieldById(4));

                break;
            case 0x22: // indicates that additional authorisation is needed (only relevant for adjustment)

                break;
        }

        // region docs
        //            String containing the additional result data. If present the first two bytes will always be:
        //
        //            ;	073	0x3B	0x3B		Semicolon
        //
        //            Result code
        //                0x20 indicates transaction OK
        //                0x21 indicates transaction/operation rejected
        //                0x22 indicates that additional authorisation is needed (only relevant for adjustment)
        //            Accumulator
        //                0x20 indicates standard update of accumulator
        //                0x22 indicates that transaction is approved offline (and sent automatically later)
        //                0x30 indicates no update of accumulator
        //
        //            The remaining content depends on transaction type and if it is approved or not:
        //
        //            Approved financial transaction (Result code is RESULT_OK and Accumulator is ACCU_APPROVED_ONLINE or ACCU_APPROVED_OFFLINE):
        //                Issuer id: 2 digit issuer number
        //                Primary account number. Variable field length, max 19 digits. Will be masked.
        //                0x3B separator
        //                Transaction timestamp in format YYYYMMDDhhmmss
        //                0x3B separator
        //                Verification indicator
        //                    0x30: no signature panel
        //                    0x31: signature panel
        //                    0x32: loyalty
        //                0x3B separator
        //                Session number, 3 digits
        //                0x3B separator
        //                Retrieval reference number, 12 digits
        //                0x3B separator
        //                0000 (4 bytes, not used)
        //                0x3B separator
        //                Total amount including tip entered by the customer on the terminal, 11 digits
        //                0x3B separator
        //                ****, 4 bytes. Present only for approved pre-authorisations, else empty
        //                0x3B separator
        //                ***, 3 bytes. Present only for approved pre-authorisations, else empty
        //                0x3B separator
        //                Authorisation flag. Present only for approved pre-authorisations, else empty
        //                    0x30: online authorisation
        //                    0x31: manual authorisation
        //                0x3B separator
        //                Retailer id, 6 numeric digits
        //                0x3B separator
        //                Terminal id, 8 bytes.
        //                0x3B separator
        //                Authorisation status. Present only for approved pre-authorisations, else empty
        //                    0x30: signature on adjustment
        //                    0x31: additional authorisation performed, signature on adjustment
        //                    0x32: online pin entered, no signature on adjustment
        //                    0x33: offline pin entered, no signature on adjustment
        //                0x3B separator
        //                Encrypted card data. Present only for approved pre-authorisations, else empty
        //                0x3B separator
        //                EMV data. Present only for approved pre-authorisations, else empty
        //                0x3B separator
        //                Checksum, 4 digits. Present only for approved pre-authorisations, else empty
        //                0x3B separator
        //                Card product. Possible content: ICA, NK, BANK, AMX or DINERS (1)
        //                0x3B separator
        //                Financial institution. Possible content: ICA, AMX, DIN, SWE, SEB, SHB, NOR, Ã–EB, FRI, SVB (1)
        //                0x3B separator
        //                ECR transaction id. Range "1"-"255" (1)
        //                0x3B separator
        //                Encrypted card number (1)
        //                    Length of encrypted PAN, 2 numeric digits
        //                    Encrypted PAN, variable size
        //                    Key serial number, 20 bytes
        //                0x3B separator
        //                Processing code, 6 numeric digits (1)
        //                0x3B separator
        //                Transaction type, the same value that was used as input to startTransaction when the transaction was started (1)
        //                0x3B separator
        //                Application Label or Application Preferred name for chip, card/issuer name for magnetic stripe (1)
        //                0x3B separator
        //                Application Identifier (only present for chip transactions) (1)
        //                0x3B separator
        //                Authorisation code, 6 digits
        //                0x3B separator
        //                Character string containing, 3 bytes. Containing POS entry mode, verification method and card authorisation channel. Used in Sweden
        //                0x3B separator
        //                VAT amount, 11 digits
        //                0x3B separator
        //                Encrypted bonus number (1)
        //                    Length of encrypted bonus number, 2 numeric digits
        //                    Encrypted bonus number, variable size
        //                    Key serial number, 20 bytes
        //                0x3B separator
        //                Bonus amount, 11 digits
        //                0x3B separator
        //                Card holder ID, 19 alphanumeric. Only present when received from NETS, else empty.
        //                0x3B separator
        //            Rejected financial transaction/administrative command, i.e. Result code is RESULT_REJECTED (1):
        //                Error type
        //                    0x30: Unknown
        //                    0x31: General terminal error
        //                    0x32: Communication error
        //                    0x33: Host rejected
        //                    0x34: Cancelled by customer or operator
        //                    0x35: Card removed
        //                    0x36: Terminal busy
        //                    0x37: Integration component error
        //                0x3B separator
        //                Detailed error code. Variable size
        //                0x3B separator
        //
        //            (1) ONLY RETURNED WHEN SPECIAL SW IN TERMINAL
        // endregion

        return terminalResponse;
    }
    
 private void handleResultEvent(PayPointResultEvent resultEvent) {
        logPrint("Result event: " + resultEvent);
        if(resultEvent.getResult() == 32) {
            logPrint("Card succesfully paid");
            printToScreen("completed");
        } else {
            logPrint("Failed to pay");
            printToScreen("payment failed");
        }
        
        
        TerminalResponse response = toTerminalResponse(resultEvent);
        System.out.println(response.toString());
        switch(response.getResult()) {
            case RESULT_FINANCIAL_TRANSACTION_OK:
            case RESULT_ADMINISTRATIVE_TRANSACTION_OK:
            case RESULT_TRANSACTION_IS_LOYALTY:
                response.setPaymentResult(1);
                break;
            default:
                response.setPaymentResult(0);
        }
        response.setOrderId(orderId);
            
        if(operator != null) {
             operator.sendMessage("OrderManager", "paymentResponse", operator.getToken(), response, null,null, null);
        }
        
    }
    
    
    public void printToScreen(String text) {
        if(operator != null) {
             operator.sendMessage("OrderManager", "paymentText", operator.getToken(), text, null,null, null);
        }
    }

    @Override
    public String getName() {
        return "VERIFONE";
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}

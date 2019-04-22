package getshop.nets;

import com.google.gson.Gson;
import eu.nets.baxi.client.LocalModeEventArgs;
import java.util.Scanner;
import com.thundashop.core.gsd.TerminalResponse;
import getshopiotserver.GetShopIOTOperator;

/**
 * Created by mcamp on 14.03.2017.
 *
 * This is the main JavaFX application class for the Sample Application. It loads the UI of the demo app, initializes
 * the BAXI Java library and shows the main API calls.
 */
public class GetShopNetsApp {
    private GetShopNetsController controller;
    private final GetShopIOTOperator operator;

    public static final int RESULT_FINANCIAL_TRANSACTION_OK = 0;        // : Financial transaction OK, accumulator updated
    public static final int RESULT_ADMINISTRATIVE_TRANSACTION_OK = 1;   // : Administrative transaction OK, no update of accumulator
    public static final int RESULT_TRANSACTION_REJECTED = 2;            // : Transaction rejected, no update of accumulator
    public static final int RESULT_TRANSACTION_IS_LOYALTY = 3;          // : Transaction is Loyalty Transaction
    public static final int RESULT_UNKNOWN = 99;      
    
    
    public static void main(String[] args) throws Exception {
//        GetShopNetsApp app = new GetShopNetsApp();
//        app.start();
        
    }
    
    private boolean isStarted = false;

    public GetShopNetsApp(GetShopIOTOperator operator) {
        this.operator = operator;
    }

    public void initialize() {
        controller = new GetShopNetsController(this);
        controller.openBaxi();
    } 

    public void start() throws Exception {
        controller = new GetShopNetsController(this);

        
        System.out.println("#### GetShop TEST CARD MENU ####");
        System.out.println("1. Open baxi");
        System.out.println("2. Close baxi");
        System.out.println("3. Start transaction");
        System.out.println("4. Stop transaction");
        System.out.println("5. Get last finansial status");
        
        while(true) {
            Scanner sc = new Scanner(System.in);
            int i = sc.nextInt();
            switch(i) {
                case 1:
                    controller.openBaxi();
                    break;
                case 2:
                    controller.closeBaxi();
                    break;
                case 3:
                    System.out.println("Amount");
                    int amount = sc.nextInt();
                    controller.totalAmount = amount;
                    controller.transferAmount();
                    break;
                case 4:
                    controller.doAdministration(0x3132);
                    break;
                case 5:
                    controller.doAdministration(0x313D);
                    break;
            }
        }

    }

    public void stop() throws Exception {
        controller.closeBaxi();
    }
    
    public void printToScreen(String text) {
         operator.sendMessage("OrderManager", "paymentText", operator.getToken(), text, null,null, null);
    }

    public void transanctionCompletedStatus(LocalModeEventArgs args) {
        
        
         TerminalResponse response = new TerminalResponse()
            .setAccountType(args.getAccountType())
            .setAcquirerMerchantID(args.getAcquirerMerchantID())
            .setApplicationEffectiveData(args.getAED())
            .setApplicationIdentifier(args.getAID())
            .setApplicationTransactionCounter(args.getATC())
            .setBankAgent(args.getBankAgent())
            .setCardIssuerName(args.getCardIssuerName())
            .setIssuerActionCode(args.getIAC())
            .setIssuerId(args.getIssuerId())
            .setOptionalData(args.getOptionalData())
            .setOrganisationNumber(args.getOrganisationNumber())
            .setRejectionReason(args.getRejectionReason())
            .setRejectionSource(args.getRejectionSource())
            .setResponseCode(args.getResponseCode())
            .setResult(args.getResult())
            .setResultData(args.getResultData())
            .setSequenceNumber(args.getSequenceNumber())
            .setSessionNumber(args.getSessionNumber())
            .setStanAuth(args.getStanAuth())
            .setSurchargeAmount(args.getSurchargeAmount())
            .setTerminalID(args.getTerminalID())
            .setTimestamp(args.getTimestamp())
            .setTipAmount(args.getTipAmount())
            .setTotalAmount(args.getTotalAmount())
            .setTruncatedPan(args.getTruncatedPan())
            .setTerminalStatusInformation(args.getTSI())
            .setTerminalVerificationResult(args.getTVR())
            .setVerificationMethod(args.getVerificationMethod());
         System.out.println(response.toString());
         
            switch(args.getResult()) {
            case RESULT_FINANCIAL_TRANSACTION_OK:
            case RESULT_ADMINISTRATIVE_TRANSACTION_OK:
            case RESULT_TRANSACTION_IS_LOYALTY:
                response.setPaymentResult(1);
                break;
            default:
                response.setPaymentResult(0);
         }
            
        if(!isStarted && response.paymentSuccess()) {
            isStarted = true;
            return;
        }
        
         operator.sendMessage("OrderManager", "paymentResponse", operator.getToken(), response, null,null, null);
    }

    public void startTransaction(Integer amount) {
        controller.totalAmount = amount;
        controller.transferAmount();
    }

    public void cancelTransaction() {
        controller.doAdministration(0x3132);
    }

    public boolean isInitialized() {
        return isStarted;
    }
    
}

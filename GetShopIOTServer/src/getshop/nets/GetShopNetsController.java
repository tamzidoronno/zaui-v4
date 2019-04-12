package getshop.nets;

import eu.nets.baxi.client.*;
import eu.nets.baxi.ef.BaxiEF;
import eu.nets.baxi.ef.BaxiEFEventListener;
import eu.nets.baxi.ef.CardEventArgs;
import eu.nets.baxi.ef.CardInfoAllArgs;
import eu.nets.baxi.log.FileAccess;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Controller in (MVC) for the Sample App UI. It will be responsible for handling the user events and
 * displaying the terminal results.
 *
 * Created by mcamp on 14.03.2017.
 */
public class GetShopNetsController implements Initializable {
    /**
     * Some logging abilities for debugging purposes.
     */
    private static  final Logger LOG = Logger.getLogger(GetShopNetsController.class.getName());

    /**
     * Default operator id for the BAXI operations. This is hardcoded for the demo app purposes.
     */
    private static final String OPERATOR_ID = "1234";

    /**
     * The new line terminator string.
     */
    private static final String NEW_LINE = "\n";

    /**
     * A string which can be used to separate events displayed in the same text area.
     */
    private static final String RESULT_SEPARATOR = "-----------------------" + NEW_LINE;

    /**
     * The connection status - connected - string.
     */
    private static final String STATUS_CONNECTED = "Connected";

    /**
     * The connection status - disconnected - string.
     */
    private static final String STATUS_DISCONNECTED = "Disconnected";

    /**
     * The JBAXI library API interface.
     */
    private BaxiCtrl baxi;

    /**
     * The JavaFX stage we're working with.
     */
    private Stage stage;

    /**
     * The connection status label.
     */
    @FXML
    private Label connStatus;

    /**
     * The items total text field.
     */
    @FXML
    private TextField itemTotalText;

    /**
     * The text area where we display the bought items when clicking the item buttons.
     */
    @FXML
    private TextArea itemArea;

    /**
     * The text area where we display text events.
     */
    @FXML
    private TextArea displayEventsArea;

    /**
     * The text area where we display receipt events.
     */
    @FXML
    private TextArea receiptEventsArea;

    /**
     * The text area where we display the terminal and local results.
     */
    @FXML
    private TextArea terminalResultArea;

    /**
     * The current total amount for the items chosen by the cashier.
     */
    public int totalAmount = 0;

    /**
     * Flag which will tell us if the application layer is connected. Based on this we set the Connected/Disconnected
     * status.
     */
    private boolean isConnected = false;

    /**
     * Flag which tells us if the communication layer connection is initiated and we are between the moment of initial
     * network layer connection and the application layer connection.
     */
    private boolean isItuHandshaking = false;
    
    private final GetShopNetsApp sampleApp;

    BaxiEFEventListener efListener = new BaxiEFEventListener() {
        /**
         * In this sample app, we are enabling the Baxi EF layer by set CardInfoAll from baxi.ini to be 1 so
         * that Extra Functionality is enabled which gives an enriched card info message
         *
         * Card info if you are using Jbaxi-android with EF(extra functionality) layer.
         * If you don't need EF layer functionality, you could include Jbaxi-android jar instead in libs folder.
         *
         * @param args the transaction arguments.
         */
        @Override
        public void OnCardInfoAll(final CardInfoAllArgs args) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String str = "";

                    str += "CardInfoAll " + NEW_LINE;
                    str += "  VAS: " + args.getVAS() + NEW_LINE;
                    str += "  Customer id: " + args.getCustomerId() + NEW_LINE;
                    str += "  Psp Command: " + args.getPspCommand() + NEW_LINE;
                    str += "  Status Code: " + args.getStatusCode() + NEW_LINE;
                    str += "  Information Field 1: " + args.getInformationField1() + NEW_LINE;
                    str += "  Information Field 2: " + args.getInformationField2() + NEW_LINE;
                    str += "  Psp Vas ID: " + args.getPspVasId() + NEW_LINE;
                    str += "  Card Validation: " + args.getCardValidation() + NEW_LINE;
                    str += "  ICC: " + args.getICCGroupId() + NEW_LINE;
                    str += "  PAN: " + args.getPAN() + NEW_LINE;
                    str += "  Issuer ID: " + args.getIssuerId() + NEW_LINE;
                    str += "  Country Code: " + args.getCountryCode() + NEW_LINE;
                    str += "  Card Restrictions: " + args.getCardRestrictions() + NEW_LINE;
                    str += "  Card Fee: " + args.getCardFee() + NEW_LINE;
                    str += "  Track2: " + args.getTrack2() + NEW_LINE;
                    str += "  TCC: " + args.getTCC() + NEW_LINE;
                    str += "  Bank Agent: " + args.getBankAgent() + NEW_LINE + RESULT_SEPARATOR;

                    terminalResultArea.appendText(str);
                    LOG.info(str);
                }
            });
        }

        /**
         * Card action status (withdraw or insert) if you are using Jbaxi-android with EF(extra functionality) layer.
         *
         * @param e the event arguments.
         */
        @Override
        public void OnCard(final CardEventArgs e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String eventText = "";

                    eventText += "  Issuer ID: " + e.getIssuerID() + NEW_LINE;
                    eventText += "  Card Status: " + e.getCardStatus() + "(" + e.getCardStatus().getValue() + ")"
                            + NEW_LINE + RESULT_SEPARATOR;

                    terminalResultArea.appendText(eventText);
                    LOG.info("OnCard is fired: " + eventText);
                }
            });
        }

        /**
         * This is an optional and special purpose event. It is triggered each time the terminal sends a response
         * frame to Baxi. The user wants to make a quick consecutive calls to Baxi after ITU completed the last operation.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnStdRspReceived(final StdRspReceivedEventArgs args) {
//            System.out.println("OnStdRspReceived : " + args.getResponse());
        }

        /**
         * Formatted printer text(Receipt) received from ITU.
         *
         * @param evt the event arguments.
         */
        @Override
        public void OnPrintText(final PrintTextEventArgs evt) {
//           System.out.println("print text: " + evt.getPrintText());
        }

        /**
         * Display text received from ITU.
         *
         * @param e the event arguments.
         */
        @Override
        public void OnDisplayText(final DisplayTextEventArgs e) {
//            System.out.println("On display text: " + e.getDisplayText() + " - " + e.getDisplaytextID() + " - " + e.getDisplaytextSourceID());
            sampleApp.printToScreen(e.getDisplayText());
        }

        /**
         * Formats the result text from the transaction arguments.
         * @param args the transaction arguments.
         * @return a formatted text.
         */
        private String formatResultText(TransactionEventArgs args) {
            String lmText = "";

            lmText += "ResultData: " + args.getResultData() + NEW_LINE;
            lmText += "   Result: " + args.getResult() + NEW_LINE;
            lmText += "   AccumulatorUpdate: 0x" + String.format("%02x", args.getAccumulatorUpdate()) + NEW_LINE;
            lmText += "   IssuerId: " + String.format("%02d", args.getIssuerId()) + NEW_LINE;
            lmText += "   CardData: " + args.getTruncatedPan() + NEW_LINE;
            lmText += "   Timestamp: " + args.getTimestamp() + NEW_LINE;
            lmText += "   VerificationMethod: " + args.getVerificationMethod() + NEW_LINE;
            lmText += "   SessionNumber: " + args.getSessionNumber() + NEW_LINE;
            lmText += "   StanAuth: " + args.getStanAuth() + NEW_LINE;
            lmText += "   SequenceNumber: " + args.getSequenceNumber() + NEW_LINE;
            lmText += "   TotalAmount: " + args.getTotalAmount() + NEW_LINE;
            lmText += "   RejectionSource: " + args.getRejectionSource() + NEW_LINE;
            lmText += "   RejectionReason: " + args.getRejectionReason() + NEW_LINE;
            lmText += "   TipAmount: " + args.getTipAmount() + NEW_LINE;
            lmText += "   SurchargeAmount: " + args.getSurchargeAmount() + NEW_LINE;
            lmText += "   terminalID: " + args.getTerminalID() + NEW_LINE;
            lmText += "   acquirerMerchantID: " + args.getAcquirerMerchantID() + NEW_LINE;
            lmText += "   cardIssuerName: " + args.getCardIssuerName() + NEW_LINE;
            lmText += "   responseCode: " + args.getResponseCode() + NEW_LINE;
            lmText += "   TCC: " + args.getTCC() + NEW_LINE;
            lmText += "   AID: " + args.getAID() + NEW_LINE;
            lmText += "   TVR: " + args.getTVR() + NEW_LINE;
            lmText += "   TSI: " + args.getTSI() + NEW_LINE;
            lmText += "   ATC: " + args.getATC() + NEW_LINE;
            lmText += "   AED: " + args.getAED() + NEW_LINE;
            lmText += "   IAC: " + args.getIAC() + NEW_LINE;
            lmText += "   OrganisationNumber: " + args.getOrganisationNumber() + NEW_LINE;
            lmText += "   BankAgent : " + args.getBankAgent() + NEW_LINE;
            lmText += "   EncryptedPAN : " + args.getEncryptedPAN() + NEW_LINE;
            lmText += "   AccountType : " + args.getAccountType() + NEW_LINE;
            lmText += "   OptionalData : " + args.getOptionalData() + NEW_LINE;

            return lmText;
        }

        /**
         * Signals the transaction is completed.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnLocalMode(final LocalModeEventArgs args) {
//            System.out.println("transaction completed: " + args.getResultData() + " : " + args.getResult());
            sampleApp.transanctionCompletedStatus(args);
        }

        /**
         * If the TerminalReady property is enabled, this event is always expected as ITU goes into idle after an
         * activity. Hence, it is ready to handle a new function like purchase.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnTerminalReady(TerminalReadyEventArgs args) {
//            System.out.println("Terminal is ready");
        }

        /**
         * This event is a TLD tag data that is sent from ITU.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnTLDReceived(final TLDReceivedEventArgs args) {
//           System.out.println("Tldrecieved");
        }

        /**
         * OnLastFinacialResult event is triggered by Administration command with AdminCode 0x313D. Parameter
         * localModeEventArgs is identical to the contents of a onLocalMode message, but it contains the results
         * of the last/latest financial operation.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnLastFinancialResult(final LastFinancialResultEventArgs args) {
            String eventText = "OnLastFinancialResul\n\n" + formatResultText(args) + RESULT_SEPARATOR;
            System.out.println(eventText);
        }

        /**
         * Raise the error message up if an error has occurred and errorEventArgs contain Error code and Error String
         * to describe the error.
         *
         * @param args the event arguments.
         */
        @Override
        public void OnBaxiError(final BaxiErrorEventArgs args) {
            String text = "Error: " + args.getErrorCode() + " " + args.getErrorString();
            displayEventsArea.setText(text);
        }

        @Override
        public void OnConnected() {
//            System.out.println("Connected to server");
        }

        @Override
        public void OnDisconnected() {
            connStatus.setText(STATUS_DISCONNECTED);
            isConnected = false;
        }

        @Override
        public void OnJsonReceived(JsonReceivedEventArgs jrea) {
        }

        @Override
        public void OnBarcodeReader(BarcodeReaderEventArgs brea) {
        }
    };

    public GetShopNetsController(GetShopNetsApp aThis) {
        this.sampleApp = aThis;
    }

    /**
     * Check to see if the baxi.ini file exists and the CardInfoAll flag is set.
     *
     * @return true if the CardInfoAll flag is set, false otherwise.
     */
    private boolean isCardInfoAll() {
        final String cardInfoKey = "CardInfoAll=";
        int retVal = -1;
        FileReader iniFile;
        try {
            iniFile = new FileReader("baxi.ini");
            BufferedReader br = new BufferedReader(iniFile);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(cardInfoKey)) {
                        String val = line.substring(cardInfoKey.length());
                        retVal = Integer.parseInt(val);
                    }
                }
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(FileAccess.class.getName()).log(Level.INFO, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileAccess.class.getName()).log(Level.INFO,"ini-file not present!");
        }

        return retVal==1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemTotalText.setText("" + totalAmount);
    }

    /**
     * Set the JavaFX stage as a global variable. We're gonna need it around.
     *
     * @param stage the JavaFX stage window.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Close the Baxi connection.
     */
    public void closeBaxi() {
        LOG.info("Closing the BAXI connection...");
        if (baxi.isOpen()) {
            int result = baxi.close();
            LOG.info("BAXI connection closed status: " + result);
        }
    }

    /**
     * Initializes the BAXI Java library controller. This is the API interface with the JBaxi library.
     */
    public void openBaxi() {
        if (isCardInfoAll()) {
            isItuHandshaking = true;
            baxi = new BaxiEF();
            ((BaxiEF) baxi).addBaxiEFListener(efListener);
            int result = baxi.open();
            LOG.info("Open BAXI status: " + result);
        } else {
            throw new IllegalStateException("Only EF Baxi supported in sample app. Please set CardInfoAll property to 1 in baxi.ini");
        }
    }


    /**
     * A generic administration operation which will be called with the admin code corresponding to the specific admin
     * operation we are trying to execute. The administration operations are Cancel, End of Day (reconciliation) and
     * Last Receipt.
     *
     * @param adminCode the admin code corresponding to the administration operation we are trying to execute.
     * @return the result of the admin operation.
     */
    public int doAdministration(int adminCode) {
        int result = 0;

        AdministrationArgs administrationArgs = new AdministrationArgs();

        administrationArgs.AdmCode =  adminCode;
        administrationArgs.OperID = OPERATOR_ID;
        administrationArgs.OptionalData = "";

        if (baxi != null) {
            result = baxi.administration(administrationArgs);
        }

        if (result == 0) {
            LOG.severe("Administration operation failed with methodRejectCode = " + baxi.getMethodRejectCode());
        }

        return result;
    }

    public void adminCancel() {
        doAdministration(0x3132);
    }

    public void adminEndOfDay() {
        doAdministration(0x3130);
    }

    public void adminLastReceipt() {
        doAdministration(0x313C);
    }

    public void close() {
        stage.close();
    }

    public void transferAmount() {
        if (totalAmount != 0) {

            String myOperation = "Purchase"; // we hardcode the "Purchase" operation.
            String amount2 ="0"; // we do not have amount 2
            String amount3 ="0"; // we do not have amount 3

            TransferAmountArgs args = new TransferAmountArgs();
            args.setOperID(OPERATOR_ID);

            try{
                args.setAmount1(totalAmount);
            }catch(NumberFormatException e){
                args.setAmount1(0);
            }

            int type1 = 0;
            if(myOperation.equalsIgnoreCase("Purchase")){
                type1 = 0x30;
            }else if(myOperation.equalsIgnoreCase("Return of Goods")){
                type1 = 0x31;
            } else if(myOperation.equalsIgnoreCase("Reversal")){
                type1 = 0x32;
            }else if(myOperation.equalsIgnoreCase("Cashback")){
                type1 = 0x33;
            }else if(myOperation.equalsIgnoreCase("Authorisation")){
                type1 = 0x34;
            }else if(myOperation.equalsIgnoreCase("Balance")){
                type1 = 0x36;
            }else if(myOperation.equalsIgnoreCase("Deposit")){
                type1 = 0x38;
            }else if(myOperation.equalsIgnoreCase("Cash Withdrawal")){
                type1 = 0x39;
            }else if(myOperation.equalsIgnoreCase("Bonus")){
                type1 = 0x3E;
            }else if(myOperation.equalsIgnoreCase("Force Offline")){
                type1 = 0x40;
            }else if(myOperation.equalsIgnoreCase("Incr PRE Auth")){
                type1 = 0x41;
            }else if(myOperation.equalsIgnoreCase("Reversal PRE Auth")){
                type1 = 0x42;
            }else if(myOperation.equalsIgnoreCase("SaleComp PRE Auth")){
                type1 = 0x43;
            }else if(myOperation.equalsIgnoreCase("Bonus Refund")){
                type1 = 0x44;
            }

            args.setType1(type1);
            try{
                if (amount2.isEmpty()){
                    args.setAmount2(0);
                }else{
                    args.setAmount2(Integer.parseInt(amount2));
                }
            }catch(NumberFormatException e){
                args.setAmount2(0);
            }

            args.setType2(0x30);
            try{
                if (amount3.isEmpty()){
                    args.setAmount3(0);
                }else {
                    args.setAmount3(Integer.parseInt(amount3));
                }
            }catch(NumberFormatException e){
                args.setAmount3(0);
            }

            args.setType3(0x32);

            int result = baxi.transferAmount(args);
            LOG.info("The transfer amount operation result: " + result);

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Amount is 0!");
            alert.setHeaderText(null);
            alert.setContentText("Total amount cannot be 0, please add some items!");
            alert.showAndWait();
        }
    }

    public boolean isOpen() {
        return isConnected;
    }
}

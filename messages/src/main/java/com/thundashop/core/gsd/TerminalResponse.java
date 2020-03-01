/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

/**
 *
 * @author boggi
 */
public class TerminalResponse {
    
    /**
     * Unique operation id to have an asynchronous link between requests and responses
     */
    private String uuid = "";
    private String orderId = "";

    /**
     * This parameter contains the complete local mode reply from the terminal in raw format.
     */
    private String resultData = "";

    /**
     * This is the result of the transaction
     *
     * @see CardTerminal RESULT_* constants
     */
    private int result = -1;

    /**
     * This is the card issuer ID.
     */
    private int issuerId = -1;

    /**
     * This parameter contains card information. The format is the card number with length up to 19 digits.
     */
    private String truncatedPan = "";

    /**
     * This parameter contains the timestamp of the transaction.
     */
    private String timestamp = "";

    /**
     * This is a code for cardholder verification method (CVM).
     *
     * @see CardTerminal VERIFICATION_* constants
     */
    private int verificationMethod = -1;

    /**
     * This parameter contains the session number of the transaction (3 bytes numeric data).
     * This number is global for a site and gets incremented after a reconciliation.
     */
    private String sessionNumber = "";

    /**
     * This parameter contains the transaction reference number. The length is 12 characters
     */
    private String stanAuth = "";

    /**
     * 4 bytes numeric data (0x30 .. 0x39). This is the customer number if the transaction was
     * Pre-Auth transaction. Must be used as reference in Transfer Amount - Adjustment transaction.
     */
    private String sequenceNumber = "";

    /**
     * 11 bytes numeric data (0x30 .. 0x39). This parameter is the Total ITU transaction amount used
     * in Restaurant or Hotel environment where TIP is added to the purchase amount on the ITU.
     * It’s also used if a surcharge fee is added to the purchase amount on the ITU. This total
     * amount will then contain the sum of the original amount plus the surcharge amount.
     */
    private int totalAmount = -1;

    /**
     * This parameter indicates the source for the rejection. Fixed length, 2 characters.
     * This field is optional. Rejection sources will be added as they are needed.
     * If a rejection source is present, there will always also be a rejection reason present.
     */
    private int rejectionSource = -1;

    /**
     * This parameter indicates the reason for a rejection. Variable length, maximum 20 characters.
     * This field is optional.
     */
    private String rejectionReason = "";

    /**
     * This parameter is used in Restaurant or Hotel environment where TIP is added to
     * the purchase amount on the ITU.
     */
    private int tipAmount = -1;

    /**
     * This parameter is used if surcharge fee is added to the purchase amount on the ITU.
     */
    private int surchargeAmount = -1;

    /**
     * This parameter is the terminal ID.
     */
    private String terminalID = "";

    /**
     * This parameter is the Site ID.
     */
    private String acquirerMerchantID = "";

    /**
     * This parameter will be ApplicationLabel in case of chip cards
     */
    private String cardIssuerName = "";

    /**
     * This parameter will contain PSP response codes in case of online approved
     * transactions & Y codes from terminal for offline approved transaction.
     */
    private String responseCode = "";

    /**
     * This parameter is Chip Card Application Identifier
     */
    private String applicationIdentifier = "";

    /**
     * This parameter is Chip Card Terminal Verification Result
     */
    private String terminalVerificationResult = "";

    /**
     * This parameter is Chip Card Terminal Status Information
     */
    private String terminalStatusInformation = "";

    /**
     * This parameter is Chip Card Application Transaction Counter
     */
    private String applicationTransactionCounter = "";

    /**
     * This parameter is Chip Card Application Effective Data
     */
    private String applicationEffectiveData = "";

    /**
     * This parameter is Chip Card Issuer Action Code
     */
    private String issuerActionCode = "";
    private String organisationNumber = "";
    private String bankAgent = "";
    private String accountType = "";
    private String optionalData = "";
    
    /**
     * 0 == Payment failed, 1 == Payment OK
     */
    private int paymentStatus = 0;
    
    /**
     * Should be set to true if this response is a result of an adminsitive task
     */
    private boolean administrativeTask = false;

    // region boilerplate

    public String getUuid() {
        return uuid;
    }

    public TerminalResponse setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public String getResultData() {
        return resultData;
    }

    public TerminalResponse setResultData(String resultData) {
        this.resultData = resultData;
        return this;
    }

    public int getResult() {
        return result;
    }

    public TerminalResponse setResult(int result) {
        this.result = result;
        return this;
    }

    public int getIssuerId() {
        return issuerId;
    }

    public TerminalResponse setIssuerId(int issuerId) {
        this.issuerId = issuerId;
        return this;
    }

    public String getTruncatedPan() {
        return truncatedPan;
    }

    public TerminalResponse setTruncatedPan(String truncatedPan) {
        this.truncatedPan = truncatedPan;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TerminalResponse setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getVerificationMethod() {
        return verificationMethod;
    }

    public TerminalResponse setVerificationMethod(int verificationMethod) {
        this.verificationMethod = verificationMethod;
        return this;
    }

    public String getSessionNumber() {
        return sessionNumber;
    }

    public TerminalResponse setSessionNumber(String sessionNumber) {
        this.sessionNumber = sessionNumber;
        return this;
    }

    public String getStanAuth() {
        return stanAuth;
    }

    public TerminalResponse setStanAuth(String stanAuth) {
        this.stanAuth = stanAuth;
        return this;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public TerminalResponse setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public TerminalResponse setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public int getRejectionSource() {
        return rejectionSource;
    }

    public TerminalResponse setRejectionSource(int rejectionSource) {
        this.rejectionSource = rejectionSource;
        return this;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public TerminalResponse setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public TerminalResponse setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
        return this;
    }

    public int getSurchargeAmount() {
        return surchargeAmount;
    }

    public TerminalResponse setSurchargeAmount(int surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
        return this;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public TerminalResponse setTerminalID(String terminalID) {
        this.terminalID = terminalID;
        return this;
    }

    public String getAcquirerMerchantID() {
        return acquirerMerchantID;
    }

    public TerminalResponse setAcquirerMerchantID(String acquirerMerchantID) {
        this.acquirerMerchantID = acquirerMerchantID;
        return this;
    }

    public String getCardIssuerName() {
        return cardIssuerName;
    }

    public TerminalResponse setCardIssuerName(String cardIssuerName) {
        this.cardIssuerName = cardIssuerName;
        return this;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public TerminalResponse setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public String getApplicationIdentifier() {
        return applicationIdentifier;
    }

    public TerminalResponse setApplicationIdentifier(String applicationIdentifier) {
        this.applicationIdentifier = applicationIdentifier;
        return this;
    }

    public String getTerminalVerificationResult() {
        return terminalVerificationResult;
    }

    public TerminalResponse setTerminalVerificationResult(String terminalVerificationResult) {
        this.terminalVerificationResult = terminalVerificationResult;
        return this;
    }

    public String getTerminalStatusInformation() {
        return terminalStatusInformation;
    }

    public TerminalResponse setTerminalStatusInformation(String terminalStatusInformation) {
        this.terminalStatusInformation = terminalStatusInformation;
        return this;
    }

    public String getApplicationTransactionCounter() {
        return applicationTransactionCounter;
    }

    public TerminalResponse setApplicationTransactionCounter(String applicationTransactionCounter) {
        this.applicationTransactionCounter = applicationTransactionCounter;
        return this;
    }

    public String getApplicationEffectiveData() {
        return applicationEffectiveData;
    }

    public TerminalResponse setApplicationEffectiveData(String applicationEffectiveData) {
        this.applicationEffectiveData = applicationEffectiveData;
        return this;
    }

    public String getIssuerActionCode() {
        return issuerActionCode;
    }

    public TerminalResponse setIssuerActionCode(String issuerActionCode) {
        this.issuerActionCode = issuerActionCode;
        return this;
    }

    public String getOrganisationNumber() {
        return organisationNumber;
    }

    public TerminalResponse setOrganisationNumber(String organisationNumber) {
        this.organisationNumber = organisationNumber;
        return this;
    }

    public String getBankAgent() {
        return bankAgent;
    }

    public TerminalResponse setBankAgent(String bankAgent) {
        this.bankAgent = bankAgent;
        return this;
    }

    public String getAccountType() {
        return accountType;
    }

    public TerminalResponse setAccountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public String getOptionalData() {
        return optionalData;
    }

    public TerminalResponse setOptionalData(String optionalData) {
        this.optionalData = optionalData;
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TerminalResponse{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", resultData='").append(resultData).append('\'');
        sb.append(", result=").append(result);
        sb.append(", issuerId=").append(issuerId);
        sb.append(", truncatedPan='").append(truncatedPan).append('\'');
        sb.append(", timestamp='").append(timestamp).append('\'');
        sb.append(", verificationMethod=").append(verificationMethod);
        sb.append(", sessionNumber='").append(sessionNumber).append('\'');
        sb.append(", stanAuth='").append(stanAuth).append('\'');
        sb.append(", sequenceNumber='").append(sequenceNumber).append('\'');
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", rejectionSource=").append(rejectionSource);
        sb.append(", rejectionReason='").append(rejectionReason).append('\'');
        sb.append(", tipAmount=").append(tipAmount);
        sb.append(", surchargeAmount=").append(surchargeAmount);
        sb.append(", terminalID='").append(terminalID).append('\'');
        sb.append(", acquirerMerchantID='").append(acquirerMerchantID).append('\'');
        sb.append(", cardIssuerName='").append(cardIssuerName).append('\'');
        sb.append(", responseCode='").append(responseCode).append('\'');
        sb.append(", applicationIdentifier='").append(applicationIdentifier).append('\'');
        sb.append(", terminalVerificationResult='").append(terminalVerificationResult).append('\'');
        sb.append(", terminalStatusInformation='").append(terminalStatusInformation).append('\'');
        sb.append(", applicationTransactionCounter='").append(applicationTransactionCounter).append('\'');
        sb.append(", applicationEffectiveData='").append(applicationEffectiveData).append('\'');
        sb.append(", issuerActionCode='").append(issuerActionCode).append('\'');
        sb.append(", organisationNumber='").append(organisationNumber).append('\'');
        sb.append(", bankAgent='").append(bankAgent).append('\'');
        sb.append(", accountType='").append(accountType).append('\'');
        sb.append(", optionalData='").append(optionalData).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setPaymentResult(int status) {
        paymentStatus = status;
    }

    public boolean paymentSuccess() {
        return paymentStatus == 1;
    }

    public void setIsAdministrativeTask() {
        this.administrativeTask = true;
    }
    
    public boolean isAdministrativeTask() {
        return this.administrativeTask;
    }
}

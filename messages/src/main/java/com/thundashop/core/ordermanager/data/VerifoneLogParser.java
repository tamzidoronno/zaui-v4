/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

/**
 *
 * @author ktonder
 */
public class VerifoneLogParser extends PaymentTerminalInformation {
    private String logLine = "";
    
    public VerifoneLogParser(Order order) {
        setData(order);
    }

    // LogData
    // 0// 01************00198-1; -  Primary account number. Variable field length, max 19 digits. Will be masked.
    // 1// 20160404140955; -  Transaction timestamp in format YYYYMMDDhhmmss
    // 2// 0; - Verification indicator, 0x30: no signature panel, 0x31: signature panel, 0x32: loyalty
    // 3// 081; - Session number, 3 digits
    // 4// 001456017271; - Retrieval reference number, 12 digits
    // 5// 0000; - 0000 (4 bytes, not used)
    // 6// 00000049600; - Total amount including tip entered by the customer on the terminal, 11 digits
    // 7// ; - ****, 4 bytes. Present only for approved pre-authorisations, else empty
    // 8// ; - ***, 3 bytes. Present only for approved pre-authorisations, else empty
    // 9// ; - Authorisation flag. Present only for approved pre-authorisations, else empty, 0x30: online authorisation, 0x31: manual authorisation
    //10// 111010; - Retailer id, 6 numeric digits 111010
    //11// 71408677; - Terminal id, 8 bytes 71408677
    //12// ; - Authorisation status. Present only for approved pre-authorisations, else empty, 0x30: signature on adjustment, 0x31: additional authorisation performed, signature on adjustment, 0x32: online pin entered, no signature on adjustment, 0x33: offline pin entered, no signature on adjustment
    //13// ; - Encrypted card data. Present only for approved pre-authorisations, else empty
    //14// ; - EMV data. Present only for approved pre-authorisations, else empty
    //15// ; - Checksum, 4 digits. Present only for approved pre-authorisations, else empty
    //16// ; - Card product. Possible content: ICA, NK, BANK, AMX or DINERS (1)
    //17// ; - Financial institution. Possible content: ICA, AMX, DIN, SWE, SEB, SHB, NOR, Ã–EB, FRI, SVB (1)
    //18// ; - ECR transaction id. Range "1"-"255" (1)
    //19// ; - Encrypted card number (1), Length of encrypted PAN, 2 numeric digits, Encrypted PAN, variable size, Key serial number, 20 bytes
    //20// 000000; - Processing code, 6 numeric digits (1)  000000
    //21// 0; - Transaction type, the same value that was used as input to startTransaction when the transaction was started (1)
    //22// BankAxept; - Application Label or Application Preferred name for chip, card/issuer name for magnetic stripe (1) 
    //23// D5780000021010; - Application Identifier (only present for chip transactions) (1) D5780000021010
    //24// 017271; - Authorisation code, 6 digits
    //25// ; - Character string containing, 3 bytes. Containing POS entry mode, verification method and card authorisation channel. Used in Sweden
    //26// ; - VAT amount, 11 digits
    //27// ; - Encrypted bonus number (1), 
    //28// ; - Bonus amount, 11 digits
    //29// DSAFE99999999999997; - Card holder ID, 19 alphanumeric. Only present when received from NETS, else empty
    
    private void setData(Order order) {
        if (!order.isVerifonePayment())
            return;
        
        paymentType = "Kort";
        for (String log : order.payment.transactionLog.values()) {
            if (log.contains("Result:") && log.contains("32\n")) {
                String data = log.split("Data:")[1].trim();
                String[] dataArr = data.split(";");
                cardInfo = dataArr[0];
                sessionNumber = Integer.parseInt(dataArr[3]);
                transactionNumber = dataArr[4];
                issuerName = dataArr[22];
                break;
            }
        }
    }

    public String getLogLine() {
        return logLine;
    }

    
}

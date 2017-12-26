/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class AcculogixExport implements Serializable {
    public String PODBarcodeID = "";
    
    /** Format: YYYYMMDDHHMMSS */
    public String StatusDateTimeCompleted = "";
   
    public double Latitude;
    public double Longitude;
    
    public String ReceiverName;
    public String TaskStatus;
    
    /** This is the total pieces sent in the Acculogix HOST data, for a PICKUP RETURN this would always be 0 */
    public int TotalPieces = 0;
    
    public String SignatureObtained;
    
    /**
     * If the PODBarcode$ID is scanned send Yes.  
     * If the barcode on the return label or return form is scanned send Yes, otherwise send No
     */
    public String BarcodeValidated = "No";
    
    public String RDDriver$ID = "";
    
    public String SignatureFile = "";
    
    /** Format: YYYYMMDDHHMMSS */
    public String ArrivalDateTime = "";
    
    public String TaskType;
    
    public String TaskComments;
    
    public int TaskContainerCount;
    
    public String taskSource;
    
    /**
     * Order fields
     */
    public String ORReferenceNumber;
    
    public String ORStatus;
    
    public int ORPieceCount;
    public String ORComments = "";
    
    public String ORPieceCorrect;
    
    public String routeId = "";
    
    public int RTRouteStopSeq;
    
    
    public String signatureBase64 = "";
    public String signatureUuid = "";
    
    public long TNTUID = 0;
    
    public transient String md5sum = "";

    public void createMd5Sum() {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            instance.update(baos.toByteArray());
            byte[] b = instance.digest();
           
            String result = "";

            
            for (int i=0; i < b.length; i++) {
                result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
       
            md5sum = result;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AcculogixExport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AcculogixExport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String getAllFieldsToConsider() {
        return PODBarcodeID
            +Latitude
            +Longitude
            +ReceiverName
            +TaskStatus
            +TotalPieces
            +SignatureObtained
            +BarcodeValidated
            +RDDriver$ID
            +SignatureFile
            +ArrivalDateTime
            +TaskType
            +TaskComments
            +TaskContainerCount
            +taskSource
            +ORReferenceNumber
            +ORStatus
            +ORPieceCount
            +ORComments
            +ORPieceCorrect
            +routeId
            +RTRouteStopSeq
            +signatureBase64
            +signatureUuid;

    }

    boolean isAFRecord() {
        return TaskStatus != null && TaskStatus.toLowerCase().equals("af");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.List;

/**
 *
 * @author ktonder
 */
public class AcculogixExport {
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.io.Serializable;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class TrackAndTraceSignature implements Serializable {
    public Date sigutureAddedDate;
    public String imageId;
    public String operatorUserId;
    public String typedName;
    
    /**
     * Will be set upon exporting this data.
     */
    @Transient
    public String address;
}

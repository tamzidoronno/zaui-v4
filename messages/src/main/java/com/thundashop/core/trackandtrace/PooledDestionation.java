/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class PooledDestionation extends DataCommon {
    public String destionationId;
    public String pooledByUserId = "";
    public String originalRouteId = "";
    
    @Transient
    public Destination destination;
    
    @Transient
    public Route originalRoute;
}

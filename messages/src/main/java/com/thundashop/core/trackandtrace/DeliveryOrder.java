/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

/**
 *
 * @author ktonder
 */
public class DeliveryOrder extends TntOrder {
    public int quantity = 0;
    public int orderOdds = 0;
    public int orderFull = 0;
    public int orderLargeDisplays = 0;
    public int orderDriverDeliveries = 0;
    public ContainerType containerType;
    public String orderType = "";
    
    /**
     * Number of bundles delivered for 
     */
    public Integer driverDeliveryCopiesCounted;
    
    
}

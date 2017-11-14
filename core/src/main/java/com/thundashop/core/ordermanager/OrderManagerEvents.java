/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

/**
 *
 * @author ktonder
 */
public interface OrderManagerEvents {
    public void orderCreated(String orderId);
    public void orderChanged(String orderId);
}

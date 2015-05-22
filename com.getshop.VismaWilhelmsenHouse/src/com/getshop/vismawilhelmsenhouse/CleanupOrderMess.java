/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.vismawilhelmsenhouse;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.hotelbookingmanager.UsersBookingData;
import com.thundashop.core.ordermanager.data.Order;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CleanupOrderMess {
    private GetShopApi api;

    public CleanupOrderMess(GetShopApi api) {
        this.api = api;
    }

    public void cleanUp() throws Exception {
        List<UsersBookingData> userBookingData = api.getHotelBookingManager().getAllUsersBookingData();
        List<Order> orders = api.getOrderManager().getOrders(null, null, null);
        
        for (Order order: orders) {
            
            if (!hasReservation(order, userBookingData) && order.status != Order.Status.CANCELED && !order.testOrder) {
                if (order.transferredToAccountingSystem) {
                    notifyDag(order);
                }
                api.getOrderManager().changeOrderStatus(order.id, Order.Status.CANCELED);
            }
        }
    }

    private boolean hasReservation(Order order, List<UsersBookingData> userBookingDatas ) {
        for (UsersBookingData userBookingData : userBookingDatas) {
            if (userBookingData.orderIds.contains(order.id)) {
                return true;
            }
        }
        
        return false;
    }

    private void notifyDag(Order order) throws Exception {
        String subject = "Bestilling kreditert: " + order.incrementOrderId;
        String content = "Bestillings id: " + order.incrementOrderId;
        content += "<br/> Beløp: " + api.getOrderManager().getTotalAmount(order);
        content += "<br/> Ny status: Kanselert";
        
        api.getMessageManager().sendMail("dag@motorcompaniet.as", "Dag Ditmansen", subject, content, "GetShop", "noreply@getshop.com");
    }

    void cleanupOldOrders() throws Exception {
        int[] orderIds = {100009,100011,100012,100013,100014,100016,100019,100022,100024,100025,100027,100028,100029,100032,100033,100035,100037,100038,100041,100046,100048,100050,100056,100057,100058,100061,100073,100077,100079,100081,100084,100085,100087,100088,100090,100093,100094,100100,100107,100108,100109,100110,100111,100113,100114,100115,100118,100119,100120,100123,100126};
        
        for (int orderId : orderIds) {
            Order order = api.getOrderManager().getOrderByincrementOrderId(orderId);
            if (order != null) {
                order.transferredToAccountingSystem = true;
                api.getOrderManager().saveOrder(order);
            }
        }
    }
    
}

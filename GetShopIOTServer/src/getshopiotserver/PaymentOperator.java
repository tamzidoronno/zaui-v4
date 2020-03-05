/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver;

/**
 *
 * @author boggi
 */
public interface PaymentOperator {
    public void initialize();
     public void stop() throws Exception;
    public void startTransaction(Integer amount, String orderId);
    public void cancelTransaction();
    public boolean isInitialized();
    public String getName();

    public void start();
}

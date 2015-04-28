package com.thundashop.core.ordermanager;

import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Internal;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Statistic;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ordermanager handles all orders created by this store.<br>
 * An order is usually created after the order has been added to the cart.<br>
 */
@GetShopApi
public interface IOrderManager {
    /**
     * Create an order out of a given cart.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException 
     */
    public Order createOrder(Address address) throws ErrorException;

    /**
     * This will create a order for a given userId.
     * To avoid fraud, shipment address and etc will only be
     * able to set to the already registered user in the database.
     * 
     * @param userId 
     */
    public Order createOrderForUser(String userId);
    
    /**
     * If a customer is providing a customer reference id, it should be possible to create order by it.
     * @param cart The cart object generated by cartmanager.
     * @return
     * @throws ErrorException 
     */
    public Order createOrderByCustomerReference(String referenceKey) throws ErrorException;
    
    /**
     * Get a list of already created orders.
     * @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
     * @param page What page are you fetching (default 0)
     * @param pageSize Number of entries for each page (default 10)
     * @return
     * @throws ErrorException 
     */
    public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize) throws ErrorException;
    
    /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException 
     */
    
    public Order getOrder(String orderId) throws ErrorException;
    
    @Internal
    public Order getOrderSecure(String orderId) throws ErrorException;
    
    /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException 
     */
    @Administrator
    public Order getOrderByReference(String referenceId) throws ErrorException;
    
    /**
     * Fetch all orders for a user.
     * @param userId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException;
    
    /**
     * Fetch all orders on product.
     * @param userId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<Order> getAllOrdersOnProduct(String productId) throws ErrorException;
    
    /**
     * Update or modify an existing order. 
     * @param order The order to modify
     * @return
     * @throws ErrorException 
     */
    public void saveOrder(Order order) throws ErrorException;
    
    /**
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Order getOrderByincrementOrderId(Integer id) throws ErrorException;
    
    /**
     * If everything is ok, the price is the same as the order and the currency, then update the status.
     * @param password A predefined password needed to update the status.
     * @param orderId The id of the order to update
     * @param currency The currency the transaction returned
     * @param price The price.
     * @throws ErrorException 
     */
    public void setOrderStatus(String password, String orderId, String currency, double price, int status) throws ErrorException;
    
    
    /**
     * Change order status of a specified order.
     * The id could be the orderId or the transaction id.
     */
    @Administrator
    public void changeOrderStatus(String id, int status) throws ErrorException;
    
    /**
     * Calculate the total amount to pay for the order.
     * 
     * @param order
     * @return 
     */
    public Double getTotalAmount(Order order);
    
    
    /**
     * Returns a list over taxes
     * for the specified order.
     * 
     * @param order
     * @return
     * @throws ErrorException 
     */
    public List<CartTax> getTaxes(Order order) throws ErrorException;
    
    /**
     * Returns how many pages there is for this store with the given pagesize
     * @return 
     */
    @Editor
    public int getPageCount(int pageSize, String searchWord);
    
    /**
     * Returns how many pages there is for this store with the given pagesize
     * @return 
     */
    @Editor
    public  List<Order>  searchForOrders(String searchWord, Integer page, Integer pageSize);
    
    /**
     * Returns the total amount of sales for a given year. If you year is left blank you 
     * will get the total amount for all years.
     * 
     * @param year
     * @return 
     */
    @Editor
    public Double getTotalSalesAmount(Integer year);
    
    @Editor
    public Map<String, List<Statistic>> getMostSoldProducts(int numberOfProducts);
    
    @Editor
    public List<Statistic> getSalesNumber(int year);
    
    public void logTransactionEntry(String orderId, String entry) throws ErrorException;
    
    
    @Administrator
    public List<Order> getOrdersToCapture() throws ErrorException;
    
    public List<Order> getOrdersNotTransferredToAccountingSystem();
    
    @Administrator
    public void updatePriceForOrderLine(String cartItemId, String orderId, double price);
    
    @Administrator
    public void changeOrderType(String orderId, String paymentTypeId);
}

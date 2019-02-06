package com.thundashop.core.ordermanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Internal;
import com.thundashop.core.getshopaccounting.DayEntry;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.DoublePostAccountingTransfer;
import com.thundashop.core.ordermanager.data.CartItemDates;
import com.thundashop.core.ordermanager.data.ClosedOrderPeriode;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderFilter;
import com.thundashop.core.ordermanager.data.OrderLight;
import com.thundashop.core.ordermanager.data.OrderManagerSettings;
import com.thundashop.core.ordermanager.data.OrderResult;
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.ordermanager.data.OrderTransactionDTO;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.ordermanager.data.PaymentTerminalInformation;
import com.thundashop.core.ordermanager.data.SalesStats;
import com.thundashop.core.ordermanager.data.Statistic;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.getshopaccounting.OrderUnsettledAmountForAccount;
import com.thundashop.core.ordermanager.data.PmiResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    
    public String createRegisterCardOrder(String paymentType);
    
    @Administrator
    public void deleteAllVirtualOrders();
    
    @Administrator
    public void checkForOrdersFailedCollecting();
    
    @Administrator
    public void forceDeleteOrder(String orderId, String password);
    
    @Administrator
    public void readdTaxGroupToNullItems(String password);
    
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
    
    @Administrator
    public Payment getUserPrefferedPaymentMethod(String userId);
    
    public Payment getMyPrefferedPaymentMethod();
    
    @Editor
    public FilteredData getOrdersFiltered(FilterOptions filterOptions);
    
    @Editor
    public List<OrderResult> getOrdersByFilter(OrderFilter filter);
    
    @Administrator
    public List<String> getPaymentMethodsThatHasOrders();
    
    /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException 
     */
    
    
    @Administrator
    public boolean payWithCard(String orderId, String cardId) throws Exception;
    
    public Order getOrder(String orderId) throws ErrorException;
    
    public OrderLight getOrderLight(String orderId) throws ErrorException;
    
    public Order getOrderWithIdAndPassword(String orderId, String password) throws ErrorException;
    
    /**
     * Fetch a single order based on its id.
     * @param orderId
     * @return
     * @throws ErrorException 
     */
    
    @Administrator
    public List<Order> getOrdersFromPeriode(long start, long end, boolean statistics) throws ErrorException;
    
    @Internal
    public Order getOrderSecure(String orderId) throws ErrorException;
    
    /**
     * Got a reference number for the order, fetch it from here.
     * @param referenceId
     * @throws ErrorException 
     */
    @Administrator
    public Order getOrderByReference(String referenceId) throws ErrorException;

    @Administrator
    public void checkForOrdersToCapture(String internalPassword) throws ErrorException;
    
    @Administrator
    public void checkForOrdersToAutoPay(int daysToTryAfterOrderHasStarted) throws ErrorException;
    
    /**
     * Fetch all orders for a user.
     * @param userId
     * @return
     * @throws ErrorException 
     */
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException;
    
    @Administrator
    public void addProductToOrder(String orderId, String productId, Integer count) throws ErrorException;
    
    @Administrator
    public void updateCountForOrderLine(String cartItemId, String orderId, Integer count);
    
    @Editor
    public Order creditOrder(String orderId);
    
    /**
     * Fetch all orders on product.
     * @param userId
     * @return
     * @throws ErrorException 
     */
    @Editor
    public List<Order> getAllOrdersOnProduct(String productId) throws ErrorException;
    
    /**
     * Update or modify an existing order. 
     * @param order The order to modify
     * @return
     * @throws ErrorException 
     */
    public void saveOrder(Order order) throws ErrorException;
    
    
    @Editor
    public void markAsPaid(String orderId, Date date, Double amount);
    
    
    public void markAsPaidWithPassword(String orderId, Date date, Double amount, String password);
   
    /**
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Order getOrderByincrementOrderId(Integer id) throws ErrorException;
    
    /**
     * @param id
     * @return
     * @throws ErrorException 
     */
    public Order getOrderByincrementOrderIdAndPassword(Integer id, String password) throws ErrorException;
    
    @Editor
    public Long getIncrementalOrderIdByOrderId(String orderId);
    
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
    
    @Administrator
    public void markAsInvoicePayment(String orderId);
    
    /**
     * Calculate the total amount to pay for the order.
     * 
     * @param order
     * @return 
     */
    public Double getTotalAmount(Order order);
    public Double getTotalAmountExTaxes(Order order);
    
    public Double getRestToPay(Order order);
    
    public List<CartItemDates> getItemDates(Date start, Date end);
    
    
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
    public Double getTotalSalesAmount(Integer year, Integer month, Integer week, Integer day, String type);
    
    /**
     * Returns the total amount of sales for a given year. If you year is left blank you 
     * will get the total amount for all years.
     * 
     * @param year
     * @return 
     */
    @Editor
    public HashMap<Long, SalesStats> getSalesStatistics(Long startDate, Long endDate, String type);
    
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
    
    @Administrator
    public void sendReciept(String orderId, String email);
    
    public Double getTotalForOrderById(String orderId);
    
    public Payment getStorePreferredPayementMethod();

    public void setExternalRefOnCartItem(String cartItem, String externalId);
    
    public List<Order> getOrdersPaid(String paymentId, String userId, Date from, Date to);
    
    public void printInvoice(String orderId, String printerId);
    
    @Administrator
    public void addClosedPeriode(ClosedOrderPeriode closed);
    
    @Administrator
    public List<Order> getAllUnpaidInvoices();
    
    @Administrator
    public List<Order> getAllUnpaid(String paymentMethod);
    
    @Administrator
    public Order mergeAndCreateNewOrder(String userId, List<String> orderIds, String paymentMethod, String note);
    
    @Administrator
    public void sendRecieptWithText(String orderId, String email, String subject, String text);
    
    public void startCheckForOrdersToCapture(String internalPassword) throws ErrorException;

    @Administrator
    public void deleteOrder(String orderId);
    
    @Administrator
    public String getEhfXml(String orderId);
 
    @Administrator
    public void closeOrder(String orderId, String reason);
    
    @Administrator
    public List<String> isConfiguredForEhf(); 
    
    @Administrator
    public void registerSentEhf(String orderId);
    
    @Administrator
    public List<OrderTransaction> getBankOrderTransactions();
    
    @Administrator
    public void markOrderAsBillabe(String orderId);
    
    @Administrator
    public List<DayIncome> getDayIncomes(Date start, Date end);
    
    @Administrator
    public List<DayEntry> getDayEntriesForOrder(String orderId);
    
    @Administrator
    public void closeTransactionPeriode(Date closeDate);
    
    @Administrator
    public boolean isLocked(Date date);
    
    @Administrator
    public boolean isBankAccountClosed(Date date);
    
    @Administrator
    public PaymentTerminalInformation getTerminalInformation(String orderId);
    
    @Administrator
    public void resetLastMonthClose(String password, Date start, Date end);
    
    /**
     * This function is not in use anywhere so it can be deleted at any time.
     * 
     * just a function that fixes a problem that we had by resetting orders 
     * that has not been fully completed.
     * 
     * @param password 
     */
    @Administrator
    public void checkGroupInvoicing(String password);
    
    public void setNewStartIncrementalOrderId(long incrementalOrderId, String password);
    
    @Editor
    public List<Order> getOverdueInvoices(FilterOptions filterData);
    
    @Editor
    public Double getTotalOutstandingInvoices(FilterOptions filterData);
    
    @Editor
    public Double getTotalOutstandingInvoicesOverdue(FilterOptions filterData);
    
    @Editor
    public void changeProductOnCartItem(String orderId, String cartItemId, String productId);
    
    @Editor
    public void updateCartItemOnOrder(String orderId, CartItem cartItem);
    
    @Editor
    public void addOrderTransaction(String orderId, double amount, String comment, Date paymentDate);
    
    @Editor
    public List<OrderTransactionDTO> getAllTransactionsForInvoices(Date start, Date end);
    
    @Administrator
    public void changeAutoClosePeriodesOnZRepport(boolean autoClose);
    
    @Administrator
    public OrderManagerSettings getOrderManagerSettings();
    
    @Administrator
    public void closeBankAccount(Date endDate);
    
    @Administrator
    public List<OrderUnsettledAmountForAccount> getOrdersUnsettledAmount(String accountNumber, Date date);
    
    @Editor
    public List<PmiResult> getPmiResult(Date start, Date end);
    
    @Editor
    public List<DayIncome> getPaymentRecords(String paymentId, Date from, Date to);
    
    @Administrator
    public void createNewDoubleTransferFile(String paymentId, Date from, Date to);
    
    @Administrator
    public List<DoublePostAccountingTransfer> getAllDoublePostTransferFiles(String paymentId, Date from, Date to);
    
    @Administrator
    public DoublePostAccountingTransfer getDoublePostAccountingTransfer(String id);
}
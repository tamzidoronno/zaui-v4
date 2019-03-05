/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsMessage;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.system.GetShopSystem;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.ticket.Ticket;
import com.thundashop.core.ticket.TicketFilter;
import com.thundashop.core.ticket.TicketManager;
import com.thundashop.core.ticket.TicketState;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class DirectorManager extends ManagerBase implements IDirectorManager {
    
    public static String password = "adsfu9o21n4jl12n341kj2bn3asdfas0f9jqowierjqkljnr54lkbdslbflabfdkajbfdkafbdaskdfb";
    
    @Autowired
    private GetShopSessionScope scope; 
    
    @Autowired
    private SystemManager systemManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private StorePool storePool;
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private TicketManager ticketManager;
    
    private DirectorySyncUtils syncUtils;
    
    @Override
    public void syncFromOld() {
        syncUtils.createSystems();
        
        for (Company company : userManager.getAllCompanies()) {
            systemManager.getSystemsForCompany(company.id).stream()
                .forEach(system -> {
                    System.out.println("Syncing system " + system.id + ", for company: " + company.name);
                    try {
                        systemManager.syncSystem(system.id);
                    } catch (Exception ex) {
                        Logger.getLogger(DirectorManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
        }
        
    }
    
    @PostConstruct
    public void initted() {
        this.syncUtils = new DirectorySyncUtils(scope, systemManager, userManager, storePool);
    }
    
    @Override
    public DailyUsage getDailyUsage(String password, Date date) {
        checkPassword(password);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();
        
        List<SmsMessage> smses = messageManager.getAllSmsMessages(start, end);
        
        String defaultPrefix = getStoreDefaultPrefix();
        DailyUsage usage = new DailyUsage();
        
        usage.domesticSmses = smses.stream()
                .filter(o -> o.prefix.trim().equals(defaultPrefix))
                .mapToInt(o -> (int)Math.ceil((double)o.message.length() / (double)164))
                .sum();
        
        usage.internationalSmses = smses.stream()
                .filter(o -> !o.prefix.trim().equals(defaultPrefix))
                .mapToInt(o -> (int)Math.ceil((double)o.message.length() / (double)164))
                .sum();
        
        usage.ehfs = orderManager.getEhfSentLog(start, end).size();
        usage.start = start;
        usage.end = end;
        usage.belongsToStoreId = getStoreId();
        
        return usage;
    }

    private void checkPassword(String password1) throws ErrorException {
        if (!password1.equals(this.password)) {
            throw new ErrorException(26);
        }
    }

    @Override
    public Date getCreatedDate(String password) {
        checkPassword(password);
        return getStore().rowCreatedDate;
    }

    private Order createOrderForCompany(String companyId, boolean virtual) throws ErrorException {
        Company company = userManager.getCompany(companyId);
        
        if (company == null)
            return null;
        
        cartManager.clear();
        User mainCompanyUser = userManager.getMainCompanyUser(companyId);
        
        List<GetShopSystem> systems = systemManager.getSystemsForCompany(company.id);
        systems.forEach(system -> addSmsAndEhf(system, virtual, company));
        systems.forEach(system -> addMonthlyCost(system));
        
        if (mainCompanyUser != null) {
            addTickets(mainCompanyUser, virtual);
        }
        
        if (!cartManager.getCart().isNullCart()) {
            Order order;
            
            if (virtual) {
                order = orderManager.createVirtualOrder(mainCompanyUser.address, "").order;
            } else {
                order = orderManager.createOrder(mainCompanyUser.address);
            }
            
            order.payment.paymentId = "70ace3f0-3981-11e3-aa6e-0800200c9a66";
            order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
            
            if (mainCompanyUser != null) {
                order.userId = mainCompanyUser.id;
                addGroupInvoicing(mainCompanyUser, order, virtual);
            }
            
            order.sortCartByProducts();
            
            if (isNonNorwegian(company)) {
                order.changeAllTaxes(productManager.getTaxGroup(0));
            }
            
            if (!virtual) {
                order.currency = company.currency;
                order.language = company.language;
                saveObject(order);
                systems.stream().forEach(o -> setInvoicedToDate(o));
                orderManager.getOrder(order.id);
            }
            return order;
        }
        
        return null;
    }   

    private void addSmsAndEhf(GetShopSystem system, boolean virtual, Company company) {
        
        List<DailyUsage> dailyUsages = systemManager.getDailyUsage(system.id);
        
        Map<String, List<DailyUsage>> usageForMonthGroupedByMonth = 
                dailyUsages.stream()
                    .filter(o -> !o.hasBeenInvoiced())
                    .collect(Collectors.groupingBy(DailyUsage::getMonthAndYear));
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentMonth = cal.get(Calendar.YEAR)+" / "+String.format("%02d", (cal.get(Calendar.MONTH)+1));
        
        for (String periode : usageForMonthGroupedByMonth.keySet()) {
            if (periode.equals(currentMonth))
                continue;
            
            List<DailyUsage> usageForMonth = usageForMonthGroupedByMonth.get(periode);
            
            int smsInternational = 0;
            int smsDomestic = 0;
            int ehf = 0;

            for (DailyUsage usage : usageForMonth) {
                smsInternational += usage.internationalSmses;
                ehf += usage.ehfs;
                smsDomestic += usage.domesticSmses;
            }

            if (smsDomestic > 0) {
                CartItem item = cartManager.addProductItem("90621fc2-f584-4443-9918-9c4f87e59dd7", smsDomestic);
                item.getProduct().name += ": Periode "+periode + " ( " + system.webAddresses + " ) ";
                if (isNonNorwegian(company)) {
                    item.getProduct().price = 0.04 * 1.25;
                }
            }

            if (smsInternational > 0) {
                CartItem item = cartManager.addProductItem("2f7a5b3d-cd5d-4f9c-83ba-b8e7dbad9256", smsInternational);
                item.getProduct().name += ": Periode "+periode + " ( " + system.webAddresses + " )";
                
                if (isNonNorwegian(company)) {
                    item.getProduct().price = 0.04 * 1.25;
                }
            }

            if (ehf > 0) {
                CartItem item = cartManager.addProductItem("a4db77c8-441a-4a04-8887-7376e5d4df0c", ehf);
                item.getProduct().name += ": Periode "+periode + " ( " + system.webAddresses + " )";
            }
            
            if (!virtual) {
                usageForMonth.stream().forEach(usage -> systemManager.markUsageAsBilled(usage));
            }
        }
    }

    private void addMonthlyCost(GetShopSystem system) {
        if (ignoreSystem(system)) {
            return;
        }
        
        Date start = system.invoicedTo;
        Date end = getNextToDate(system);
        
        List<CartItem> items = new ArrayList();
        
        String periode = "";
        if (start == null) {
            periode = "To "+getMonthAndYear(end);
        } else {
            periode = getMonthAndYear(start)+" - "+getMonthAndYear(end);
        }
        CartItem item = new CartItem();
        item.setProduct(productManager.getProduct(system.productId));
        item.getProduct().description = "Periode: "+periode+" ("+system.webAddresses+")";
        item.getProduct().name += " Periode: "+ periode + " ("+system.webAddresses+")";
        item.setCount(1);
        item.getProduct().price = system.monthlyPrice * (item.getProduct().taxGroupObject.getTaxRate() + 1);
        items.add(item);
        
        cartManager.getCart().addCartItems(items);
    }

    private void addGroupInvoicing(User mainCompanyUser, Order order, boolean virtual) {
        List<Order> orders = orderManager.getAllOrdersForUser(mainCompanyUser.id)
                .stream()
                .filter(o -> o.status != Order.Status.PAYMENT_COMPLETED)
                .filter(o -> o.isSamleFaktura())
                .collect(Collectors.toList());
        
        for (Order iorder : orders) {
            order.cart.addCartItems(iorder.getCartItems());
            if (!virtual) {
                orderManager.creditOrder(iorder.id);
            }
        }
    }

    @Override
    public List<Order> createVirtualOrders() {
        List<Company> companies = userManager.getAllCompanies();
        List<Order> virtualOrders = new ArrayList();
        
        for (Company company : companies) {
            Order order = createOrderForCompany(company.id, true);
            if (order != null) {
                virtualOrders.add(order);
            }
        }
        
        return virtualOrders;
    }

    private boolean ignoreSystem(GetShopSystem system) {
        Date today = new Date();
        
        // Return if there current periode already has been invoiced.
        if (system.invoicedTo != null && today.before(system.invoicedTo)) {
            return true;
        }
        
        // Check if there is anything more to invoice for the system.
        if (system.isFinalInvoiced()) {
            return true;
        }
        
        return false;
    }

    private Date getNextToDate(GetShopSystem system) {
        Calendar cal = Calendar.getInstance();
        Date date = system.invoicedTo;
        if (date == null) {
            date = new Date();
        }
        
        cal.setTime(date);
        cal.add(Calendar.MONTH, system.numberOfMonthsToInvoice);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private String getMonthAndYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR);
    }

    private void addTickets(User mainCompanyUser, boolean virtual) {
        TicketFilter filter = new TicketFilter();
        filter.userId = mainCompanyUser.id;
        filter.state = TicketState.COMPLETED;
        ticketManager.getAllTickets(filter).stream()
            .filter(ticket -> !ticket.transferredToAccounting)
            .forEach(ticket -> {
                int seconds = (int) (ticket.timeInvoice * 60 * 60);
                String timeSpent = timeConversion(seconds);

                Product ticketProduct = getTicketProduct(ticket).clone();
                ticketProduct.price = (ticketProduct.price * ticket.timeInvoice);
                ticketProduct.name = "Support: " + ticket.incrementalId + " - " + ticket.title + " ( " + timeSpent + " )";
                CartItem item = cartManager.getCart().createCartItem(ticketProduct, 1);
                
                if (!virtual) {
                    ticketManager.markTicketAsTransferredToAccounting(ticket.id);
                }
            });
    }
    
    private Product getTicketProduct(Ticket ticket) {
        Product ticketProduct = productManager.getProduct("TICKET-" + ticket.type);
        if (ticketProduct == null) {
            ticketProduct = new Product();
            ticketProduct.id = "TICKET-" + ticket.type;
            ticketProduct.price = 1000;
            productManager.saveProduct(ticketProduct);
        }
        
        return ticketProduct;
    }
    
    private String timeConversion(int seconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int minutes = seconds / SECONDS_IN_A_MINUTE;
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        int hours = minutes / MINUTES_IN_AN_HOUR;
        minutes -= hours * MINUTES_IN_AN_HOUR;

        if (hours < 1) {
            return minutes + " minutes";
        }

        if (minutes < 1 && hours > 1) {
            return hours + " hours";
        }

        if (minutes < 1 && hours == 1) {
            return hours + " hour";
        }

        return hours + " hours and " + minutes + " minutes";
    }

    @Override
    public List<Order> createOrders() {
        List<Company> companies = userManager.getAllCompanies();
        List<Order> retOrders = new ArrayList();
        
        for (Company company : companies) {
            Order order = createOrderForCompany(company.id, false);
            if (order != null) {
                retOrders.add(order);
            }
        }
        
        return retOrders;
    }

    private void setInvoicedToDate(GetShopSystem o) {
        o.invoicedTo = getNextToDate(o);
        systemManager.saveSystem(o);
    }

    private boolean isNonNorwegian(Company company) {
        return company.currency != null && !company.currency.isEmpty() && !company.currency.toLowerCase().equals("nok");
    }
}
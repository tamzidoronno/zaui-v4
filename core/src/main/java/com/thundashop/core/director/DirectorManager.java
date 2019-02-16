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
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.system.GetShopSystem;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

//    @Override
//    public void createOrder(String companyId, int month, int year) {
//        createOrderForCompany(companyId, month, year, false);
//    }   

    private Order createOrderForCompany(String companyId, boolean virtual) throws ErrorException {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Company company = userManager.getCompany(companyId);
        if (company == null)
            return null;
        
        cartManager.clear();
        User mainCompanyUser = userManager.getUsersByCompanyId(companyId)
                .stream()
                .filter(o -> o.isCompanyMainContact)
                .findAny()
                .orElse(null);
        
        List<GetShopSystem> systems = systemManager.getSystemsForCompany(company.id);
        systems.forEach(system -> addSmsAndEhf(system));
        systems.forEach(system -> addMonthlyCost(system));
        
        if (!cartManager.getCart().isNullCart()) {
            Order order;
            
            if (virtual) {
                order = orderManager.createVirtualOrder(company.address, "").order;
            } else {
                order = orderManager.createOrder(company.address);
            }
            
            order.payment.paymentId = "70ace3f0-3981-11e3-aa6e-0800200c9a66";
            order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
            
            if (mainCompanyUser != null) {
                order.userId = mainCompanyUser.id;
                addGroupInvoicing(mainCompanyUser, order, virtual);
            }
            
            order.sortCartByProducts();
            
            if (!virtual) {
                saveObject(order);
            }
            return order;
        }
        
        return null;
    }   

    private void addSmsAndEhf(GetShopSystem system) {
        
        List<DailyUsage> dailyUsages = systemManager.getDailyUsage(system.id);
        
        Map<String, List<DailyUsage>> usageForMonthGroupedByMonth = dailyUsages.stream()
            .collect(Collectors.groupingBy(DailyUsage::getMonthAndYear));
        
        for (String periode : usageForMonthGroupedByMonth.keySet()) {
            
            List<DailyUsage> usageForMonth = usageForMonthGroupedByMonth.get(periode);
            
            int smsInternational = 0;
            int smsDomestic = 0;
            int ehf = 0;

            for (DailyUsage usage : usageForMonth) {
                if (usage.hasBeenInvoiced())
                    continue;

                smsInternational += usage.internationalSmses;
                ehf += usage.ehfs;
                smsDomestic += usage.domesticSmses;
            }

            if (smsDomestic > 0) {
                CartItem item = cartManager.addProductItem("90621fc2-f584-4443-9918-9c4f87e59dd7", smsDomestic);
                item.getProduct().description = "Year/Month: "+periode;
            }

            if (smsInternational > 0) {
                CartItem item = cartManager.addProductItem("2f7a5b3d-cd5d-4f9c-83ba-b8e7dbad9256", smsInternational);
                item.getProduct().description = "Year/Month: "+periode;
            }

            if (ehf > 0) {
                CartItem item = cartManager.addProductItem("a4db77c8-441a-4a04-8887-7376e5d4df0c", ehf);
                item.getProduct().description = "Year/Month: "+periode;
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
//        item.getProduct().name = "Periode: "+ periode + " ("+system.webAddresses+")";
        item.setCount(1);
        item.getProduct().price = system.monthlyPrice;
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
}
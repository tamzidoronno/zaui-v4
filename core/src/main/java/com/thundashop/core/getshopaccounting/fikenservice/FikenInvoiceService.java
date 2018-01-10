/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.getshopaccounting.AccountingSystemBase;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ktonder
 */
public class FikenInvoiceService extends FikenServiceBase {

    public FikenInvoiceService(AccountingSystemBase base, ProductManager productManager, UserManager userManager) {
        super(base, productManager, userManager);
    }

    public boolean createInvoice(Order order) {
        Resource<FikenCompany> company = getCompany();
        FikenBankAccount bankAccount = getBankAccount(company);
        
        FikenInvoiceCreateRequest invoice = new FikenInvoiceCreateRequest();
        FikenInvoiceCreateRequest.Customer customer = invoice.new Customer();
        
        customer.setUrl(getContactUrl(order, company));
        createOrderLine(company, order, invoice);
        
        invoice.setBankAccountUrl(bankAccount.getLink("self").getHref());
        invoice.setCash(false);
        invoice.setCustomer(customer);
        invoice.setUuid(UUID.randomUUID().toString());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        invoice.setDueDate(cal.getTime());
        invoice.setInvoiceText(order.invoiceNote);
        invoice.setIssueDate(new Date());
        invoice.setOurReference(""+order.incrementOrderId);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON_UTF8 }));
        headers.set("X-Request-ID", getRequestId());
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        String invoiceServiceUrl = company.getLink("https://fiken.no/api/v1/rel/create-invoice-service").getHref();
        HttpEntity<FikenInvoiceCreateRequest> request = new HttpEntity<>(invoice, headers);
        ResponseEntity<FikenInvoiceCreateRequest> response = restTemplate.exchange(invoiceServiceUrl, HttpMethod.POST, request, FikenInvoiceCreateRequest.class);
        return response.getStatusCode().equals(HttpStatus.CREATED);
    }

    private void createOrderLine(Resource<FikenCompany> company, Order order, FikenInvoiceCreateRequest invoice) {
        AccountingSystemBase base = getAccountingBaseSystem();
        
        for (CartItem item : order.cart.getItems()) {
            Product product = productManager.getProduct(item.getProduct().id);
            
            FikenInvoiceCreateRequest.OrderLine orderLine = invoice.new OrderLine();

            orderLine.setDescription(base.createLineText(item));
            orderLine.setProductUrl(getProductUrl(company, product));
            orderLine.setDiscountPercent(0);
            orderLine.setGrossAmount(calculateGrossAmount(item));
            orderLine.setNetAmount(calculateNetAmount(item));
            orderLine.setUnitNetAmount(calculateNetAmount(item)/item.getCount());
            orderLine.setQuantity(item.getCount());
            
            int vatAmount = (int) (orderLine.getGrossAmount() - orderLine.getNetAmount());
            orderLine.setVatAmount(vatAmount);
            orderLine.setVatType("HIGH");
            
            invoice.getLines().add(orderLine);
        }
    }
    
    public String getProductUrl(Resource<FikenCompany> company, Product product) {
        String contactUrlRel = company.getLink("https://fiken.no/api/v1/rel/products").getHref();
        return contactUrlRel +"/" + product.accountingSystemId;    
    }
    
    public Resource<FikenCompany> getCompany() {
        ResponseEntity<Resource<FikenCompany>> responseEntity
                = restTemplate.exchange("https://fiken.no/api/v1/companies/" + companyName, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<FikenCompany>>() {
                }, Collections.emptyMap());
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        
        return null;
    }

    private FikenBankAccount getBankAccount(Resource<FikenCompany> company) {
        String bankAccountName = this.getBankAccount();
        String url = company.getLink("https://fiken.no/api/v1/rel/bank-accounts").getHref();
        
        ResponseEntity<Resources<FikenBankAccount>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Resources<FikenBankAccount>>() {}, Collections.emptyMap());
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            for (FikenBankAccount bankAccount : responseEntity.getBody()) {
                if (bankAccount.getName().toLowerCase().equals(bankAccountName.toLowerCase())) {
                    return  bankAccount;
                }
            }
        }
        
        return null;
    }
   
    private int calculateGrossAmount(CartItem item) {
        int val = (int) Math.round(item.getProduct().price * item.getCount() * 100);
        return val;
    }

    private int calculateNetAmount(CartItem item) {
        double val = item.getProduct().priceExTaxes * item.getCount() * 100;
        return (int) Math.round(val);
    }

    private String getContactUrl(Order order, Resource<FikenCompany> company) {
        User user = userManager.getUserById(order.userId);
        
        if (user != null) {
            String contactUrlRel = company.getLink("https://fiken.no/api/v1/rel/contacts").getHref();
            return contactUrlRel +"/" + user.accountingId;    
        }
        
        return "";
        
    }

}

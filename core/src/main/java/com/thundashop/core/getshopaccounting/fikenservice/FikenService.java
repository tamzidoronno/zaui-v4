/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ktonder
 */
public class FikenService {

    private String username = "ktonder@gmail.com";
    private String password = "testtest";
    private String company = "fiken-demo-engelsk-og-nodvendig-ide-as";
    private final RestTemplate restTemplate;

    public FikenService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); 
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
        
        RestTemplate restTemplate = new BasicAuthRestTemplate("ktonder@gmail.com", "testtest");
        
        MappingJackson2HttpMessageConverter halConverter = new TypeConstrainedMappingJackson2HttpMessageConverter(ResourceSupport.class);
        halConverter.setObjectMapper(objectMapper);
        
        List<HttpMessageConverter<?>> existingConverters = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        newConverters.add(halConverter);
        newConverters.addAll(existingConverters);
        restTemplate.setMessageConverters(newConverters);
        
        this.restTemplate = restTemplate;
    }
    
    public void createInvoice() {
        Resource<FikenCompany> company = getCompany();
        
        String url = company.getLink("https://fiken.no/api/v1/rel/create-invoice-service").getHref();

        System.out.println(url);
        
        FikenInvoiceCreateRequest invoice = new FikenInvoiceCreateRequest();
        FikenInvoiceCreateRequest.Customer customer = invoice.new Customer();
        
        createOrderLine(invoice, customer);
        
        invoice.setBankAccountUrl("https://fiken.no/api/v1/companies/fiken-demo-engelsk-og-nodvendig-ide-as/bank-accounts/435495070");
        invoice.setCash(false);
        invoice.setCustomer(customer);
        invoice.setUuid(UUID.randomUUID().toString());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 14);
        invoice.setDueDate(cal.getTime());
        invoice.setInvoiceText("Test invoice");
        invoice.setIssueDate(new Date());
        invoice.setOurReference("Our reference");
        invoice.setYourReference("Your ref");
        
        String requestId = UUID.randomUUID().toString();
        System.out.println(requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON_UTF8 }));
        headers.set("X-Request-ID", requestId);
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        
        addInterceptor();
        HttpEntity<FikenInvoiceCreateRequest> request = new HttpEntity<>(invoice, headers);
        ResponseEntity<FikenInvoiceCreateRequest> response = restTemplate.exchange(url, HttpMethod.POST, request, FikenInvoiceCreateRequest.class);
        System.out.println(response);
    }

    private void addInterceptor() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new LoggingRequestInterceptor());
        this.restTemplate.setInterceptors(interceptors);
    }
    
    private void createOrderLine(FikenInvoiceCreateRequest invoice, FikenInvoiceCreateRequest.Customer customer) {
        FikenInvoiceCreateRequest.OrderLine orderLine = invoice.new OrderLine();
        customer.setUrl("https://fiken.no/api/v1/companies/fiken-demo-engelsk-og-nodvendig-ide-as/contacts/435415083");
        
        orderLine.setDescription("A simple description of the orderline");
        orderLine.setProductUrl("https://fiken.no/api/v1/companies/fiken-demo-engelsk-og-nodvendig-ide-as/products/435462076");
        orderLine.setDiscountPercent(0);
        orderLine.setGrossAmount(250);
        orderLine.setNetAmount(200);
        orderLine.setUnitNetAmount(200);
        orderLine.setVatAmount(50);
        orderLine.setVatType("HIGH");
        orderLine.setUnitNetAmount(200);
        invoice.getLines().add(orderLine);
    }
    
    
    public Resource<FikenCompany> getCompany() {
        ResponseEntity<Resource<FikenCompany>> responseEntity
                = restTemplate.exchange("https://fiken.no/api/v1/companies/" + company, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<FikenCompany>>() {
                }, Collections.emptyMap());
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        
        return null;
    }
    
    public static void main(String args[]) throws UnirestException, JSONException {
       FikenService fikenService = new FikenService();
       fikenService.createInvoice();
    }

   
}

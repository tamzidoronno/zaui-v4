/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thundashop.core.getshopaccounting.AccountingSystemBase;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ktonder
 */
public abstract class FikenServiceBase {
    protected String companyName = "fiken-demo-engelsk-og-nodvendig-ide-as";
    protected final RestTemplate restTemplate;
    protected ProductManager productManager;
    protected UserManager userManager;
    private final AccountingSystemBase base;

    protected String getRequestId() {
        return UUID.randomUUID().toString();
    }
    
    public FikenServiceBase(AccountingSystemBase base, ProductManager productManager, UserManager userManager) {
        this.companyName = base.getConfig("company");
        
        String username = base.getConfig("username");
        String password = base.getConfig("password");
        
        this.productManager = productManager;
        this.userManager = userManager;
        this.base = base;
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false); 
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
        
        RestTemplate restTemplate = new BasicAuthRestTemplate(username, password);
        
        MappingJackson2HttpMessageConverter halConverter = new TypeConstrainedMappingJackson2HttpMessageConverter(ResourceSupport.class);
        halConverter.setObjectMapper(objectMapper);
        
        List<HttpMessageConverter<?>> existingConverters = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        newConverters.add(halConverter);
        newConverters.addAll(existingConverters);
        restTemplate.setMessageConverters(newConverters);
        
        this.restTemplate = restTemplate;
    }
    
    protected  void addInterceptor() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new LoggingRequestInterceptor());
        this.restTemplate.setInterceptors(interceptors);
    }
    
    public String getBankAccount() {
        return base.getConfig("bankaccount");
    }
    
    protected AccountingSystemBase getAccountingBaseSystem() {
        return base;
    }
    
}

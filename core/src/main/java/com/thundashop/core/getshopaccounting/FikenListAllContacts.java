/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.getshopaccounting.fikenservice.BasicAuthRestTemplate;
import com.thundashop.core.getshopaccounting.fikenservice.FikenBankAccount;
import com.thundashop.core.getshopaccounting.fikenservice.FikenCompany;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ktonder
 */
public class FikenListAllContacts {
    public void run() {
        
        RestTemplate restTemplate = new BasicAuthRestTemplate("ktonder@gmail.com", "testtest");
        
        ResponseEntity<Resources<FikenContact>> responseEntity = restTemplate.exchange("https://fiken.no/api/v1/companies/getshop-as/contacts", HttpMethod.GET, null, new ParameterizedTypeReference<Resources<FikenContact>>() {
        
                }, Collections.emptyMap());
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            for (FikenContact contact : responseEntity.getBody().getContent()) {
            }
        }
        

    }
    
    
    public static void main(String[] args) {
        
        
        new FikenListAllContacts().run();
        
    }
}

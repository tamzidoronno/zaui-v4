/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.meca.data;

import java.util.Date;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author emil
 */
@Embedded
public class Vehicle {
    
    private String make;
    private String model;
    private String registration;
    private Integer year;
    private Date lastEuControlDate;
    private Date lastServiceDate;
    private Boolean euControlAlertActive;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getLastEuControlDate() {
        return lastEuControlDate;
    }

    public void setLastEuControlDate(Date lastEuControlDate) {
        this.lastEuControlDate = lastEuControlDate;
    }

    public Date getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(Date lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Boolean getEuControlAlertActive() {
        return euControlAlertActive;
    }

    public void setEuControlAlertActive(Boolean euControlAlertActive) {
        this.euControlAlertActive = euControlAlertActive;
    }
    
    
    
}

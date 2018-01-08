/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting.fikenservice;

/**
 *
 * @author ktonder
 */
public class FikenCompany {
    private String name;
    private String slug;
    private String organizationNumber;
    boolean templated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }
}

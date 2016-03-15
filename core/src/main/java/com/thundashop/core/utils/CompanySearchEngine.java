/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.usermanager.data.Company;
import java.util.List;

/**
 *
 * @author ktonder
 */
public interface CompanySearchEngine {
    public Company getCompany(String organisationNumber, boolean fetch);
    public String getName();
    public List<Company> search(String search);
}

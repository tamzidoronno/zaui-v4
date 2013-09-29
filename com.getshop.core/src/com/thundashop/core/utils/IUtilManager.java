/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.usermanager.data.Company;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IUtilManager {
    public Company getCompanyFromBrReg(String companyVatNumber) throws ErrorException;
}

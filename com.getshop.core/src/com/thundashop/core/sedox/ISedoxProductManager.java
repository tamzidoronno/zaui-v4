/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISedoxProductManager  {
    public SedoxProductSearchPage search(String searchString);
}

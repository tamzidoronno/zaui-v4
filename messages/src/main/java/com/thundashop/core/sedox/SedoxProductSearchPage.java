/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxProductSearchPage {
    public int pageNumber;
    public List<SedoxSharedProduct> products;
    public List<SedoxProduct> userProducts;
    public int totalPages;
}
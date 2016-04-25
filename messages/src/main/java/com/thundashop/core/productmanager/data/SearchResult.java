/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SearchResult {
    public List<Product> products = new ArrayList();
    public int pages = 0;
    public int pageNumber = 0;
}
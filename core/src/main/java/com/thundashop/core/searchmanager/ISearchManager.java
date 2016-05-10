/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.searchmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISearchManager {
    
    /**
     * Search for a word.
     * Returns a map where key is the content and value is the pageId.
     * @param searchWord
     * @return 
     */
    public HashMap<String, String> search(String searchWord);
    
}

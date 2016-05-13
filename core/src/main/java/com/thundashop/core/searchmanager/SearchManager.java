/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.searchmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.content.ContentManager;
import com.thundashop.core.common.ManagerBase;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SearchManager extends ManagerBase implements ISearchManager {
    
    @Autowired
    public ContentManager contentManager;

    @Override
    public HashMap<String, String> search(String searchWord) {
        searchWord = searchWord.toLowerCase();
        
        HashMap<String, String> searchResult = new HashMap();
        searchResult.putAll(contentManager.search(searchWord));
        return searchResult;
    }
    
    
}

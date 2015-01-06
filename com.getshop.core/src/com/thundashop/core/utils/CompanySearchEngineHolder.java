/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class CompanySearchEngineHolder {

    @Autowired
    private List<CompanySearchEngine> searchEngines;

    public CompanySearchEngine getSearchEngine(String storeId) {
        if (storeId == null) {
            return getSearchEngineByName("brreg");
        }

        if (storeId.equals("d27d81b9-52e9-4508-8f4c-afffa2458488")) {
            return getSearchEngineByName("eniro");
        }

        if (storeId.equals("2fac0e57-de1d-4fdf-b7e4-5f93e3225445")) {
            return getSearchEngineByName("brreg");
        }

        return null;
    }

    private CompanySearchEngine getSearchEngineByName(String name) {
        for (CompanySearchEngine engine : searchEngines) {
            if (engine.getName().equals(name)) {
                return engine;
            }
        }

        return null;
    }
}

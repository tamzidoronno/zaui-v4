/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CompanyProjectWorkPackageSettings {
    
    public Set<String> hourListIds = new HashSet();
    public Set<String> otherCostsIds = new HashSet();
    public List<C3ProjectContract> projectContracts = new ArrayList();

    void setProjectPrice(Date startDate, Date endDate, int price, String contractId) {
        C3ProjectContract contract = null;
        
        if (contractId != null && !contractId.isEmpty()) {
            contract = getContract(contractId);
        }
        
        if (contract == null) {
            contract = new C3ProjectContract();
            contract.id = UUID.randomUUID().toString();
            projectContracts.add(contract);
        }
        
        contract.startDate = startDate;
        contract.endDate = endDate;
        contract.contractValue = price;
    }

    int getCost(Date start, Date end) {
        for (C3ProjectContract contract : projectContracts) {
            if (contract.within(start, end)) {
                return contract.contractValue;
            }
        }
        
        return 0;
    }

    private C3ProjectContract getContract(String contractId) {
        for (C3ProjectContract contract : projectContracts) {
            if (contract.id.equals(contractId)) {
                return contract;
            }
        }
        
        return null;
    }

    void removeContract(String contractId) {
        if (contractId == null || contractId.isEmpty()) {
            projectContracts.removeIf(cont -> cont.id == null || cont.id.isEmpty());
            return;
        }
        
        C3ProjectContract contract = getContract(contractId);
        projectContracts.remove(contract);
    }
}

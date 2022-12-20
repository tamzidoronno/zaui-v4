package com.thundashop.services.productservice;

import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.repository.utils.SessionInfo;

import java.util.List;

public interface IProductService {
    List<TaxGroup> getAllTaxGroups(SessionInfo sessionInfo);
}

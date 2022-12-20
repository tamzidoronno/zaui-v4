package com.thundashop.repository.productrepository;

import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface ITaxGroupRepository  extends IRepository<TaxGroup> {
    TaxGroup getById(String id, SessionInfo sessionInfo);
}

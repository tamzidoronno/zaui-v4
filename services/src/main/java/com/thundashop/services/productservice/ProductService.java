package com.thundashop.services.productservice;

import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.repository.productrepository.ITaxGroupRepository;
import com.thundashop.repository.utils.SessionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class ProductService implements IProductService {
    private final ITaxGroupRepository taxGroupRepository;

    @Override
    public List<TaxGroup> getAllTaxGroups(SessionInfo sessionInfo) {
        return taxGroupRepository.getAll(sessionInfo);
    }
}

package com.thundashop.repository.gotohubrepository;

import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoConfigRepository extends IRepository<GoToConfiguration> {
    GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo) throws NotUniqueDataException;    
}

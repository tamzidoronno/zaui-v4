package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.repository.pmsmanager.PmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@GetShopSession
public class PmsLogManager extends ManagerBase implements IPmsLogManager {

    private final PmsLogRepository pmsLogRepository;

    @Autowired
    public PmsLogManager(PmsLogRepository pmsLogRepository) {
        this.pmsLogRepository = pmsLogRepository;
    }

    @Override
    public void save(PmsLog pmsLog) {
        pmsLogRepository.save(pmsLog, getSessionInfo());
    }

    @Override
    public List<PmsLog> query(PmsLog filter) {
        return pmsLogRepository.query(filter, getStoreIdInfo());
    }

}

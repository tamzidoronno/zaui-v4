package com.thundashop.repository.pmsmanager;

import java.util.List;

import com.thundashop.core.pmsmanager.PmsLog;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IPmsLogRepository extends IRepository<PmsLog>{
    List<PmsLog> query(PmsLog filter, SessionInfo sessionInfo);
}

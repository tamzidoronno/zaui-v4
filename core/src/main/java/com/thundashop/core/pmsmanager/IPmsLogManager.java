package com.thundashop.core.pmsmanager;

import java.util.List;

public interface IPmsLogManager {

    void save(PmsLog pmsLog);

    List<PmsLog> query(PmsLog filter);

}

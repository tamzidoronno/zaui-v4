package com.thundashop.core.pmsmanager;

import java.util.Date;
import java.util.List;

public interface IPmsLogManager {

    void save(PmsLog pmsLog);

    void logEntry(String logText, String bookingId, String itemId, PmsConfiguration configuration);

    void logEntry(String logText, String bookingId, String itemId, String roomId, String logType, PmsConfiguration configuration);

    void logEntryObject(PmsLog log, PmsConfiguration configuration);

    List<PmsLog> getLogEntries(PmsLog filter, Date start, Date end);
}

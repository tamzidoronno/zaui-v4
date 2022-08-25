package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.pmsmanager.PmsLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

@Component
@GetShopSession
public class PmsLogManager extends ManagerBase implements IPmsLogManager {

    private static final Logger logger = LoggerFactory.getLogger(PmsLogManager.class);

    private final PmsLogRepository pmsLogRepository;
    private final UserManager userManager;
    private final BookingEngine bookingEngine;
    private final MessageManager messageManager;

    @Autowired
    public PmsLogManager(PmsLogRepository pmsLogRepository, UserManager userManager,
                         BookingEngine bookingEngine, MessageManager messageManager) {
        this.pmsLogRepository = pmsLogRepository;
        this.userManager = userManager;
        this.bookingEngine = bookingEngine;
        this.messageManager = messageManager;
    }

    @Override
    public void save(PmsLog pmsLog) {
        pmsLogRepository.save(pmsLog, getSessionInfo());
    }

    @Override
    public List<PmsLog> query(PmsLog filter) {
        return pmsLogRepository.query(filter, getStoreIdInfo());
    }

    @Override
    public void logEntry(String logText, String bookingId, String itemId, PmsConfiguration configuration) {
        logEntry(logText, bookingId, itemId, null, "api", configuration);
    }

    @Override
    public void logEntry(String logText, String bookingId, String itemId, String roomId, String logType, PmsConfiguration configuration) {
        PmsLog log = new PmsLog();
        log.bookingId = bookingId;
        log.bookingItemId = itemId;
        log.roomId = roomId;
        log.logText = logText;
        log.logType = logType;
        logEntryObject(log, configuration);
    }

    @Override
    public void logEntryObject(PmsLog log, PmsConfiguration configuration) {
        if (isBlank(log.logText)) {
            return;
        }

        String userId = getCurrentUserId();
        User user = userManager.getUserById(userId);

        if (user != null) {
            log.userName = user.fullName;
        }

        if (log.bookingItemId != null) {
            BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
            if (item != null) {
                log.bookingItemType = item.bookingItemTypeId;
                log.roomName = item.bookingItemName;
            }
        }
        log.userId = userId;
        save(log);

        if (log.tag != null && log.tag.equals("mobileapp")) {
            List<String> emailsToNotify = configuration.emailsToNotify.get("applogentry");
            if (emailsToNotify != null) {
                for (String email : emailsToNotify) {
                    String text = "";
                    text += "<br/>Store email: " + getStoreEmailAddress();
                    text += "<br/>Store name: " + getStoreName();
                    text += "<br/>Store default address: " + getStoreDefaultAddress();
                    text += "<br/>Entry added:<br>" + log.logText;

                    messageManager.sendMailWithDefaults(email, email, "App log entry added", text);
                }
            }
        }
    }

    @Override
    public List<PmsLog> getLogEntries(PmsLog filter) {
        logger.debug("PmsLog for bookingId {} filter {}", filter.bookingId, filter);
        List<PmsLog> logEntries = query(filter);
        logger.debug("PmsLogs found for bookingId {} count {}", filter.bookingId, logEntries.size());

        for (PmsLog log : logEntries) {
            if (isEmpty(log.userName)) {
                User user = userManager.getUserById(log.userId);
                if (user != null) {
                    log.userName = user.fullName;
                }
            }

            if (isEmpty(log.roomName) && isNotEmpty(log.bookingItemId)) {
                BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
                if (item != null) {
                    log.roomName = item.bookingItemName;
                }
            }
        }

        return logEntries;
    }
}

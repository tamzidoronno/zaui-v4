package com.thundashop.core.channelmanagerexperiment.wubook;

import com.mongodb.BasicDBObject;
import com.thundashop.core.databasemanager.WubookLogCleanerDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

/**
 * This is not a distributed task scheduler. Per application instance will run its own scheduler.
 * If there is a need for a distributed task scheduler, look into the Quartz project.
 */

@Service
public class WubookLogCleanerSchedulerManager {

    private static final Logger logger = LoggerFactory.getLogger(WubookLogCleanerSchedulerManager.class);

    private final String MANAGER;

    private final WubookLogCleanerDB database;

    @Autowired
    public WubookLogCleanerSchedulerManager(WubookLogCleanerDB wubookLogCleanerDB) {
        this.database = wubookLogCleanerDB;
        this.MANAGER = WubookLogManager.getManager();
    }

    public int delete() {
        return database.getAllCollection(MANAGER)
                .stream()
                .mapToInt(this::deleteByQuery)
                .sum();
    }

    private int deleteByQuery(String collectionName) {
        long cutOff = WubookLogManager.getCutOff();
        int count = database.deleteByQuery(MANAGER, collectionName, new BasicDBObject("timeStamp", new BasicDBObject("$lt", cutOff)));
        logger.debug("Deleted {} entries from collection {}", count, collectionName);
        return count;
    }

    // initialDeal = 60 minutes, fixedDelay = 360 minutes
    @Scheduled(initialDelay = 3_600_000, fixedDelay = 21_600_000)
    public void wubookLogCleanerScheduler() {
        try {
            int deleteCount = delete();
            logger.debug("At {} , {} documents deleted from wubookLogManager database", now(), deleteCount);
        } catch (Exception e) {
            logger.error("Error while deleting wubook log", e);
        }
    }

}

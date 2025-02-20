package com.thundashop.core.jomres;

import static java.time.LocalDateTime.now;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.thundashop.core.databasemanager.JomresLogCleanerDB;

import lombok.extern.slf4j.Slf4j;

/**
 * This is not a distributed task scheduler. Per application instance will run its own scheduler.
 * If there is a need for a distributed task scheduler, look into the Quartz project.
 */

@Service
@Slf4j
public class JomresLogCleanerSchedulerManager {
    private final String MANAGER;

    private final JomresLogCleanerDB database;

    @Autowired
    public JomresLogCleanerSchedulerManager(JomresLogCleanerDB jomresLogCleanerDB) {
        this.database = jomresLogCleanerDB;
        this.MANAGER = JomresLogManager.getManager();
    }

    public int delete() {
        return database.getAllCollection(MANAGER)
                .stream()
                .mapToInt(this::deleteByQuery)
                .sum();
    }

    private int deleteByQuery(String collectionName) {
        long cutOff = JomresLogManager.getCutOff();
        int count = database.deleteByQuery(MANAGER, collectionName, new BasicDBObject("timeStamp", new BasicDBObject("$lt", cutOff)));
        log.info("Deleted {} entries from collection {}", count, collectionName);
        return count;
    }

    // initialDeal = 60 minutes, fixedDelay = 360 minutes
    @Scheduled(initialDelay = 3_600_000, fixedDelay = 21_600_000)
    public void jomresLogCleanerScheduler() {
        try {
            int deleteCount = delete();
            log.info("At {} , {} documents deleted from jomresLogManager database", now(), deleteCount);
        } catch (Exception e) {
            log.error("Error while deleting jomres log", e);
        }
    }

}

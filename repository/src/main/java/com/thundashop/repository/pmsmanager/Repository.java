package com.thundashop.repository.pmsmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.common.SessionInfo;
import com.thundashop.repository.db.Database;

import java.util.Date;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class Repository {

    private final Database database;
    private final String dbName;

    public Repository(Database database, String dbName) {
        this.database = database;
        this.dbName = dbName;
    }

    public Database getDatabase() {
        return database;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCollectionName(SessionInfo sessionInfo) {
        return "col_" + sessionInfo.getStoreId();
    }

    public DataCommon save(DataCommon dataCommon, SessionInfo sessionInfo) {
        if (isEmpty(dataCommon.id)) {
            dataCommon.id = UUID.randomUUID().toString();
        }

        if (dataCommon.rowCreatedDate == null) {
            dataCommon.rowCreatedDate = new Date();
        }

        if (isNotEmpty(sessionInfo.getCurrentUserId())) {
            dataCommon.lastModifiedByUserId = sessionInfo.getCurrentUserId();
        }

        dataCommon.storeId = sessionInfo.getStoreId();
        dataCommon.lastModified = new Date();
        dataCommon.gs_manager = getDbName();

        if (isNotEmpty(sessionInfo.getLanguage())) {
            String lang = sessionInfo.getLanguage();
            dataCommon.validateTranslationMatrix();
            dataCommon.updateTranslation(lang);
        }

        return database.save(dbName, getCollectionName(sessionInfo), dataCommon);
    }

}

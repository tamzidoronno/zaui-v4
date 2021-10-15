package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public abstract class Repository<T> {

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
        dataCommon.colection = getCollectionName(sessionInfo);

        if (isNotEmpty(sessionInfo.getLanguage())) {
            String lang = sessionInfo.getLanguage();
            dataCommon.validateTranslationMatrix();
            dataCommon.updateTranslation(lang);
        }

        return database.save(dbName, getCollectionName(sessionInfo), dataCommon);
    }

    protected Optional<T> getSingle(List<T> resultList, Supplier<String> notUniqueExceptionMessage) {
        if (resultList.size() > 1) {
            throw new NotUniqueDataException(notUniqueExceptionMessage.get());
        }

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    public Optional<T> findById(String id, Class<T> entityClass, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject("_id", id);
        List<T> result = getDatabase().query(getDbName(), getCollectionName(sessionInfo), entityClass, query);
        return getSingle(result, () -> "Found more than one entity by id " + id + " entityClass: " + entityClass.getName());
    }

}

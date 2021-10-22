package com.thundashop.repository.common;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public T save(DataCommon dataCommon, SessionInfo sessionInfo) {
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

        return (T) database.save(dbName, getCollectionName(sessionInfo), dataCommon);
    }

    protected Optional<T> getOne(DBObject query, Class<T> entityClass, SessionInfo sessionInfo) {
        List<T> resultList = getDatabase().query(getDbName(), getCollectionName(sessionInfo), entityClass, query);

        if (resultList.size() > 1) {
            throw new NotUniqueDataException(String.format("Found multiple data count: %s , entity: %s , query: %s",
                    resultList.size(), entityClass.getName(), query));
        }

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    public Optional<T> findById(String id, Class<T> entityClass, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject("_id", id);
        return getOne(query, entityClass, sessionInfo);
    }

    protected boolean exist(DBObject query, Class<T> entityClass, SessionInfo sessionInfo) {
        return !getDatabase().query(getDbName(), getCollectionName(sessionInfo), entityClass, query).isEmpty();
    }

    public int markDeletedByQuery(DBObject query, SessionInfo sessionInfo) {
        DBObject updateFields = new BasicDBObject().append("deleted", new Date()).append("gsDeletedBy", sessionInfo.getCurrentUserId());
        DBObject setQuery = new BasicDBObject("$set", updateFields);
        return getDatabase().updateMultiple(getDbName(), getCollectionName(sessionInfo), query, setQuery);
    }

    public <U> List<U> distinct(String field, DBObject query, SessionInfo sessionInfo) {
        return getDatabase().distinct(getDbName(), getCollectionName(sessionInfo), field, query);
    }

}

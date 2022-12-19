package com.thundashop.repository.baserepository;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

public abstract class Repository<T> implements IRepository<T> {
    
    private final Database database;

    protected abstract String getClassName();

    public Repository(Database database) {
        this.database = database;
    }    

    public Database getDatabase() {
        return database;
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
        dataCommon.gs_manager = sessionInfo.getManagerName();
        dataCommon.colection = getCollectionName(sessionInfo);

        if (isNotEmpty(sessionInfo.getLanguage())) {
            String lang = sessionInfo.getLanguage();
            dataCommon.validateTranslationMatrix();
            dataCommon.updateTranslation(lang);
        }

        return (T) database.save(sessionInfo.getManagerName(), getCollectionName(sessionInfo), dataCommon);
    }
  
    public List<T> getAll(SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("deleted", null);
        return getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query);
    }

    public Optional<T> getFirst(DBObject query, SessionInfo sessionInfo) {
        T result = getDatabase().findFirst(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    public Optional<T> getOne(DBObject query, SessionInfo sessionInfo) throws NotUniqueDataException {
        List<T> resultList = getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query);

        if (resultList.size() > 1) {
            throw new NotUniqueDataException(String.format("Found multiple data count: %s , entity: %s , query: %s",
                    resultList.size(), getClassName(), query));
        }

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    public Optional<T> findById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject("_id", id);
        return getFirst(query, sessionInfo);
    }
    

    public boolean exist(DBObject query, SessionInfo sessionInfo) {
        return !getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query).isEmpty();
    }

    public int markDeletedByQuery(DBObject query, SessionInfo sessionInfo) {
        DBObject updateFields = new BasicDBObject().append("deleted", new Date()).append("gsDeletedBy", sessionInfo.getCurrentUserId());
        DBObject setQuery = new BasicDBObject("$set", updateFields);
        return getDatabase().updateMultiple(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query, setQuery);
    }

    public <U> List<U> distinct(String field, DBObject query, SessionInfo sessionInfo) {
        return getDatabase().distinct(sessionInfo.getManagerName(), getCollectionName(sessionInfo), field, query);
    }

}

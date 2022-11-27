package com.thundashop.repository.baserepository;

import java.util.List;
import java.util.Optional;

import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

public interface IRepository<T> {
    Database getDatabase();
    String getCollectionName(SessionInfo sessionInfo);
    List<T> getAll(SessionInfo sessionInfo);
    Optional<T> getFirst(DBObject query, SessionInfo sessionInfo);
    Optional<T> getOne(DBObject query, SessionInfo sessionInfo) throws NotUniqueDataException;
    T save(DataCommon dataCommon, SessionInfo sessionInfo);
    Optional<T> findById(String id, SessionInfo sessionInfo);        
    boolean exist(DBObject query, SessionInfo sessionInfo);
    int markDeletedByQuery(DBObject query, SessionInfo sessionInfo);
    <U> List<U> distinct(String field, DBObject query, SessionInfo sessionInfo);    
}

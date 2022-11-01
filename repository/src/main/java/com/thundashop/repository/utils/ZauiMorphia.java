package com.thundashop.repository.utils;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MappingException;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.db.BigDecimalConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZauiMorphia {    
    private final Morphia morphia;

    public ZauiMorphia(){
        this.morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
        morphia.map(DataCommon.class);
    }

    public DataCommon fromDBObject(DBObject object){
        try{            
            return morphia.fromDBObject(DataCommon.class, object);
        }
        catch(MappingException ex){
            log.error("Morphia mapping exception occured. Exception: {0}", ex);
            log.error("Failed to map DBObject to data. DBObject: {0}", object.toString());
            throw ex;
        }
    }

    public DBObject toDBObject(DataCommon data){        
        try{            
            return morphia.toDBObject(data);
        }
        catch(MappingException ex){
            log.error("Morphia mapping exception occured. Exception: {0}", ex);
            log.error("Failed to map data to DBObject. Data: {0}", data.toString());
            throw ex;
        }
    }
}

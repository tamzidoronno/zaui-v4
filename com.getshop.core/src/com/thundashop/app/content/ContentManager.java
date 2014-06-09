package com.thundashop.app.content;

import com.thundashop.app.contentmanager.data.ContentData;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author privat
 */
@Component
@Scope("prototype")
public class ContentManager extends ManagerBase implements IContentManager {
    private int id=1002;
    
    HashMap<String, ContentData> memory = new HashMap<String, ContentData>();
    
    @Autowired
    public ContentManager(DatabaseSaver databaseSaver, Logger logger) throws ErrorException {
        super(logger, databaseSaver);
    }    

    @Override
    public void dataFromDatabase(DataRetreived data) {
         List<DataCommon> theData = data.data;
        for(int i = 0; i < theData.size(); i++) {
            if(theData.get(i) instanceof ContentData) {
                ContentData data2 = (ContentData) theData.get(i);
                memory.put(data2.appId, data2);
            }
        }
    }

    @Override
    public void saveContent(String id, String content) throws ErrorException {
        ContentData data = null;
        String contentId = id;
        if(memory.get(contentId) != null) {
            data = memory.get(contentId);
        } else {
            data = new ContentData();
        }
        data.appId = contentId;
        data.storeId = storeId;
        
        data.content = content;
        databaseSaver.saveObject(data, credentials);
        memory.put(contentId,data);
    }

    @Override
    public void deleteContent(String id) throws ErrorException {
        ContentData data = getContentData(id);
        databaseSaver.deleteObject(data, credentials);
        memory.remove(id);
    }
 
   @Override
    public String createContent(String content) throws ErrorException {
        String id = UUID.randomUUID().toString();
        saveContent(id, content);
        return id;
    }

    @Override
    public String getContent(String id) throws ErrorException {
        ContentData object = getContentData(id);
        return object.content;
    }

    @Override
    public HashMap<String,String> getAllContent() throws ErrorException {
        HashMap<String,String> result = new HashMap();
        int i = 0;
        for(String key : memory.keySet()) {
            result.put(key, memory.get(key).content);
        }
        
        return result;
    }

    private ContentData getContentData(String id) throws ErrorException {
        if(memory.get(id) == null) {
            memory.put(id, new ContentData());
            memory.get(id).content = "";
            memory.get(id).appId = id;
        }
        
        return memory.get(id);
    }
    
}

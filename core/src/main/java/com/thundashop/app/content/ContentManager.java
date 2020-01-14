package com.thundashop.app.content;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.contentmanager.data.ContentData;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.owasp.validator.html.CleanResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author privat
 */
@Component
@GetShopSession
public class ContentManager extends ManagerBase implements IContentManager {
    private int id=1002;

    private Map<String, HashMap<String, ContentData>> moduleMemory = new HashMap();
    private HashMap<String, ContentData> memory = new HashMap();
    
    private GetShopModules modules = new GetShopModules();
    
    @Autowired
    public PageManager pageManager;
    
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
         List<DataCommon> theData = data.data;
        for(int i = 0; i < theData.size(); i++) {
            if(theData.get(i) instanceof ContentData) {
                ContentData data2 = (ContentData) theData.get(i);
                memory.put(data2.appId, data2);
            }
        }
        
        try {
            loadRemoteData();
        }catch(Exception e) {
            
        }
    }

    @Override
    public void saveContent(String id, String content) throws ErrorException {
        ContentData data = null;
        String contentId = id;
        if(getMemoryForCurrentModule().get(contentId) != null) {
            data = getMemoryForCurrentModule().get(contentId);
        } else {
            data = new ContentData();
        }
        data.appId = contentId;
        data.storeId = storeId;
        
        data.content = content;
        saveObject(data);
        getMemoryForCurrentModule().put(contentId, data);
    }

    @Override
    public void deleteContent(String id) throws ErrorException {
        ContentData data = getContentData(id);
        deleteObject(data);
        getMemoryForCurrentModule().remove(id);
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
        object.updateTranslation(getSession().language);
        return object.content;
    }

    @Override
    public HashMap<String,String> getAllContent() throws ErrorException {
        HashMap<String,String> result = new HashMap();
        int i = 0;
        for(String key : getMemoryForCurrentModule().keySet()) {
            result.put(key, getMemoryForCurrentModule().get(key).content);
        }
        
        return result;
    }

    private ContentData getContentData(String id) throws ErrorException {
        if(getMemoryForCurrentModule().get(id) == null) {
            getMemoryForCurrentModule().put(id, new ContentData());
            getMemoryForCurrentModule().get(id).content = "";
            getMemoryForCurrentModule().get(id).appId = id;
        }
        
        return getMemoryForCurrentModule().get(id);
    }   

    public Map<String, String> search(String searchWord) {
        List<ContentData> res = getMemoryForCurrentModule().values().stream()
                .filter(contentData -> contentData.content.toLowerCase().contains(searchWord))
                .collect(Collectors.toList());
        
        HashMap<String, String> returnMap = new HashMap();
        
        for (ContentData data : res) {
            String pageId = pageManager.getPagesForApplicationOnlyBody(data.appId).stream().findFirst().orElse(null);
            if (pageId != null) {
                String content = getSubContent(data, searchWord);
                returnMap.put(content, pageId);
            }
        }
        
        return returnMap;
    }

    private String getSubContent(ContentData data, String searchWord) {
        String content = data.content.toLowerCase();
        content = Jsoup.parse(content).text();
        
        String[] res = content.split(searchWord, id);
        
        if (res.length == 0) {
            return "";
        }
        
        if (res.length == 1) {
            return res[0];
        }
        
        String start = res[0];
        String end = res[1];
        
        int stringLength = 100;
        
        if (start.length() > stringLength) {
            start = start.substring(start.length() - stringLength, start.length());
        }
        
        if (end.length() > stringLength) {
            end = end.substring(0, stringLength);
        }
        
        String word = start + "<b>" + searchWord + "</b>" + end;
        return word;
    }
    
    private HashMap<String, ContentData> getMemoryForCurrentModule() {
        if (isCmsModule()) {
            return memory;
        }
        
        if (moduleMemory.get(getCurrentGetShopModule()) == null) {
            moduleMemory.put(getCurrentGetShopModule(), new HashMap());
        }
        
        return moduleMemory.get(getCurrentGetShopModule());
    }
    
    
    private void loadRemoteData() {
        modules.getModules().stream().forEach(m -> {
            databaseRemote.getAll("ContentManager", "all", m.id).forEach(o -> {
                if (o instanceof ContentData) {
                    ContentData instance = (ContentData)o;
                    if (moduleMemory.get(instance.getshopModule) == null) {
                        moduleMemory.put(instance.getshopModule, new HashMap());
                    }
                    moduleMemory.get(instance.getshopModule).put(instance.appId, instance);
                }
            });
        });
    }
}
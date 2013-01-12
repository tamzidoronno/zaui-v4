package com.thundashop.app.content;

import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ContentManagerCache implements IContentManager {
    private ContentManager manager;
    private final String addr;

    public ContentManagerCache(ContentManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    @Override
    public void saveContent(String id, String content) throws ErrorException {
        getContent(id);
    }

    @Override
    public void deleteContent(String id) throws ErrorException {
        getContent(id);
    }

    @Override
    public String createContent(String content) throws ErrorException {
        return null;
    }

    @Override
    public String getContent(String id) throws ErrorException {
        CachingKey key = new CachingKey();
        String result = manager.getContent(id);
        
        LinkedHashMap<String, Object> keys = new LinkedHashMap();
        keys.put("id", id);
        
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = "";
        key.method = "getContent";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    @Override
    public HashMap<String, String> getAllContent() throws ErrorException {
        return null;
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }
    
}

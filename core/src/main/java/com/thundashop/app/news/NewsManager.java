/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.news;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.newsmanager.data.MailSubscription;
import com.thundashop.app.newsmanager.data.NewsEntry;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.mobilemanager.MobileManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.usermanager.UserManager;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class NewsManager extends ManagerBase implements INewsManager {
    public HashMap<String, NewsEntry> entries = new HashMap();
    public HashMap<String, MailSubscription> subscribers = new HashMap();
    
    @Autowired
    private MobileManager mobileManager;

    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private UserManager userManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon retData : data.data) {
            if (retData instanceof NewsEntry) {
                NewsEntry newEntry = (NewsEntry)retData;
                entries.put(newEntry.id, newEntry);
            }
            if (retData instanceof MailSubscription) {
                MailSubscription subscription = (MailSubscription)retData;
                subscribers.put(subscription.id, subscription);
            }
        }
    }

    public List<MailSubscription> getNewsSubscriptors() {
        return new ArrayList<MailSubscription>(subscribers.values());
    }

    @Override
    public List<NewsEntry> getAllNews() throws ErrorException {
        List<NewsEntry> data;
        if (entries != null) {
            data = new ArrayList<NewsEntry>(entries.values());
        } else {
            data = new ArrayList();
        }

        //Sort the list before returning
        Collections.sort(data, new Comparator<NewsEntry>() {
            @Override
            public int compare(NewsEntry o1, NewsEntry o2) {
                if (o1.date == null || o2.date == null) {
                    return 1;
                }
                if (o1.date.getTime() < o2.date.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        finalizeList(data);
        return data;
    }

//    @Override
    public String generateNewsGroupId() throws ErrorException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String addNews(NewsEntry entry) throws ErrorException {
        entry.storeId = storeId;
        saveObject(entry);
        entries.put(entry.id, entry);
        return entry.id;
    }

    @Override
    public void deleteNews(String id) throws ErrorException {
        NewsEntry entry = entries.remove(id);
        deleteObject(entry);
    }

    @Override
    public MailSubscription addSubscriber(String email) throws ErrorException {
        return addSubscriberEntry(email);
    }

    @Override
    public void removeSubscriber(String id) throws ErrorException {
        MailSubscription subscriber = findMailSubscriber(id);
        removeSubscriberEntry(subscriber);
    }

    @Override
    public List<MailSubscription> getAllSubscribers() throws ErrorException {
        return new ArrayList<MailSubscription>(subscribers.values());
    }

    private MailSubscription addSubscriberEntry(String email) throws ErrorException {
        MailSubscription sub = new MailSubscription();
        sub.email = email;
        sub.storeId = storeId;
        saveObject(sub);
        
        if(subscribers == null) {
            subscribers = new HashMap();
        }
        
        subscribers.put(sub.id, sub);
        
        return sub;
    }

    private MailSubscription findMailSubscriber(String id) {
        for(MailSubscription sub : subscribers.values()) {
            if(sub.id.equals(id)) {
                return sub;
            }
        }
        return null;
    }

    private void removeSubscriberEntry(MailSubscription subscriber) throws ErrorException {
        deleteObject(subscriber);
        subscribers.remove(subscriber);
    }

    @Override
    public void publishNews(String id) throws ErrorException {
        NewsEntry entry = entries.get(id);
        mobileManager.sendMessageToAll(entry.subject);
        entry.isPublished = true;
        saveObject(entry);
    }

    private void finalizeList(List<NewsEntry> data) {
        for(NewsEntry entry : data) {
            finalize(entry);
        }
    }

    private void finalize(NewsEntry entry) {
        if(entry.pageId == null || entry.pageId.isEmpty()) {
            Page templatePage = pageManager.createPageFromTemplatePage("news_template_1");
            entry.pageId = templatePage.id;
            if(entry.userId != null && !entry.userId.isEmpty()) {
                entry.usersName = userManager.getUserById(entry.userId).fullName;
            }
            saveObject(entry);
        }
    }

    @Override
    public NewsEntry getNewsForPage(String id) throws ErrorException {
        for(NewsEntry entry : entries.values()) {
            if(entry.pageId.equals(id)) {
                return entry;
            }
        }
        return null;
    }
}

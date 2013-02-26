/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.news;

import com.thundashop.app.newsmanager.data.MailSubscription;
import com.thundashop.app.newsmanager.data.NewsEntry;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class NewsManager extends ManagerBase implements INewsManager {
    public HashMap<String, NewsEntry> entries = new HashMap();
    public HashMap<String, MailSubscription> subscribers = new HashMap();
    
    @Autowired
    public NewsManager(DatabaseSaver databaseSaver, Logger logger) throws ErrorException {
        super(logger, databaseSaver);
    }

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

        return data;
    }

//    @Override
    public String generateNewsGroupId() throws ErrorException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String addNews(String news) throws ErrorException {
        NewsEntry entry = createNewsEntry(news);
        return entry.id;
    }

    @Override
    public void deleteNews(String id) throws ErrorException {
        deleteNewsEntry(getNewsEntry(id));
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

    private NewsEntry createNewsEntry(String news) throws ErrorException {
        if (entries == null) {
            entries = new HashMap();
        }
        
        NewsEntry entry = new NewsEntry();
        entry.content = news;
        entry.date = new Date();
        entry.storeId = storeId;
        
        databaseSaver.saveObject(entry, credentials);
        entries.put(entry.id, entry);
        
        return entry;
    }

    private NewsEntry getNewsEntry(String id) {
        for(NewsEntry entry : entries.values()) {
            if(entry.id.equals(id)) {
                return entry;
            }
        }
        return null;
    }

    private void deleteNewsEntry(NewsEntry newsEntry) throws ErrorException {
        entries.remove(newsEntry);
        databaseSaver.deleteObject(newsEntry, credentials);
    }

    private MailSubscription addSubscriberEntry(String email) throws ErrorException {
        MailSubscription sub = new MailSubscription();
        sub.email = email;
        sub.storeId = storeId;
        databaseSaver.saveObject(sub, credentials);
        
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
        databaseSaver.deleteObject(subscriber, credentials);
        subscribers.remove(subscriber);
    }
}

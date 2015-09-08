package com.thundashop.app.news;

import com.thundashop.app.newsmanager.data.MailSubscription;
import com.thundashop.app.newsmanager.data.NewsEntry;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Writing;
import java.util.List;

/**
 * Create news / delete news, subscribe for them or unsubscribe for them.<br>
 * This manager help you do all that, and when news are being added, it will automatically send emails to the registered subscribers. <br>
 */
@GetShopApi
public interface INewsManager {
    
    /**
     * Fetch all news added.
     * @return
     * @throws ErrorException 
     */
    public List<NewsEntry> getAllNews() throws ErrorException;
    
    /**
     * Add a new news entry.
     * @param news The news object to add.
     * @return The id for this news entry.
     * @throws ErrorException 
     */
    @Administrator
    @Writing
    public String addNews(NewsEntry newsEntry) throws ErrorException;
    
    /**
     * Delete a given news id.
     * @param id The id for the news to delete.
     * @throws ErrorException 
     */
    @Administrator
    public void deleteNews(String id) throws ErrorException;
    
    /**
     * Add a subscriber.
     * Whenever a new news is updated to this, the subscribe will get an email.
     * @param email The email address for the subscriber.
     * @return
     * @throws ErrorException 
     */
    public MailSubscription addSubscriber(String email) throws ErrorException;
    
    /**
     * Remove an existing subscriber.
     * @param subscriberId The subscribers id found in the MailSubscriber object.
     * @return
     * @throws ErrorException 
     */
    public void removeSubscriber(String subscriberId) throws ErrorException;
    
    /**
     * Get all subscribers.
     * @return
     * @throws ErrorException 
     */
    public List<MailSubscription> getAllSubscribers() throws ErrorException;
    
    /**
     * Publishing news.
     * 
     * @param id
     * @throws ErrorException 
     */
    @Administrator
    @Writing
    public void publishNews(String id) throws ErrorException;
}

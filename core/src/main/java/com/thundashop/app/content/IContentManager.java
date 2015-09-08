package com.thundashop.app.content;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Writing;
import java.util.HashMap;

/**
 * The content manager helps you keep text configuration. With this, you will be able
 * to content like html / text and push it somewhere you like to your page.
 */
@GetShopApi
public interface IContentManager {
    /**
     * Update / replace the content for a given id.
     * @param id The id to update the content for.
     * @param content The content to update. This could be html / text.
     * @throws ErrorException
     */
    @Administrator
    @Writing
    public void saveContent(String id, String content) throws ErrorException;
    
    /**
     * Remove the content for a given id.
     * @param id The id to remove the content for.
     * @throws ErrorException
     */
    @Administrator
    @Writing
    public void deleteContent(String id) throws ErrorException;
    
    /**
     * Create a new instance for the content manager.<br>
     * An id will automatically be generated and returned on creation.<br>
     * 
     * @param content The content to build upon.
     * @return The id for the new content.
     * @throws ErrorException
     */
    @Administrator
    @Writing
    public String createContent(String content) throws ErrorException;
    
    /**
     * Fetch the content for a given id.
     * @param id The id which is identifying the content.
     * @return
     * @throws ErrorException
     */
    public String getContent(String id) throws ErrorException;
    
    /**
     * Fetch all content saved until now.
     * @return A list of all content accosiated with its given id.
     * @throws ErrorException 
     */
    public HashMap<String,String> getAllContent() throws ErrorException;
}

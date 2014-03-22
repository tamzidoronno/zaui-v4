package com.thundashop.core.messagemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

@GetShopApi
public interface INewsLetterManager {
    /**
     * Calling this function will start sending newsletter with a five minute interval for all recipients.
     * @param group 
     */
    public void sendNewsLetter(NewsLetterGroup group) throws ErrorException;
    
    /**
     * Send a preview to the selected contacts.
     * @param group 
     */
    public void sendNewsLetterPreview(NewsLetterGroup group) throws ErrorException;
}

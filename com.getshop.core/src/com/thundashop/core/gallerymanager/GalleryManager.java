/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gallerymanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.gallerymanager.data.ImageEntry;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class GalleryManager extends ManagerBase implements IGalleryManager {

    public HashMap<String, List<ImageEntry>> entries = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon entry : data.data) {
            if(entry instanceof ImageEntry) {
                ImageEntry theEntry = (ImageEntry) entry;
                List<ImageEntry> collection = getCollection(theEntry.appId);
                collection.add(theEntry);
            }
        }
    }

    private List<ImageEntry> getCollection(String listId) {
        List<ImageEntry> collection = entries.get(listId);
        if(collection == null) {
            collection = new ArrayList();
            entries.put(listId, collection);
        }
        
        return collection;
    }

    @Override
    public ImageEntry addImageToGallery(String galleryId, String imageId, String description, String title) throws ErrorException {
        ImageEntry entry = createImageEntry(imageId, description, title);
        addToCollection(galleryId, entry);
        
        return entry;
    }

    @Override
    public void saveEntry(ImageEntry entry) throws ErrorException {
        ImageEntry old_entry = findEntry(entry.id);
        if(old_entry == null) {
            throw new ErrorException(1000007);
        }
        
        old_entry.description = entry.description;
        old_entry.title = entry.title;
        
        databaseSaver.saveObject(old_entry, credentials);
    }

    @Override
    public void deleteImage(String entryId) throws ErrorException {
        List<ImageEntry> collection = findCollection(entryId);
        removeFromCollection(collection, entryId);
    }

    @Override
    public String createImageGallery() throws ErrorException {
        return UUID.randomUUID().toString();
    }

    private ImageEntry createImageEntry(String imageId, String description, String title) throws ErrorException {
        ImageEntry entry = new ImageEntry();
        entry.description = description;
        entry.imageId = imageId;
        entry.title = title;
        entry.storeId = storeId;
        databaseSaver.saveObject(entry, credentials);
        return entry;
    }

    private void addToCollection(String galleryId, ImageEntry entry) throws ErrorException {
        List<ImageEntry> collection = getCollection(galleryId);
        entry.appId = galleryId;
        collection.add(entry);
        databaseSaver.saveObject(entry, credentials);
    }

    private ImageEntry findEntry(String entryId) throws ErrorException {
        List<ImageEntry> collection = findCollection(entryId);
        if(collection == null) {
            throw new ErrorException(1000008);
        }
        
        for(ImageEntry entry : collection) {
            if(entry.id.equals(entryId)) {
                return entry;
            }
        }
        throw new ErrorException(1000007);
    }
    
    private List<ImageEntry> findCollection(String entryId) {
        for(String key : entries.keySet()) {
            for(ImageEntry entry : getCollection(key)) {
                if(entry.id.equals(entryId)) {
                    return getCollection(key);
                }
            }
        }
        
        return null;
    }

    private void removeFromCollection(List<ImageEntry> collection, String entryId) throws ErrorException {
        ImageEntry entry = findEntry(entryId);
        collection.remove(entry);
        databaseSaver.deleteObject(entry, credentials);
    }

    @Override
    public List<ImageEntry> getAllImages(String id) throws ErrorException {
        return getCollection(id);
    }

    @Override
    public ImageEntry getEntry(String id) throws ErrorException {
        return findEntry(id);
    }
}
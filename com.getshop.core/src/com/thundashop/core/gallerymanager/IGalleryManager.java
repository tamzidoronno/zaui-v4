package com.thundashop.core.gallerymanager;
import com.thundashop.app.gallerymanager.data.ImageEntry;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 * Some webshops like to show off their products using a gallery.<br>
 * By using a gallery you will be able to list your products in a nice gallery view.<br>
 * You can also you this gallery to display images to other things, like events created by your company etc.<br>
 */
@GetShopApi
public interface IGalleryManager {
    /**
     * Add a image to a given gallery.
     * @param galleryId The id for the gallery, if this does not exists, it creates a gallery related to this id.
     * @param imageId The image id generated by the filemanager.
     * @param description A description to the image.
     * @param title A title to the image.
     * @throws ErrorException 
     */
    @Administrator
    public ImageEntry addImageToGallery(String galleryId, String imageId, String description, String title) throws ErrorException;
    
    /**
     * Update an already existing image.
     * @param entry The entry to update.
     * @throws ErrorException 
     */
    @Administrator
    public void saveEntry(ImageEntry entry) throws ErrorException;
    
    /**
     * Remove an already existing image.
     * @param entryId The id of the image to remove.
     * @throws ErrorException 
     */
    @Administrator
    public void deleteImage(String entryId) throws ErrorException;
    
    /**
     * Create a new gallery.
     * @return The id for the new gallery.
     * @throws ErrorException 
     */
    @Administrator
    public String createImageGallery() throws ErrorException;
    
    
    /**
     * Fetch all images binded to a given gallery id.
     * @param id The id to fetch the entries from.
     * @return
     * @throws ErrorException 
     */
    public List<ImageEntry> getAllImages(String id) throws ErrorException;
    
    /**
     * Find an existing entry.
     * @param id The id for search for (found in the ImageEntry object)
     * @return
     * @throws ErrorException 
     */
    public ImageEntry getEntry(String id) throws ErrorException;
}

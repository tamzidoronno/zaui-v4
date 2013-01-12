
package com.thundashop.app.banner;

import com.thundashop.app.bannermanager.data.BannerSet;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 * A bannermanager help you save banners to your system. Save a set of images categorized by ids.<br>
 * <br>
 * Save the height and width and make an application that helps you slide images or display multiple images to your page.<br>
 */
@GetShopApi
public interface IBannerManager {
    /**
     * Update a given bannerset.<br>
     * If the id of the bannerset does not exists, it will automatically create one for you.<br>
     * 
     * @param set The bannerset to save.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public BannerSet saveSet(BannerSet set) throws ErrorException;
    
    /**
     * Delete a given banner set.
     * 
     * @param id The id for the bannerset to delete.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public BannerSet deleteSet(String id) throws ErrorException;
    
    /**
     * Add a new image to an existing bannerset.<br>
     * The fileid is just an identifier and should be generated / stored by the one calling this application<br>
     * 
     * @param id The id to save on.
     * @param fileId The given file id for this image, fetched from the filemanager when uploading the image.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public BannerSet addImage(String id, String fileId) throws ErrorException;
    
    /**
     * Remove an already existing image from a given bannerset.
     * @param bannerSetId The id for the bannerset.
     * @param fileId The file id to remove.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public BannerSet removeImage(String bannerSetId, String fileId) throws ErrorException;
    
    /**
     * Initiate / create a new bannerset.
     * @param width The width for the banners
     * @param height The height for the banners.
     * @param id Specify an id if you want to override the id generation, leave empty otherwhise.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public BannerSet createSet(int width, int height, String id) throws ErrorException;
    
    /**
     * Fetch an existing bannerset.
     * @param id The id for the bannerset to fetch, if the id does not exists, it will be created.
     * @return A bannerset.
     * @throws ErrorException 
     */
    public BannerSet getSet(String id) throws ErrorException;
    
    /**
     * Add a product to a given image.
     * @param bannerSetId The id of the bannerset which holds the images.
     * @param imageId The image id to link a product to.
     * @param productId The id of the product to be linked.
     * @return
     * @throws ErrorException 
     */
    public BannerSet linkProductToImage(String bannerSetId, String imageId, String productId) throws ErrorException;
}
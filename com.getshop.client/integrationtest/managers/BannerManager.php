<?php

class BannerManager extends TestBase {
    
    public function BannerManager($api) {
        $this->api = $api;
    }
    
    /**
     * Creating your first bannerset is easy.
     */
    public function test_createSet() {
        $api = $this->getApi();
        $bannermanager = $api->getBannerManager();
        $set = $bannermanager->createSet((int)800, (int)200, "");
    }
    
    /**
     * You are not really attaching a image to the banner set,
     * but you are adding the id for the image (or a reference to the image)
     * This is how you do it.
     */
    public function test_addImage() {
        //First you should write some code to upload an image.
        //Then you can add a reference to this.
        $uploadedImageId = "someid_or_path";
        
        $manager = $this->getApi()->getBannerManager();
        $set = $manager->createSet(700, 200, "");
        
        $manager->addImage($set->id, $uploadedImageId);
    }
    
    /**
     * Need to delete a bannerset? Is it not in use anynmore?
     * Sure, just delete it.
     */
    public function test_deleteSet() {
        $manager = $this->getApi()->getBannerManager();
        
        //First just create one you can delete.
        $set = $manager->createSet(1, 1, "");
        
        //Delete this.
        $manager->deleteSet($set->id);
    }
    
    /**
     * Get an existing banner set?
     */
    public function test_getSet() {
        $manager = $this->getApi()->getBannerManager();
        
        //First just create one you can delete.
        $set = $manager->createSet(1, 1, "");
        
        //Now just fetch it again.
        $refetched_set = $manager->getSet($set->id);
    }
    
    /**
     * Of course it would be interesting to link a product to one of this images.
     * This can easily be done.
     */
    public function test_linkProductToImage() {
        $manager = $this->getApi()->getBannerManager();
        
        //The image should be uploaded and stored somewhere else.
        //Then add the reference for this image.
        $fileId = "path_to_image";
        
        //The product id is provided by the productmanager.
        $productId = "someproductid";
        
        //First just create one you can link to.
        $set = $manager->createSet(1, 1, "");
        $manager->addImage($set->id, $fileId);
        $manager->linkProductToImage($set->id, $fileId, $productId);
    }
    
    /**
     * Removing a image?
     */
    public function test_removeImage() {
        $manager = $this->getApi()->getBannerManager();
        
        //The image should be uploaded and stored somewhere else.
        //Then add the reference for this image.
        $fileId = "path_to_image";
        
        //The product id is provided by the productmanager.
        $productId = "someproductid";
        
        //First just create one you can remove.
        $set = $manager->createSet(1, 1, "");
        $manager->addImage($set->id, $fileId);
        
        //Now remove it.
        $manager->removeImage($set->id, $fileId);
    }
    
    /**
     * Sometimes you need to change some of the attributes of the banner set.
     * For example size, this can be done like this.
     */
    public function test_saveSet() {
        $manager = $this->getApi()->getBannerManager();

        //First create a set to update.
        $set = $manager->createSet(200, 100, "");
        
        //Now modify it.
        $set->width = 600;
        $set->height = 200;
        
        //update it
        $manager->saveSet($set);
        
    }
}

?>

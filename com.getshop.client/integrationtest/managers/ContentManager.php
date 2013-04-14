<?php

class ContentManager extends TestBase {
    public function ContentManager($api) {
        $this->api = $api;
    }
    
    /**
     * Want to store some content (text content, might be everything).
     */
    public function test_createContent() {
        $manager = $this->getApi()->getContentManager();
        
        //Save some content.
        $id = $manager->createContent("Something we need to save");
    }
    
    /**
     * Example: This is how you delete content from the manager.
     */
    public function test_deleteContent() {
        $manager = $this->getApi()->getContentManager();
        
        //Save some content.
        $id = $manager->createContent("Something we need to save");
        
        //Now just delete it.
        $manager->deleteContent($id);
    }
    
    /**
     * You just want to fetch all the content for this store?<br>
     * Just do it like this.
     */
    public function test_getAllContent() {
        $manager = $this->getApi()->getContentManager();
        
        //Save some content.
        $manager->createContent("Something we need to save 1");
        $manager->createContent("Something we need to save 2");
        
        $allContent = $manager->getAllContent();
        foreach($allContent as $id => $content) {
            //Do something with each content.
        }
    }
    
    
    /**
     * Get the content for a given content id.
     */
    public function test_getContent() {
        $manager = $this->getApi()->getContentManager();
        
        //Save some content.
        $id = $manager->createContent("Something we need to save 1");
        
        //Just fetch it.
        $content = $manager->getContent($id);
    }
    
    /**
     * Want to replace the content which has already been created?
     */
    public function test_saveContent() {
        $manager = $this->getApi()->getContentManager();
        
        //Save some content.
        $id = $manager->createContent("Something we need to save 1");
        
        //Now just replace it.
        $manager->saveContent($id, "New content");
    }
}

?>

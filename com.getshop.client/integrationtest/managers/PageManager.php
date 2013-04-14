<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PageManager
 *
 * @author boggi
 */
class PageManager extends TestBase {

    public function PageManager($api) {
        $this->api = $api;
    }

    /**
     * Update the configuration for an added application.
     */
    public function test_saveApplicationConfiguration() {
        $manager = $this->getApi()->getPageManager();

        //First create an application you want to add.";
        $config = $manager->addApplication("LeftMenu");

        //Now make it inheritated.
        $config->inheritate = 1;
        $manager->saveApplicationConfiguration($config);
    }
    
    /**
     * Adding a singleton application
     */
    public function test_addApplication() {
        $manager = $this->getApi()->getPageManager();
        $manager->addApplication("GoogleAnalytics");
    }
    
    /**
     * This is how you add an application to a given page.
     */
    public function test_addApplicationToPage() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        
        $manager->addApplicationToPage($page->id, "LeftMenu", "middle");
    }
   
    /**
     * Change the layout for a given page.
     */
    public function test_changePageLayout() {
        $manager = $this->getApi()->getPageManager();
        
        //First create the page.
        $page = $manager->createPage(1, "");
        
        //Now change the layout.
        $manager->changePageLayout($page->id, 4);
    }
    
    /**
     * Change the userlevel for a given page.
     */
    public function test_changePageUserLevel() {
        $manager = $this->getApi()->getPageManager();
        
        //First create the page.
        $page = $manager->createPage(1, "");
        
        //Now change the page userlevel.
        $manager->changePageUserLevel($page->id, 100);
    }
    
    /**
     * Simply create a page.
     */
    public function test_createPage() {
        $manager = $this->getApi()->getPageManager();
        $manager->createPage(2, "");
    }
    
    /**
     * Fetch all settings for an application.
     */
    public function test_getApplicationSettings() {
        $manager = $this->getApi()->getPageManager();
        //The PHP name for the application.
        $settings = $manager->getApplicationSettings("GoogleAnalytics");
    }
    
    /**
     * Fetch all applications.
     */
    public function test_getApplications() {
        $manager = $this->getApi()->getPageManager();
        $allApplications = $manager->getApplications();
    }
    
    /**
     * Get a given page.
     */
    public function test_getPage() {
        $manager = $this->getApi()->getPageManager();
        
        //First create the page to fetch.
        $page = $manager->createPage(1, "");
        
        //Fetch the page.
        $refetched_page = $manager->getPage($page->id);
    }
    
    /**
     * Need to remove delete an application?
     */
    public function test_removeApplication() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        $app = $manager->addApplicationToPage($page->id, "LeftMenu", "middle");
        
        //remove the application.
        $manager->removeApplication($app->id, $page->id);
    }
    
    /**
     * Reorder the application inside a given page area?
     */
    public function test_reorderApplication() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        $app = $manager->addApplicationToPage($page->id, "LeftMenu", "middle");
        
        $manager->reorderApplication($page->id, $app->id, true);
    }
    
    /**
     * Stick an application to app pages?
     */
    public function test_setApplicationSettings() {
        $manager = $this->getApi()->getPageManager();
        
        //Add a singleton application
        $app = $manager->addApplication("GoogleAnalytics");
        
        $setting = $this->getApiObject()->core_common_Setting();
        $setting->id = "google_ad_key";
        $setting->value = "the_google_ad_key";
        
        $settings = $this->getApiObject()->core_common_Settings();
        $settings->settings[] = $setting;
        $settings->appId = $app->id;
        
        $manager->setApplicationSettings($settings);
    }
    
    /**
     * Stick an application.
     */
    public function test_setApplicationSticky() {
        $manager = $this->getApi()->getPageManager();
        
        //Create an application instance.
        $page = $manager->createPage(1, "");
        $app = $manager->addApplicationToPage($page->id, "LeftMenu", "middle");
        $manager->setApplicationSticky($app->id, 1);
    }

    /**
     * Delete application. This api should not be confused with removeApplicationFromPage.
     * By using this function you actually delete the app from the store application pool,
     * meaning that you can not get it back from the pool on a later stage. 
     */
    public function test_deleteApplication() {
        $manager = $this->getApi()->getPageManager();
        
        // Need to add a application first before we can delete it.
        $app = $manager->addApplication("Bring");
        
        // Deleting the bring application
        $manager->deleteApplication($app->id);
    }
    
    /**
     * Sometimes it is nessesary to link pages to make sure what applications in the areas above this page is supposed to be printed.
     */
    public function test_setParentPage() {
        $manager = $this->getApi()->getPageManager();
        
        // Need to create a couple of pages.
        $firstPage = $manager->createPage(1, "");
        $parentPage  = $manager->createPage(1, "");
        
        //And now link them
        $manager->setParentPage($firstPage->id, $parentPage->id);
    }
    
    /**
     * If you wish to add existing application that allready has been added to the store
     * you can use this function.
     */
    public function test_addExistingApplicationToPageArea() {
        $manager = $this->getApi()->getPageManager();
        $appConfiguration = $manager->addApplicationToPage("home", "LeftMenu", "left");
        $manager->addExistingApplicationToPageArea("checkout", $appConfiguration->id, "left");
    }
    
    /**
     * Sometimes it is convient to add pages based on a id that you
     * already know.
     */
    public function test_createPageWithId() {
        $manager = $this->getApi()->getPageManager();
        $manager->createPageWithId(1, "home", "home_sub_page");
        $page = $manager->getPage("home_sub_page");
        assert(is_object($page));
    }
    
    /**
     * Deleting of pages, pretty self explained.
     */
    public function test_deletePage() {
        $manager = $this->getApi()->getPageManager();
        $manager->deletePage("home_sub_page");
    }
    
    
    /**
     * Want to figure out where a given application is being used?
     */
    public function test_getPagesForApplications() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        
        $app = $manager->addApplicationToPage($page->id, "LeftMenu", "middle");
       
        $appIds = array();
        $appIds[] = $app->id;
        
        $result = $manager->getPagesForApplications($appIds);
        
        foreach($result as $appId => $pages) {
            foreach($pages as $pageId) {
                
            }
        }
    }
    
    /**
     * You can translate a page id, usually a human readable text is found in a
     * list manager, or a product manager. Use the translatePages call to figure out
     * a good translation for the page id.
     */
    public function test_translatePages() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = "My name";
        $entry->pageId = $page->id;
        
        $this->getApi()->getListManager()->addEntry("some_list", $entry, "");
        
        $ids = array();
        $ids[] = $page->id;
        
        $translated = $manager->translatePages($ids);
        
        //Just for testing, this if statement should not be true!
        if($translated->{$page->id} != $entry->name) {
            echo "Failed to find page name";
            exit(0);
        }
    }
}

?>

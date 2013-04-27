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
        $config = $manager->addApplication("00d8f5ce-ed17-4098-8925-5697f6159f66");

        //Now make it inheritated.
        $config->inheritate = 1;
        $manager->saveApplicationConfiguration($config);
    }
    
    /**
     * Do you need to remove all applications added to a given page?
     */
    public function test_clearPageArea() {
        $manager = $this->getApi()->getPageManager();
        $page = $manager->createPage(1, "");

        $manager->clearPageArea($page->id, "left");
        $manager->clearPageArea($page->id, "middle");
        $manager->clearPageArea($page->id, "right");
    }
    
    
    /**
     * Fetch all instances of a given application.
     * The applicationsettings id is the id identifying the application. 
     */
    public function test_getApplicationsBasedOnApplicationSettingsId() {
        $manager = $this->getApi()->getPageManager();
        
        //The app settings id is the id generated when you create an application
        //Remember the instanceid is the id of the instance using this applications.
        //Have a look at appmanager for more information about this.
        $appSettingsId = "someappsettingsid";
        $instances = $manager->getApplicationsBasedOnApplicationSettingsId($appSettingsId);
    }
    
    
    /**
     * Do you need all instances created by a given application for a given area?
     */
    public function test_getApplicationsByPageAreaAndSettingsId() {
        $manager = $this->getApi()->getPageManager();
        
        //The app settings id is the id generated when you create an application
        //Remember the instanceid is the id of the instance using this applications.
        //Have a look at appmanager for more information about this.
        $appSettingsId = "someappsettingsid";
        $pageArea = "left";
        $instances = $manager->getApplicationsByPageAreaAndSettingsId($appSettingsId, $pageArea);
    }
    
    
    /**
     * Do you need all instances for a given page?
     */
    public function test_getApplicationsForPage() {
        $manager = $this->getApi()->getPageManager();
        
        //This will get all applications for the homepage.
        $pageid = "home";
        $instances = $manager->getApplicationsForPage($pageid);
    }
    
    
    /**
     * Somethimes it is useful to remove all instances for a given application.
     */
    public function test_removeAllApplications() {
        $manager = $this->getApi()->getPageManager();
        $appsettingsid = "someappsettingsid";
        $manager->removeAllApplications($appsettingsid);
    }
    
    /**
     * When cloning an application... you need to change all the current instances into the clone.
     * This is done so that you can modify the already added instances with the new cloned one.
     * 
     * This is how it is done.
     */
    public function test_swapApplication() {
        $manager = $this->getApi()->getPageManager();
        //In this case, we just swap it to itself.
        $appsettingsid = "0cf21aa0-5a46-41c0-b5a6-fd52fb90216f";
        $clonedid = "0cf21aa0-5a46-41c0-b5a6-fd52fb90216f";
        $manager->swapApplication($appsettingsid, $clonedid);
    }
    
    
    /**
     * Adding a singleton application
     */
    public function test_addApplication() {
        $this->addApp("0cf21aa0-5a46-41c0-b5a6-fd52fb90216f");
    }
    
    /**
     * This is how you add an application to a given page.
     */
    public function test_addApplicationToPage() {
        $manager = $this->getApi()->getPageManager();
        
        //Create a page to attach the app to.
        $page = $manager->createPage(1, "");
        
        $app = $this->addAppToPage($page->id);
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
        $app = $this->addAppToPage($page->id);
        
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
        $app = $this->addAppToPage($page->id);
        
        $manager->reorderApplication($page->id, $app->id, true);
    }
    
    /**
     * Stick an application to app pages?
     */
    public function test_setApplicationSettings() {
        $manager = $this->getApi()->getPageManager();
        
        //Add a singleton application
        $app = $this->addApp("0cf21aa0-5a46-41c0-b5a6-fd52fb90216f");
        
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
        $app = $this->addAppToPage($page->id);
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
        $app = $manager->addApplication("2da52bbc-a392-4125-92b6-eec1dc4879e9");
        
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
        $appConfiguration = $this->addAppToPage("home");
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
        $app = $this->addAppToPage($page->id);
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
    
    public function addApp($appSettingsId) {
        $manager = $this->getApi()->getPageManager();
        return $manager->addApplication($appSettingsId);
    }

    public function addAppToPage($pageid) {
        $manager = $this->getApi()->getPageManager();
        return $manager->addApplicationToPage($pageid, "00d8f5ce-ed17-4098-8925-5697f6159f66", "middle");
    }
}

?>

<?php

class AppManager extends TestBase {
    
    public function AppManager($api) {
        $this->api = $api;
    }
    
    /**
     * Start by creating yourself an application.
     */
    public function test_createApplication() {
        $api = $this->getApi();
        $appManager = $api->getAppManager();
        $app = $appManager->createApplication("My new application");
    }
    
    /**
     * Do you want to check if your synch client has been connected to the backend?
     */
    public function test_isSyncToolConnected() {
        $api = $this->getApi();
        if($api->getAppManager()->isSyncToolConnected()) {
            //It is connected
        } else {
            //It is not connected
        }
    }
    
    /**
     * Fetch all the application which has been marked for synchronization.
     */
    public function test_getSyncApplications() {
        $api = $this->getApi();
        $apps = $api->getAppManager()->getSyncApplications();
        if(is_array($apps)) {
            foreach($apps as $app) {
                /* @var $app core_appmanager_data_ApplicationSynchronization */
            }
        }
    }
    
    /**
     * Mark an application for synchronization.
     */
    public function test_setSyncApplication() {
        $api = $this->getApi();
        /* $allApps core_appmanager_data_AvailableApplications */
        $allApps = $api->getAppManager()->getAllApplications();
        
        $appid = $allApps->applications[0]->id;
        $api->getAppManager()->setSyncApplication($appid);
    }
    
    /**
     * Update the application with the nessesary attributes.
     */
    public function test_saveApplication() {
        $api = $this->getApi();
        $appManager = $api->getAppManager();
//        $myapp = $appManager->createApplication("My new application 2");
//        $myapp->price = 10.0;
//        $appManager->saveApplication($myapp);
    }
    
    /**
     * Fetch all applications attached to you
     */
    public function test_getAllApplications() {
        $api = $this->getApi();
        $appManager = $api->getAppManager();
        $allApps = $appManager->getAllApplications();
        foreach($allApps->applications as $app) {
            /* @var $app core_applicationmanager_ApplicationSettings */
        }
    }
    
    /**
     * Well, i just want to delete my application....
     */
    public function test_deleteApplication() {
        $api = $this->getApi();
        $appManager = $api->getAppManager();
        $myapp = $appManager->createApplication("An application to delete");
        $appManager->deleteApplication($myapp->id);
    }
    
    /**
     * I just want the settings for one specific application.
     */
    public function test_getApplication() {
        $api = $this->getApi();
        $appManager = $api->getAppManager();
        $myapp = $appManager->createApplication("My application with settings");
        
        $settings = $appManager->getApplication($myapp->id);
        
        // $settings === $myapp
    }
}

?>

<?php

class ReportingManager extends TestBase {

    public $api;

    public function __construct($api) {
        $this->api = $api;
    }

    /**
     * This report function allows you to fetch a report for a current time period.
     */
    public function test_getReport() {
        $manager = $this->getApi()->getReportingManager();
        $reports = $manager->getReport("2012-04-02", "2012-12-12", 3);
        foreach ($reports as $report) {
            /* @var $report core_reportingmanager_data_Report */
        }
    }

    /**
     * Fetch all page views for a given time periode.
     */
    public function test_getPageViews() {
        $manager = $this->getApi()->getReportingManager();
        $pageViews = $manager->getPageViews("2012-04-02", "2012-12-12");
        if (is_array($pageViews)) {
            foreach ($pageViews as $pageView) {
                /* @var $pageView core_reportingmanager_data_PageView */
            }
        }
    }

    /**
     * Fetch all users logged on at a given time periode
     */
    public function test_getUserLoggedOn() {
        $manager = $this->getApi()->getReportingManager();
        $usersLoggedOn = $manager->getUserLoggedOn("2012-04-02", "2012-12-12");
        if (is_array($usersLoggedOn)) {
            foreach ($usersLoggedOn as $loggedOnUser) {
                /* @var $loggedOnUser core_reportingmanager_data_LoggedOnUser */
            }
        }
    }

    /**
     * Fetch all products viewed at a given time periode.
     */
    public function test_getProductViewed() {
        $manager = $this->getApi()->getReportingManager();
        $productsViewed = $manager->getProductViewed("2012-04-02", "2012-12-12");
        if (is_array($productsViewed)) {
            foreach ($productsViewed as $productViewed) {
                /* @var $productViewed core_reportingmanager_data_ProductViewed */
            }
        }
    }
    
    /**
     * Find all events connected to a specific session.
     */
    public function test_getAllEventsFromSession() {
        $manager = $this->getApi()->getReportingManager();
        $result = $manager->getAllEventsFromSession("2012-04-02", "2012-12-12", "somesessiond");
        if(is_array($result)) {
            foreach($result as $res) {
                /* @var $res core_reportingmanager_data_EventLog */
            }
        }
    }
    
    /**
     * Find all users connected at a given time period.
     */
    public function test_getConnectedUsers() {
        $manager = $this->getApi()->getReportingManager();
        
        $filter = $this->getApiObject()->core_reportingmanager_data_ReportFilter();
        $pages = array();
        $pages[] = "Home";
        $filter->includeOnlyPages = $pages;
        
        $result = $manager->getConnectedUsers("2012-04-02", "2012-12-12", $filter);
        if(is_array($result)) {
            foreach($result as $res) {
                /* @var $res core_reportingmanager_data_UserConnected */
            }
        }
    }
    
    /**
     * Fetch all orders created at a given time period.
     */
    public function test_getOrdersCreated() {
        $manager = $this->getApi()->getReportingManager();
        $result = $manager->getOrdersCreated("2012-04-02", "2012-12-12");
        if(is_array($result)) {
            foreach($result as $res) {
                /* @var $res core_reportingmanager_data_OrderCreated */
            }
        }
        
    }
}

?>

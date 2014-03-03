<?php

namespace ns_1d8e7f34_14ee_4018_a3f4_190500dce026;

class GetShopStarter extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return "GetShopStarter";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "GetShopStarter";
    }

    public function createStore() {
        $webPageData = new \core_getshop_data_WebPageData();
        $webPageData->fullName = $_POST['data']['name'];
        $webPageData->emailAddress = $_POST['data']['email'];
        $webPageData->password = $_POST['data']['password'];
        
        $created = $this->getApi()->getGetShop()->createWebPage($webPageData);
        
        echo  "http://".$created->store->webAddress . "/index.php?logonwithkey=".$created->user->key."&showdesigns=true&prepopulate=true";

          
//Adding home menu entry
//        $this->getApi()->getPageManager()->getPage("home");
//        $applications = $this->getApi()->getPageManager()->getApplications();
//        foreach ($applications as $app) {
//            if ($app->appName == "TopMenu") {
//                $topMenuId = $app->id;
//                break;
//            }
//        }
//        $entry = $this->getApiObject()->core_listmanager_data_Entry();
//        $entry->name = "Home";
//        $entry->pageId = "home";
//
//        $this->getApi()->getListManager()->addEntry($topMenuId, $entry, "home");
//        
//        //Generating logon link
//        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
//        $user->key = rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999);
//        $this->getApi()->getUserManager()->saveUser($user);
//        

    }

    public function render() {
        $this->includefile("GetShopStarter");
    }

}

?>

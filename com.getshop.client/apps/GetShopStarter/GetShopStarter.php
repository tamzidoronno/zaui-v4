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
        
        $store = $this->getApi()->getGetShop()->createWebPage($webPageData);
        
        echo  "http://".$store->webAddress;
    }

    public function render() {
        $this->includefile("GetShopStarter");
    }

}

?>

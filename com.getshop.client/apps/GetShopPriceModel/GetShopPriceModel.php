<?php
namespace ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e;

class GetShopPriceModel extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopPriceModel";
    }

    public function render() {
        $this->includefile("calculator");
    }
    
    public function log() {
        $data = json_encode($_POST['data']);
        $this->getApi()->getTrackerManager()->logTracking("GetShopPriceModel", "getshop_price_model", "changed", $data);
        $vars = implode("&", $_POST['data']);
        $link = "/scripts/pricecalculator.php?vars=var";
        foreach ($_POST['data'] as $key => $var) {
            $link .= "&".$key."=".$var;
        } 
        
        echo base64_encode($link);
        die();
    }
    
    public function sendOffer() {
        $this->getApi()->getUtilManager()->sendPriceOffer($_POST['data']['link'], $_POST['data']['email']);
    }
}
?>
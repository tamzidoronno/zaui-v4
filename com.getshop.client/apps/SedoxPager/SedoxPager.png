<?php
namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxDownloadHistory extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxDownloadHistory";
    }

    public function render() {
        $this->includefile("downloadhistory");
    }

    public function getCurrentOrders() {
        $account = $this->getApi()->getSedoxProductManager()->getSedoxUserAccount();
        $orders = $account->orders;
        $orders = array_reverse($orders);
        return array_splice($orders, 0, 20);
    }
    
    public static function createProductName($product) {
        return $product->brand." ".$product->model." ".$product->engineSize." ".$product->power." ".$product->year;
    }
}
?>

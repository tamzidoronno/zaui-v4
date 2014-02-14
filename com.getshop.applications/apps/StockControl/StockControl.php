<?php
namespace ns_a93d64e4_b7fa_4d55_a804_ea664b037e72;

class StockControl extends \MarketingApplication implements \Application {
    public $singleton = true;

    public function getYoutubeId() {
        return "BkGILSPFSGI";
    }
    
    public function getDescription() {
        return $this->__f("This application gives you the ability to control your stocks, simply change how many you have in stock on each product.");
    }
    
    public function getName() {
        return $this->__("Stock Control");
    }
    
    public function postProcess() {
    }
    
    public function preProcess() {
    }
    
    public function render() {
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
}

?>

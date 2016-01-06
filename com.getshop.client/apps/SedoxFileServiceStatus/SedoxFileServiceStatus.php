<?php
namespace ns_b9642995_2bb2_474c_b1e8_5c859dad67a1;

class SedoxFileServiceStatus extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileServiceStatus";
    }

    public function render() {
        $this->includefile("fileservicestatus");
    }

    public function getTime() {
        return date('H:i');
    }

}
?>

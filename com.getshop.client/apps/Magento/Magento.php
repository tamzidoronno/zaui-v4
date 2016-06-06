<?php
namespace ns_815955a0_559a_4164_8994_cecd4497efb9;

class Magento extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Magento";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
}
?>

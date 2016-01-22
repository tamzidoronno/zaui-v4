<?php
namespace ns_54b33da0_11d2_48cb_afb2_2da4b2bdb7be;

class XLedger extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__w("Xledger is a economy system.");
    }

    public function getName() {
        return "XLedger";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("xledgerconfig");
    }
}
?>

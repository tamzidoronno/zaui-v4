<?php
namespace ns_932810f4_5bd1_4b56_a259_d5bd2e071be1;

class AccountingTransfer extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__w("Export to accounting systems.");
    }

    public function getName() {
        return "AccountingTransfer";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("accountingconfig");
    }
}
?>

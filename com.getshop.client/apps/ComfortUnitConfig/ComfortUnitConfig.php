<?php
namespace ns_f462df2e_2a2e_4e3b_9bfc_13ff2cf03f4f;

class ComfortUnitConfig extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortUnitConfig";
    }

    public function render() {
        $this->includefile("unitconfig");
    }
}
?>

<?php
namespace ns_7db21d0e_6636_4dd3_a767_48b06932416c;

class PmsRestrictions extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsRestrictions";
    }

    public function render() {
        $this->includefile("createrestriction");
    }
}
?>

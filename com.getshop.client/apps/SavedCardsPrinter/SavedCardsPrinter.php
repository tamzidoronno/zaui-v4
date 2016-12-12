<?php
namespace ns_92f398b2_7be4_4aad_a790_2aa189108e3c;

class SavedCardsPrinter extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SavedCardsPrinter";
    }

    public function render() {
        $this->includefile("cards");
    }
}
?>

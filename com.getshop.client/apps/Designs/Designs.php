<?php
namespace ns_636149b5_f3c9_4b63_99e1_83eeb5742e05;

class Designs extends \SystemApplication implements \Application {

    public function getDescription() {
        return "Our designs";
    }

    public function getName() {
        return "Designs";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function render() {
        $this->includefile("designs");
    }

}

?>

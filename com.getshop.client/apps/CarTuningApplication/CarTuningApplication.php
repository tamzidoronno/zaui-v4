<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of CarTuningApplication
 *
 * @author ktonder
 */
namespace ns_3bfa7e0d_3280_4c85_8f72_7516cd145446;

class CarTuningApplication extends \WebshopApplication implements \Application { 
    public function getDescription() {
        return $this->__f("Simple cartuning application.");
    }

    public function getName() {
        return $this->__f("Car tuning");
    }

    public function getTopModels() {
        $top = $this->getApi()->getCarTuningManager()->getCarTuningData(null);
        return $top;
    }
    
    public function render() {
        $this->includefile("tuningdata");
    }    
}

?>

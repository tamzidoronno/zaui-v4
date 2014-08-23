<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxTheme
 *
 * @author ktonder
 */
namespace ns_4e2598a8_47d9_4400_ba72_a18b2b45bcb5;

class SedoxTheme extends \ThemeApplication implements \Application {

    function __construct() {
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 80);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
    }

    public function render() {

    }    
    
    public function getDescription() {
        return $this->__f("The description");
    }
    
    public function getName() {
        return $this->__("Sedox Databank Theme");
    }
    
}

?>
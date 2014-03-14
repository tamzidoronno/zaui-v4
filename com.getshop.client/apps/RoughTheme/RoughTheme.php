<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_bcd06c3e_283b_4862_aafc_4d4b8209c9b8;

class RoughTheme extends \ThemeApplication implements \Application {
    
    function __construct() {
            // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 85);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
        
    }

    public function getDescription() {
        return $this->__w("Nice modern theme. Suitable for different carmarkeds.");
    }

    public function render() {
        
    }
    
    public function getName() {
        return $this->__f("Snickles");
    }
   
    
}
?>
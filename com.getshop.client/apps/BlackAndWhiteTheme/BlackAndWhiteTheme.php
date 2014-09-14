<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_9aaa0023_c512_4be0_a3a4_e42ba84c7a4e;

class BlackAndWhiteTheme extends \ThemeApplication implements \Application {
    
    function __construct() {
            // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 100);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
        
    }

    public function getDescription() {
        return $this->__w("Timeless black and white design.");
    }

    public function render() {
        
    }
    
    public function getName() {
        return $this->__f("BlackAndWhiteTheme");
    }
   
    
}
?>
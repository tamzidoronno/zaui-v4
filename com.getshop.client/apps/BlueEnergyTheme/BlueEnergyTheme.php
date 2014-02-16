<?php

/*
 */

/**
 * Description of WhiteAndBlueTheme
 *
 * @author ktonder  
 */
namespace ns_9ffcc6a0_130e_11e3_8ffd_0800200c9a66;

class BlueEnergyTheme extends \ThemeApplication {
    public function getDescription() {
        return $this->__f("this is restricted to owner only");
    }
    
    function __construct() {
        
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 100);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
        
    }
}

?>

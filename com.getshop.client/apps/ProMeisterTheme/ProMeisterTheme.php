<?php

/*
 */

/**
 * Description of WhiteAndBlueTheme
 *
 * @author ktonder  
 */
namespace ns_0747d81a_9688_4b71_8d12_04a099654e3d;

class ProMeisterTheme extends \ThemeApplication {
    public function getDescription() {
        return $this->__f("this is restricted to owner only");
    }
    
    function __construct() {
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 100);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);        
    }

    public function renderFonts() {
        echo '<script src="//use.typekit.net/tdn7qln.js"></script>';
    }

}

?>

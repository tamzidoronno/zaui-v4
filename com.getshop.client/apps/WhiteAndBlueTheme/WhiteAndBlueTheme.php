<?php

/*
 */

/**
 * Description of WhiteAndBlueTheme
 *
 * @author ktonder  
 */
namespace ns_a84cbbb0_8f21_11e2_9e96_0800200c9a66;

class WhiteAndBlueTheme extends \ThemeApplication {
    public function getDescription() {
        return $this->__f("this is restricted to owner only");
    }
    
    function __construct() {
        
    }
    
    public function addScripts() {
        echo "\n" . "<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>";
    }
}

?>

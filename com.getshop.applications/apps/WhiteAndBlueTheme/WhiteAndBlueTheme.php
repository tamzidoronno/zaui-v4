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
        $this->setTotalWidth(900);
        $this->setWidthLeft(190);
        $this->setWidthMidle(490);
        $this->setWidthRight(200);
        $this->setHeaderHeight(135);
        $this->setFooterHeight(30);
        
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 100);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
        
        $this->setCategoryImageWidth(140);
        $this->setCategoryImageHeight(140);
        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);
    }
}

?>

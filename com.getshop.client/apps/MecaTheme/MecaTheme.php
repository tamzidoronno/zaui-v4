<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of MecaTheme
 *
 * @author ktonder
 */
namespace ns_b1e89810_2dc3_11e3_aa6e_0800200c9a66;
class MecaTheme extends \ThemeApplication {
    public function getDescription() {
        return $this->__f("this is restricted to owner only");
    }
    
    function __construct() {
        $this->setTotalWidth(1100);
        $this->setWidthLeft(210);
        $this->setWidthMidle(660);
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

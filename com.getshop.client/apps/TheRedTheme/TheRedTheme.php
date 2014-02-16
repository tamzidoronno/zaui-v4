<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of TheRedTheme
 *
 * @author ktonder
 */
namespace ns_d147f6a0_8f31_11e2_9e96_0800200c9a66;

class TheRedTheme extends \ThemeApplication {
    public function getDescription() {
        return $this->__w("A red theme that can be used when selling exciting things");
    }
    
    function __construct() {
        $this->setTotalWidth(900);
        $this->setWidthLeft(190);
        $this->setWidthMidle(490);
        $this->setWidthRight(200);
        $this->setHeaderHeight(221);
        $this->setFooterHeight(202);

        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 168);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 900);
        
        $this->setCategoryImageHeight(140);
        $this->setCategoryImageWidth(140);
        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);
    }

}

?>

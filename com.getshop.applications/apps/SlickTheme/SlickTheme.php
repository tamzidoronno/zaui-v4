<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_efcbb450_8f26_11e2_9e96_0800200c9a66;

/**
 * Description of SlickTheme
 *
 * @author ktonder
 */
class SlickTheme extends \ThemeApplication {

    /** @var \GetShopApi */
    private $api;

    public function getDescription() {
        
    }

    function __construct() {
        $this->api = $this->getApi();

        $this->setTotalWidth(1018);
        $this->setWidthLeft(208);
        $this->setWidthRight(208);
        $this->setWidthMidle(567);
        $this->setHeaderHeight(202);
        $this->setFooterHeight(202);
        
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 127);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 992);

        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'height', 120);
        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'width', 120);

        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);
    }

}
?>

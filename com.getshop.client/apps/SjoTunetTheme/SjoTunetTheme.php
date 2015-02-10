<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_06321eda_afaa_4e91_8ca7_67d342dbd1ea;

/**
 * Description of SlickTheme
 *
 * @author ktonder
 */
class SjoTunetTheme extends \ThemeApplication {

    /** @var \GetShopApi */
    private $api;

    public function getDescription() {
        
    }
    public function addScripts() {
        ?>
        <script src="//use.typekit.net/yda1aff.js"></script>
        <script>try{Typekit.load();}catch(e){}</script>
        <?
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
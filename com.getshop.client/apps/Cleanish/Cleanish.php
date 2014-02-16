<?php

namespace ns_0cec32f4_d7eb_45bf_a58e_6ca3b79ee72d;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class Cleanish extends \ThemeApplication {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    /** @var \GetShopApi */
    private $api;

    public function getDescription() {
    }

    function __construct() {
        $this->api = $this->getApi();

        $this->setTotalWidth(998);
        $this->setWidthLeft(208);
        $this->setWidthRight(208);
        $this->setWidthMidle(567);
        $this->setHeaderHeight(100);
        $this->setFooterHeight(202);

        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 45);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 200);

        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'height', 120);
        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'width', 120);

        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);
    }

}
?>

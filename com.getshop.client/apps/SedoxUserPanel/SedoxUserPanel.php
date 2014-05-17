<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxUserPanel
 *
 * @author ktonder
 */

namespace ns_32b5f680_dd8d_11e3_8b68_0800200c9a66;

class SedoxUserPanel extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "An application for Sedox Performance that can be used for displaying userspecific information to the user";
    }

    public function getName() {
        return "Sedox User Panel";
    }

    public function render() {
        $this->includefile("userpanel");
    }    
}

?>

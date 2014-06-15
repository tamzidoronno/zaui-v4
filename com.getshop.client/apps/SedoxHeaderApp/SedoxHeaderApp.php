<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxHeaderApp
 *
 * @author ktonder
 */

namespace ns_264b0edf_b654_4ce0_9be2_0ebb3d2887af;

class SedoxHeaderApp extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "Sedox Header App";
    }

    public function getName() {
        return "Sedox Header App";
    }

    public function render() {
        if (!$this->getUser()) {
            $this->includefile("notloggedin");
        } else {
            $this->includefile("header");
        }
    }
}

?>

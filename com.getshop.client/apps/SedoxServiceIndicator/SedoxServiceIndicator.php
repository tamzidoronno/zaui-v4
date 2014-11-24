<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxServiceIndicator
 *
 * @author ktonder
 */

namespace ns_4cc9499c_6928_4811_9c9b_dba648258c52;

class SedoxServiceIndicator extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "";        
    }

    public function getName() {
        return "Sedox Service Indicator";
    }

    public function render() {
        $this->includefile("servicestatus");
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }

    public function isOpen() {
        
        $isClosed = $this->getConfigurationSetting("isclosed");
        if ($isClosed=="true") {
            return false;
        }
        
        $open = $this->getConfigurationSetting("open");
        $close = $this->getConfigurationSetting("close");
        
        $show_start = strtotime($open);
        $show_end = strtotime($close);
        $currentTime = time();
        
        if ($currentTime > $show_start && $currentTime < $show_end) {
            return true;
        }
        
        return false;
    }
}

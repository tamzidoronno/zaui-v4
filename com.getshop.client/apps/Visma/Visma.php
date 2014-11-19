<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Visma
 *
 * @author ktonder
 */

namespace ns_4cc9499c_6928_4811_9c9b_dba648258c52;

class Visma extends \ReportingApplication implements \Application {
    
    public function getDescription() {
        return "Visma integration";
    }

    public function getName() {
        return "Visma";
    }

    public function render() {
        echo "Rendered";
    }
    
    public function renderConfig() {
        $this->includefile("vismaconfig");
    }

}
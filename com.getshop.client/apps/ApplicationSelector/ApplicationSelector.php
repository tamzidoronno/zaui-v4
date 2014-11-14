<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_f1fc4af3_656e_4294_a268_40d2a82d0aa1;

class ApplicationSelector extends \SystemApplication implements \Application {
    
    public function getDescription() {
        return $this->__f("A application for selecting applications");
    }

    public function getName() {
        return "ApplicationSelector";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("applicationlist");
    }

    public function activate_application() {
        $this->getApi()->getStoreApplicationPool()->activateApplication($_POST['value']);
    }
    
    public function deactivate_application() {
        $this->getApi()->getStoreApplicationPool()->deactivateApplication($_POST['value']);
    }

    public function isModuleInArray($key, $modules) {
        if (!is_array($modules)) {
            return false;
        }
        
        foreach ($modules as $module) {
            if ($module->id == $key) {
                return true;
            }
        }
        
        return false;
    }

    public function activateModule() {
        $this->getApi()->getStoreApplicationPool()->activateModule($_POST['value']);
    }

    public function isActivated($app, $notActivated) {
        foreach ($notActivated as $notactive) {
            if ($notactive->id == $app->id) {
                return false;
            }
        }
        
        return true;
    }

}

?>
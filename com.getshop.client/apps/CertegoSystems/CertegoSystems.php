<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of CertegoSystems
 *
 * @author ktonder
 */

namespace ns_27a320a3_e983_4f55_aae8_cf94add661c2;

class CertegoSystems extends \ApplicationBase implements \Application {
    function __construct() {
    }

    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("systemsoverview");
    }

    public function createSystem() {
        $system = new \core_certego_data_CertegoSystem();
        $system->name = $_POST['name'];
        $system->phoneNumber = $_POST['phonenumber'];
        $system->email = $_POST['email'];
        $system->number = $_POST['systemnname'];
        
        if (isset($_POST['systemid']) && $_POST['systemid'] != "") {
            $system->id = $_POST['systemid'];
        }
        
        $system = $this->getApi()->getCertegoManager()->saveSystem($system);
        $_POST['value'] = $system->id;
    }

    public function deleteSystem() {
        $this->getApi()->getCertegoManager()->deleteSystem($_POST['systemid']);
    }
    
    public function getSystem($systemId) {
        $systems = $this->getApi()->getCertegoManager()->getSystems();
        foreach ($systems as $system) {
            if ($system->id == $systemId) {
                return $system;
            }
        }
        
        return false;
    }

    public function searchForGroup() {
        
    }
    
    public function removeGroupFromSystem() {
        $system = $this->getSystem($_POST['value']);
        $system->groupId = null;
        $this->getApi()->getCertegoManager()->saveSystem($system);
    }
    
    public function assignSystemToGroup() {
        $system = $this->getSystem($_POST['value']);
        $system->groupId = $_POST['value2'];
        $this->getApi()->getCertegoManager()->saveSystem($system);
    }
}

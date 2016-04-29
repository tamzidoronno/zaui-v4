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

class CertegoSystems extends \ns_27716a58_0749_4601_a1bc_051a43a16d14\GSTableCommon implements \Application {

    private $systemsForGroup;

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
    
    public function addSystemToGroup() {
        $system = $this->getSystem($_POST['value2']);
        $system->groupId = $_POST['value'];
        $this->getApi()->getCertegoManager()->saveSystem($system);
    }

    public function getGroupName($system) {
        $group = $this->getApi()->getUserManager()->getGroup($system->groupId);
        if ($group) {
            return $group->groupName;
        }
        
        return false;
    }
    
    /**
     * This is used for rendering extra information in grouplist. 
     * Userful for adding more information in the list.
     */
    public function renderExtraGroupList($group) {
        $count = $this->getSystemCount($group->id);
        echo "<div style='font-size: 13px; font-style: italic;'>".$this->__f("Systems").": ".$this->getSystemCount($group->id)."</div>";
    }

    public function getSystemCount($groupId) {
        $systems = $this->getApi()->getCertegoManager()->getSystems();
        $count = 0;
        foreach($systems as $system) {
            if ($system->groupId == $groupId) {
                $count++;
            }
        }
        
        return $count;
    }

    public function renderGroupInformation($group) {
        $this->systemsForGroup = $this->getApi()->getCertegoManager()->getSystemsForGroup($group);
        $this->includefile('grouplist');
    }
    
    public function getId() {
        return $this->getApplicationSettings()->id;
    }
    
    public function searchForSystems() {
        
    }
    
    public function printSystem($system) {
        echo $system->number ." - ". $system->name." - ".$system->email." - ".$system->phoneNumber; 
    }
    
    function getSystemsForGroup() {
        return $this->systemsForGroup;
    }
    
    public function removeSystemFromGroup() {
        $system = $this->getSystem($_POST['value2']);
        $system->groupId = "";
        $this->getApi()->getCertegoManager()->saveSystem($system);
    }
    
    public function loadData() {
        if (!$this->filteredData)
            $this->filteredData = $this->getApi()->getCertegoManager()->getSystemsFiltered ($this->createFilter());
    }
}

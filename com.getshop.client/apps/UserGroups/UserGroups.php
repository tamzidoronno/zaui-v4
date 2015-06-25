<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of UserGroups
 *
 * @author ktonder
 */
namespace ns_3983a370_d0cc_46de_ba94_cc22fe7becbb;

class UserGroups extends \ApplicationBase implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }

    public function createGroup() {
        $group = new \core_usermanager_data_Group();
        $group->groupName = $_POST['name'];
        $this->getApi()->getUserManager()->saveGroup($group);
    }

    public function deleteGroup() {
        $this->getApi()->getUserManager()->removeGroup($_POST['value']);
    }
    
    public function search() {
        // Save search ?
    }
    
    public function addUserToGroup() {
        $this->getApi()->getUserManager()->addGroupToUser($_POST['value2'], $_POST['value']);
    }
    
    public function removeUserFromGroup() {
        $this->getApi()->getUserManager()->removeGroupFromUser($_POST['value2'], $_POST['value']);
    }
}

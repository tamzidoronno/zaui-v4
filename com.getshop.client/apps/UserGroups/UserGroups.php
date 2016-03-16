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
        $group = $this->getApi()->getUserManager()->saveGroup($group);
        $_POST['value'] = $group->id;
    }

    
    public function deleteGroup() {
        $this->getApi()->getUserManager()->removeGroup($_POST['value']);
    }
    
    public function saveGroup() {
        $group = $this->getApi()->getUserManager()->getGroup($_POST['value']);
        $group->groupName = $_POST['name'];
        $group->defaultDeliveryAddress->fullName = $_POST['deliveryName'];
        $group->defaultDeliveryAddress->address = $_POST['deliveryAddress1'];
        $group->defaultDeliveryAddress->address2 = $_POST['deliveryAddress2'];
        $group->defaultDeliveryAddress->postCode = $_POST['deliveryPostNumber'];
        $group->defaultDeliveryAddress->city = $_POST['deliveryPostPlace'];
        $group->defaultDeliveryAddress->phone = $_POST['deliveryCellphone'];
        $group->defaultDeliveryAddress->emailAddress = $_POST['deliveryEmail'];
        
        $group->invoiceAddress->fullName = $_POST['invoiceName'];
        $group->invoiceAddress->address = $_POST['invoiceAddress1'];
        $group->invoiceAddress->address2 = $_POST['invoiceAddress2'];
        $group->invoiceAddress->postCode = $_POST['invoicePostNumber'];
        $group->invoiceAddress->city = $_POST['invoicePostPlace'];
        $group->invoiceAddress->customerNumber = $_POST['invoiceCustomerNumber'];
        $group->invoiceAddress->vatNumber = $_POST['invoiceVatNumber'];
        $group->invoiceAddress->reference = $_POST['invoiceReference'];
        $group->invoiceAddress->phone = $_POST['invoiceCellPhone'];
        
        $group->isPublic = $_POST['isPublic'];
        $group->imageId = $_POST['imageId'];
        $group->usersRequireGroupReferencePlaceholder = $_POST['usersRequireGroupReferencePlaceholder'];
        $group->usersRequireGroupReferenceValidationMin = $_POST['usersRequireGroupReferenceValidationMin'];
        $group->usersRequireGroupReferenceValidationMax = $_POST['usersRequireGroupReferenceValidationMax'];
        $group->usersRequireGroupReference = $_POST['usersRequireGroupReference'];
        
        $this->getApi()->getUserManager()->saveGroup($group);
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
    
    public function addSystem() {
        $system = new \core_usermanager_data_CertegoSystem();
        $system->name = $_POST['addSystem'];
        $this->getApi()->getUserManager()->addGroupInformation($_POST['value'], $system);
    }

    public function getNumberOfUsersInGroup($group) {
        return count($this->getApi()->getUserManager()->getUsersBasedOnGroupId($group->id));
    }

    public function printExtraInformation($group) {
        $singleTons = $this->getFactory()->getApi()->getStoreApplicationPool()->getApplications();
        
        foreach ($singleTons as $singleTon) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($singleTon);
            if (method_exists($instance, "renderExtraGroupList")) {
                $instance->renderExtraGroupList($group);
            }
        }
    }
    
    public function renderExtraGroupList($group) {
        echo "<div style='font-size: 13px; font-style: italic;'>".$this->__f("Users").": ".$this->getNumberOfUsersInGroup($group)."</div>";
    }

    public function printExtraApps($group) {
        $singleTons = $this->getFactory()->getApi()->getStoreApplicationPool()->getApplications();
        
        foreach ($singleTons as $singleTon) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($singleTon);
            if (method_exists($instance, "renderGroupInformation")) {
                $instance->renderGroupInformation($group);
            }
        }
    }

}

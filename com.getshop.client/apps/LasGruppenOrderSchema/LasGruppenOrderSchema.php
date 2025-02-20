<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of LasGruppenOrderSchema
 *
 * @author ktonder
 */

namespace ns_7004f275_a10f_4857_8255_843c2c7fb3ab;

class LasGruppenOrderSchema extends \ApplicationBase implements \Application {
//    public static $url = "http://20192.3.0.local.getshop.com";
    public static $url = "https://20192.getshop.com";
    
    public function getDescription() {
        return "TEST";
    }

    public function getName() {
        return $this->__f("LasGruppen Ordering Schema");
    }

    public function render() {
        $this->includefile("orderingschema");
    }
    
    public function sendConfirmation() {
        if (isset($_POST['data']['confirmationEmail']) && $_POST['data']['confirmationEmail']) {
            $attachments = $this->getAttachments();
            $this->sendMail($_POST['data']['confirmationEmail'], $attachments);
        }
    }
    
    public function getBrReg() {
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['number']);
        echo json_encode($company);
        die();
    }
    
    public function downloadPdf() {
        $_POST['data']['userLoggedIn'] = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null;
        if ($_POST['data']['userLoggedIn']) {
            $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
            $group = $this->getGroup($user);
            $_POST['data']['groupName'] = $group->groupName;
            $_POST['data']['fullName'] = $user->fullName;
            $_POST['data']['groupReference'] = $group->invoiceAddress->customerNumber;
        }
        
        $this->saveOrder();
        
        if ($_POST['data']['saveInvoiceAddress'] == "true" && $_POST['data']['page1']['invoice']['companyName']) {
            $_POST['data']['name'] = $_POST['data']['page1']['invoice']['companyName'];
            $_POST['data']['addr1'] = $_POST['data']['page1']['invoice']['address'];
            $_POST['data']['addr2'] = $_POST['data']['page1']['invoice']['address2'];
            $_POST['data']['postcode'] = $_POST['data']['page1']['invoice']['postnumer'];
            $_POST['data']['city'] = $_POST['data']['page1']['invoice']['city'];
            $_POST['data']['vatNumber'] = $_POST['data']['page1']['invoice']['vatnumber'];
            $_POST['data']['phone'] = $_POST['data']['page1']['invoice']['cellphone'];
            $_POST['data']['email'] = $_POST['data']['page1']['invoice']['email'];
            $_POST['data']['addrid'] = "";
            $this->saveInvoiceAddr(true);
        }
        
        if ($_POST['data']['saveDeliveryAddress'] == "true" && $_POST['data']["page3"]["deliveryInfo"]["name"]) {
            $_POST['data']['name'] = $_POST['data']["page3"]["deliveryInfo"]["name"];
            $_POST['data']['addr1'] = $_POST['data']["page3"]["deliveryInfo"]["address"];
            $_POST['data']['addr2'] = $_POST['data']["page3"]["deliveryInfo"]["address2"];
            $_POST['data']['postcode'] = $_POST['data']["page3"]["deliveryInfo"]["postnumber"];
            $_POST['data']['city'] = $_POST['data']["page3"]["deliveryInfo"]["city"];
            $_POST['data']['phone'] = $_POST['data']["page3"]["deliveryInfo"]["cellphone"];
            $_POST['data']['emailaddress'] = $_POST['data']["page3"]["deliveryInfo"]["emailaddress"];
            $_POST['data']['addrid'] = "";
            $this->saveDeliveryAddr(true);
        }
        
        
        if ($_POST['data']['page4']['securitytype'] != "signature" || \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
            $_POST['data']['hidePinCode'] = false;
            $_SESSION['lasgruppen_pdf_data'] = json_encode($_POST);
            $attachments = $this->getAttachments();
            $this->sendMail($this->getEmailAddress(), $attachments, \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null);
        }
        
        if ($_POST['data']['page4']['securitytype'] == "signature") {
            $_POST['data']['hidePinCode'] = false;
            $_SESSION['lasgruppen_pdf_data'] = json_encode($_POST);
        }
        
        if (isset($_POST['data']['page4']['emailCopy']) && $_POST['data']['page4']['emailCopy']) {
            $_POST['data']['hidePinCode'] = true;
            $_SESSION['lasgruppen_pdf_data'] = json_encode($_POST);
            $attachments = $this->getAttachments();
            $this->sendMail($_POST['data']['page4']['emailCopy'], $attachments);
        }
    }
    
    public function isCompany() {
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['vatnumber']);
        if ($company) {
            echo "true";
        } else {
            echo "false";
        }
        die();
    }
    
    private function getAttachments() {
        $address = LasGruppenOrderSchema::$url."/scripts/generatePdfLasgruppen.php?id=" . session_id();
        $sessionId = session_id();
        session_write_close();
        $content = $this->getApi()->getUtilManager()->getBase64EncodedPDFWebPage($address);
        
        session_id($sessionId);
        session_start();
        session_id($sessionId);
        
        $attachments = [];
        $attachments['bestilling.pdf'] = $content;
        return $attachments;
    }
    
    private function sendMail($mailAddress, $attachments, $loggedIn = false) {
        $subject = $this->getFirstSystemNumber();
        
        if ($loggedIn) {
            $subject .= " (INNLOGGET) ";
        }
        
        if ($subject) {
            $subject .= " - ";
        }
        
        $subject .= "Bestilling fra Certego";
        
        $this->getApi()->getMessageManager()->sendMailWithAttachments($mailAddress, $mailAddress, $subject, "", "certego@getshop.com", "Certego AS", $attachments);
    }
    
    public function doLogin() {
        $user = $this->getApi()->getUserManager()->checkUserNameAndPassword($_POST['data']['username'], $_POST['data']['password']);
        if ($user) {
            $this->requestPincode();
        } else {
            echo "failed";
        }
    }
    
    public function requestPincode() {
        $success = $this->getApi()->getUserManager()->requestNewPincode($_POST['data']['username'], $_POST['data']['password']);
        if ($success) {
            echo "success";
        } else {
            echo "failed";
        }
    }
    
    public function loginWithPincode() {
        $userLoggedIn = $this->getApi()->getUserManager()->loginWithPincode($_POST['data']['username'], $_POST['data']['password'], $_POST['data']['pincode']);
        
        if ($userLoggedIn != null && isset($userLoggedIn)) {
            unset($_SESSION['tempaddress']);
            $_SESSION['loggedin'] = serialize($userLoggedIn);
            echo "success";
        } else {
            echo "failed";
        }
    }
    
    public function logout() {
        $this->getApi()->getUserManager()->logout();
        session_destroy();
    }

    public function getCurrentGroup() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        return $this->getGroup($user);
    }
    public function getGroup($user) {
        if ($user == null) {
            return null;
        }
        
        $groups = [];
        
        if ($user->groups) {
            foreach ($user->groups as $groupId) {
                $inGroup = $this->getApi()->getUserManager()->getGroup($groupId);
                if ($inGroup) {
                    $groups[] = $inGroup;
                }
            }
        }
        
        
        if (count($user->groups) === 1) {
            $group = $this->getApi()->getUserManager()->getGroup($user->groups[0]);
            return $group;
        }
        
        return null;
    }

    public function getSystems($group) {
        if ($group == null) {
            return [];
        }
        
        return $this->getApi()->getCertegoManager()->getSystemsForGroup($group);
    }

    public function getSelectedSystem() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $group = $this->getGroup($user);    
        $systems = $this->getSystems($group);
        
        if (count($systems) == 1) {
            return $systems[0];
        }
        
        return null;
    }


    public function getSelectedInvoiceAddress() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        if ($user) {
            return $this->getGroup($user)->invoiceAddress;
        }
        
        return null;
    }

    public function getDefaultDeliveryAddress() {
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        if ($user) {
            return $this->getGroup($user)->defaultDeliveryAddress;
        }
        
        return null;
    }

    public function deleteDeliveryAddr() {
        $group = $this->getCurrentGroup();
        $this->getApi()->getUserManager()->deleteExtraAddressToGroup($group->id, $_POST['data']['addrid']);
        $this->includefile("welcome");
    }
    
    public function saveDeliveryAddr($silent=false) {
        $address = new \core_usermanager_data_Address();
        $address->type = "shipment";
        $address->fullName = $_POST['data']['name'];
        $address->address = $_POST['data']['addr1'];
        $address->address2 = $_POST['data']['addr2'];
        $address->postCode = $_POST['data']['postcode'];
        $address->city = $_POST['data']['city'];
        $address->phone = $_POST['data']['phone'];
        $address->emailAddress = $_POST['data']['emailaddress'];
        $address->id = $_POST['data']['addrid'];
        
        $group = $this->getCurrentGroup();
        $this->getApi()->getUserManager()->saveExtraAddressToGroup($group, $address);
        if (!$silent) {
            $this->includefile("welcome");
        }
    }
    
    public function saveInvoiceAddr($silent=false) {
        $address = new \core_usermanager_data_Address();
        $address->type = "invoice";
        $address->fullName = $_POST['data']['name'];
        $address->address = $_POST['data']['addr1'];
        $address->address2 = $_POST['data']['addr2'];
        $address->postCode = $_POST['data']['postcode'];
        $address->city = $_POST['data']['city'];
        $address->vatNumber = $_POST['data']['vatNumber'];
        $address->phone = $_POST['data']['phone'];
        $address->emailAddress = $_POST['data']['email'];
        $address->id = $_POST['data']['addrid'];
        
        $group = $this->getCurrentGroup();
        $this->getApi()->getUserManager()->saveExtraAddressToGroup($group, $address);
        if (!$silent) {
            $this->includefile("welcome");
        }
    }
    
    public function showAddresses() {
        $this->includefile("address_select_box");
    }

    public function saveOrder() {
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
            $certegoOrder = new \core_certego_data_CertegoOrder();
            $certegoOrder->data = base64_encode(json_encode($_POST));
            $this->getApi()->getCertegoManager()->saveOrder($certegoOrder);
        }
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("page1_welcome_header", $_POST['data']['page1_welcome_header']);
        $this->setConfigurationSetting("page1_welcome_body", $_POST['data']['page1_welcome_body']);
        $this->setConfigurationSetting("page2_orderinformation", $_POST['data']['page2_orderinformation']);
        $this->setConfigurationSetting("page3_shippinginformation_rekommandert", $_POST['data']['page3_shippinginformation_rekommandert']);
        $this->setConfigurationSetting("page3_shippinginformation_express", $_POST['data']['page3_shippinginformation_express']);
        $this->setConfigurationSetting("page3_shippinginformation_bedriftspakke", $_POST['data']['page3_shippinginformation_bedriftspakke']);
        $this->setConfigurationSetting("page3_shippinginformation_service", $_POST['data']['page3_shippinginformation_service']);
    }

    public function getEmailAddress() {
        $userObject = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        /** @var $app ns_48a459b8_90b2_4dea_9bae_f548d006f526\CertegoSystemDepartments */
        $appJava = $this->getApi()->getStoreApplicationPool()->getApplication("48a459b8-90b2-4dea-9bae-f548d006f526");
        $app = $this->getFactory()->getApplicationPool()->createInstace($appJava);
        
        $email = $app->getDefaultEmailAddress(); 
        
        if ($userObject && $userObject->groups[0]) {
            $group = $this->getApi()->getUserManager()->getGroup($userObject->groups[0]);
            if ($group) {
                $app->currentGroup = $group;
                $selectedGroup = $app->getCurrentSelectedGroup(); 
                
                if ($selectedGroup) {
                    $email = $app->getEmailAddress($selectedGroup);
                }
            }
        }
        
        return $email;
    }

    public function getFirstSystemNumber() {
        $fromKey = @$_POST['data']['page2']['keys_setup'][0]['systemNumber'];
        
        if ($fromKey) {
            return $fromKey;
        }
        
        $fromCyl = @$_POST['data']['page2']['cylinder_setup'][0]['systemNumber'];
        
        if ($fromCyl) {
            return $fromCyl;
        }
        
        return "";
    }

}
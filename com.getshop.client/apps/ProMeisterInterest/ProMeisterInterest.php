<?php
namespace ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d;

class ProMeisterInterest extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    private $cachedUsers = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterInterest";
    }
    
    public function cancelStep2() {
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/eventType']);
    }

    public function render() {
        if (isset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction'])) {
            $this->includefile($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction']);
        } else {
            if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
                $this->includefile("adminmenu");
            }
            if (isset($_POST['event']) && $_POST['event'] == "setLocations") {
                $this->wrapContentManager("success_intreset_message", "Thank you, we have registered your interest now");

            } else if (isset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/eventType'])) {
                $this->includefile("selectLocation");
            } else {
                $this->includefile("promeisterinterest");
            }    
        }
    }
    
    public function selectEventType() {
        $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/eventType'] = $_POST['data']['typeid'];
    }
    
    public function sendInterestMessage() {
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getBookingEngineName(), $_POST['data']['typeid']);
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        $company = $user->companyObject;
        
        $subject = "Intresseanmälen";
        $content = "<br/>Event: " . $type->name;
        $content .= "<br/>";
        if ($company) {
            $content .= "<br/><b>Company</b>";
            $content .= "<br/>Company name: ".@$company->name;
            $content .= "<br/>VatNumber: ".@$company->vatNumber;
            $content .= "<br/>Address: ".@$company->address->address;
            $content .= "<br/>Post code: ".@$company->address->postCode;
            $content .= "<br/>City: ".@$company->address->city;
        }
        
        $content .= "<br/>";
        $content .= "<br/>Deltagarens namn: ".$user->fullName;
        $content .= "<br/>Kundnummer: ".@$company->reference;
        
        $to = $this->getFactory()->getStoreConfiguration()->emailAdress;

        $this->getApi()->getMessageManager()->sendMail($to, "ProMeister", $subject, $content, "noreply@getshop.com", "GetShop");
    }
    
    public function setLocations() {
        $setIds = array();
        
        foreach ($_POST['data'] as $key => $value) {
            if (!strstr($key, "location_")) {
                continue;
            }
            
            if ($value !== "true")
                continue;
            
            $datas = explode("_", $key);
            $id = $datas[1];
            $setIds[] = $id;
        }
        
        $obj = new \core_eventbooking_EventIntrest();
        $obj->eventTypeId = $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/eventType'];
        $obj->locations = $setIds;
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/eventType']);
        $this->getApi()->getEventBookingManager()->registerEventIntrest($this->getBookingEngineName(), $obj);
    }
    
    public function showResult() {
        $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction'] = "showsummary";
    }

    /**
     * 
     * @param type $param0
     * @return \core_usermanager_data_User
     */
    public function getUserById($param0) {
        if (isset($this->cachedUsers[$param0])) {
            return $this->cachedUsers[$param0];
        }
        
        $this->cachedUsers[$param0] = $this->getApi()->getUserManager()->getUserById($param0);
        return $this->cachedUsers[$param0];
    }

    public function getGrouped($interests) {
        $groupedArray = array();
        foreach ($interests as $interest) {
            if (!isset($groupedArray[$interest->eventTypeId])) {
                $groupedArray[$interest->eventTypeId] = array();
            }

            foreach ($interest->locations as $locationId) {
                if (!isset($groupedArray[$interest->eventTypeId][$locationId])) {
                    $groupedArray[$interest->eventTypeId][$locationId] = array();
                }

                $groupedArray[$interest->eventTypeId][$locationId][] = $interest;
            }

        }
        
        return $groupedArray;
    }
    
    public function selectTypeToWorkWith() {
        $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/currentTypeId'] = $_POST['data']['typeid'];
        $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction'] = "workwithentry";
    }
    
    public function getCurrentTypeId() {
        return $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/currentTypeId'];
    }
    
    public function getSelectedUsers() {
        if (!isset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'])) {
            return array();
        }
        return $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'];
    }
    
    public function cancelShowHistory() {
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers']);
        unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/adminaction']);
    }
    
    public function toggleCheckBox() {
        $userid = $_POST['data']['userid'];
        $checked = $_POST['data']['checked'] == "true";
        
        if (!isset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'])) {
            $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'] = array();
        }
        
        if ($checked) {
            $_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'][$userid] = $_POST['data']['locationid'];
        } else {
            unset($_SESSION['ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d/selectedUsers'][$userid]);
        }
        
        $this->includefile("selectedUsers");
        die();
    }
}
?>

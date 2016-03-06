<?php
namespace ns_afec873d_d876_46c8_856c_2c599bada065;

class EditEventType extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function addBookingItemType() {
        
    }
    
    public function preProcess() {
        if (isset($_GET['bookingItemTypeId'])) {
            $_SESSION['editevent_bookingItemTypeId'] = $_GET['bookingItemTypeId'];
        } 
   }
    
    public function getName() {
        return "EditEventType";
    }
    
    public function saveEvent() {
        $type = $this->getEventType();
        $type->name = $_POST['data']['name'];
        
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getBookingEngineName(), $type);
        
        $metaData = $this->getApi()->getEventBookingManager()->getBookingTypeMetaData($this->getBookingEngineName(), $type->id);
        $certificates = $this->getApi()->getEventBookingManager()->getCertificates($this->getBookingEngineName());
        
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        foreach ($groups as $group) {
            $groupId = $group->id;
            if (isset($_POST['data']["price_$groupId"])) {
                $metaData->groupPrices->{$groupId} = $_POST['data']["price_$groupId"];
            }
            
            $certs = [];
            foreach ($certificates as $certificate) {
                $certificateId = $certificate->id;
                if (isset($_POST['data']["certificate_".$groupId."_".$certificateId])) {
                    if ($_POST['data']["certificate_".$groupId."_".$certificateId] == "true") {
                        $certs[] = $certificateId;
                    }
                }
            }
            
            $metaData->certificateIds->{$groupId} = $certs;
            $metaData->publicPrice = $_POST['data']['publicPrice'];
            $metaData->publicVisible = $_POST['data']['public_visible'];
            $metaData->visibleForGroup->{$groupId} = $_POST['data']['visible_'.$groupId];
        }
        
        if (isset($_POST['data']['questBackId'])) {
            $metaData->questBackId = $_POST['data']['questBackId'];
        }
        
        $this->getApi()->getEventBookingManager()->saveBookingTypeMetaData($this->getBookingEngineName(), $metaData);
    }

    public function render() {
        $this->includeFile("edit_event_type");
    }
    
    public function getEventType() {
        return $this->getApi()->getBookingEngine()->getBookingItemType($this->getBookingEngineName(), $_SESSION['editevent_bookingItemTypeId']);
    }
    
}
?>

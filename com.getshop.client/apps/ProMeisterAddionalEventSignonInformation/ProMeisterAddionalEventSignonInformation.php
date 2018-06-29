<?php
namespace ns_a532618a_f189_4273_9145_c75b6a8289c5;

class ProMeisterAddionalEventSignonInformation extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterAddionalEventSignonInformation";
    }

    public function render() {
        if (isset($_POST['data']['doConfirmBooking'])) {
            $this->wrapApp("320ada5b-a53a-46d2-99b2-9b0b26a7105a", "bookingconfirmed"); 
        } else {
            $this->includefile("confirmbooking");
        }
    }
    
    public function confirmBooking() {
        $userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        
        if (isset($_POST['data']['personalid']) && strlen($_POST['data']['personalid']) != 11) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Personnr må være 11 siffer"; // The message you wish to display in the gserrorfield
            $obj->gsfield->personalid = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }
        
        if ($this->getModalVariable("userid")) {
            $userId = $this->getModalVariable("userid");
        }
        
        $this->getApi()->getUserManager()->addMetaData($userId, "event_signon_alergic", $_POST['data']['alergic']);
        $this->getApi()->getUserManager()->addMetaData($userId, "event_signon_specialfoodrequest", $_POST['data']['specialfoodrequest']);
        $this->getApi()->getUserManager()->addMetaData($userId, "event_signon_additionalinfo", $_POST['data']['additionalinfo']);
        $this->getApi()->getEventBookingManager()->addUserToEvent($this->getBookingEngineName(), $this->getModalVariable("eventid"), $userId, false, "web");
        
        if (isset($_POST['data']['personalid']) && $_POST['data']['personalid']) {
            $this->getApi()->getEventBookingManager()->addPersonalIdToEvent($this->getBookingEngineName(), $this->getModalVariable("eventid"), $userId, $_POST['data']['personalid']);
        }
    }
}
?>

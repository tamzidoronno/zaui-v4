<?php
namespace ns_bd751f7e_5062_4d0d_a212_b1fc6ead654f;

class EventUserList extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventUserList";
    }

    public function render() {
        $this->includeFile("userlist");
    }
    
    public function removeUserFromEvent() {
        $this->getApi()->getEventBookingManager()->removeUserFromEvent($this->getBookingEngineName(), $_POST['data']['eventid'], $_POST['data']['userid']);
    }
    
    public function showComments() {
        $this->includefile("showcomments");
    }
    
    public function addComment() {
        $this->getApi()->getEventBookingManager()->addUserComment($this->getBookingEngineName(), $_POST['data']['userId'], $_POST['data']['eventId'], $_POST['data']['comment']);
    }
    
    public function showSettings() {
        $this->includefile("usersettings");
    }
    
    public function setParticiationStatus() {
        $this->getApi()->getEventBookingManager()->setParticipationStatus($this->getBookingEngineName(), $_POST['data']['eventId'], $_POST['data']['userId'], $_POST['data']['status']);
    }

    /**
     * 
     * @param \core_usermanager_data_User $user
     */
    public function getUserAdditionalInformation($user) {
        $text1 = @$user->metaData->{"event_signon_alergic"};
        $text2 = @$user->metaData->{"event_signon_specialfoodrequest"};
        $text3 = @$user->metaData->{"event_signon_additionalinfo"};
        
        $retText = "";
        $retText .= $text1 ? "<br/> Alergic: ".nl2br($text1) : "";
        $retText .= $text2 ? "<br/> Food request: ".nl2br($text2) : "";
        $retText .= $text3 ? "<br/><br/> Comment: <br/>".nl2br($text3) : "";
        
        return $retText;
    }

}
?>

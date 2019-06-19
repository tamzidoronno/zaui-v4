<?php
namespace ns_66e0c307_5a6d_4c16_8ed6_5204d63d5675;

class ConferenceNew extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ConferenceNew";
    }

    public function render() {
        $this->includefile("new");
    }
    
    public function createConference() {
        $conference = new \core_pmsmanager_PmsConference();
        $conference->meetingTitle = $_POST['data']['name'];
        
        $saveConf = $this->getApi()->getPmsConferenceManager()->saveConference($conference);
        echo $saveConf->id;
    }
}
?>

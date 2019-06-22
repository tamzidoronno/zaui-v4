<?php
namespace ns_28befd67_e4ea_412b_a67a_23b1aa10781c;

class ConferenceView extends \MarketingApplication implements \Application {
    
    private $conference;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ConferenceView";
    }

    public function render() {
        $this->setData();
        $this->includefile("view");
    }

    public function setData() {
        if (isset($_GET['confid'])) {
            $this->conference = $this->getApi()->getPmsConferenceManager()->getConference($_GET['confid']);
            return;
        }
        
        if (isset($_POST['data']['confid'])) {
            $this->conference = $this->getApi()->getPmsConferenceManager()->getConference($_POST['data']['confid']);
            return;
        }
    }

    /**
     * 
     * @return \core_pmsmanager_PmsConference
     */
    public function getConference() {
        return $this->conference;
    }

    public function renderWorkArea() {
        if (isset( $_SESSION['ns_28befd67_e4ea_412b_a67a_23b1aa10781c_conferenceid_current_tab'])) {
            if ( $_SESSION['ns_28befd67_e4ea_412b_a67a_23b1aa10781c_conferenceid_current_tab'] == "events") {
                $this->includefile("events");
            }
            if ( $_SESSION['ns_28befd67_e4ea_412b_a67a_23b1aa10781c_conferenceid_current_tab'] == "customer") {
                $this->includefile("customer");
            }
            if ( $_SESSION['ns_28befd67_e4ea_412b_a67a_23b1aa10781c_conferenceid_current_tab'] == "pos") {
                $this->includefile("pos");
            }
        } else {
            $this->includefile("customer");
        }
        
    }
    
    public function deleteConference() {
        $this->getApi()->getPmsConferenceManager()->deleteConference($_POST['data']['confid']);
    }
    
    public function changeUser($user) {
         $this->setData();
         $this->conference->forUser = $user->id;
         $this->getApi()->getPmsConferenceManager()->saveConference($this->conference);
    }
    
    public function createCompany() {
        $this->setData();
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        
        $this->conference->forUser = $user->id;
        $this->getApi()->getPmsConferenceManager()->saveConference($this->conference);
        return $user;
    }
    
    public function showTab() {
        $_SESSION['ns_28befd67_e4ea_412b_a67a_23b1aa10781c_conferenceid_current_tab'] = $_POST['data']['tab'];
    }
    
    public function renderEvent() {
        $this->setData();
        $this->includefile("eventview");
    }
    
    public function saveEvent() {
        $this->setData();
        
        if ($_POST['data']['eventid']) {
            $event = $this->getApi()->getPmsConferenceManager()->getConferenceEvent($_POST['data']['eventid']);
        } else {
            $event = new \core_pmsmanager_PmsConferenceEvent();
        }
        
        $event->name = $_POST['data']['title'];
        $event->pmsConferenceId = $this->getConference()->id;
        $event->pmsConferenceItemId = $_POST['data']['pmsConferenceItemId'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['starttime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['endtime']));

        $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
        $this->setData();
        
        $this->includefile("events");
        
        $existingEntries = $this->getApi()->getPmsConferenceManager()->getEventEntries($_POST['data']['eventid']);
        
        foreach ($existingEntries as $existingEntry) {
            $found = false;
            foreach ($_POST['data']['activities'] as $activity) {
                if ($activity['activityid'] == $existingEntry->id) {
                    $found = true;
                }
            }
            
            if (!$found) {
                $this->getApi()->getPmsConferenceManager()->deleteEventEntry($existingEntry->id);
            }
        }
        
        foreach ($_POST['data']['activities'] as $activity) {
            $existing = $this->getExistingEntry($activity['activityid'], $existingEntries);
            $existing->pmsEventId = $event->id;
            $existing->conferenceId = $this->getConference()->id;
            $existing->count = $activity['count'];
            $existing->text = $activity['text'];
            $existing->extendedText = $activity['extendedText'];
            $existing->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$activity['from']));
            $existing->to = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$activity['to']));
            
            $this->getApi()->getPmsConferenceManager()->saveEventEntry($existing);
        }
        
        $this->setData();
        
    }

    /**
     * 
     * @param type $id
     * @param type $existingEntries
     * @return \core_pmsmanager_PmsConferenceEventEntry
     */
    public function getExistingEntry($id, $existingEntries) {
        foreach ($existingEntries as $entry) {
            if ($entry->id == $id) {
                return $entry;
            }
        }
        
        return new \core_pmsmanager_PmsConferenceEventEntry();
    }

    public function deleteEvent() {
        $this->getApi()->getPmsConferenceManager()->deleteConferenceEvent($_POST['data']['eventid']);
    }
    
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function setConferenceTitle() {
        $this->setData();
        $conference = $this->getConference();
        $conference->meetingTitle = $_POST['data']['meetingTitle'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
        $this->setData();
    }
}
?>

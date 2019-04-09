<?php
namespace ns_02b94bcd_39b9_41aa_b40c_348a27ca5d9d;

class PmsConference extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsConference";
    }
    
    public function deleteEventEntry() {
        $id = $_POST['data']['entryid'];
        $this->getApi()->getPmsConferenceManager()->deleteEventEntry($id);
        echo $id;
    }
    
    public function loadSubitems() {
        $_SESSION['pmsconferencecurrentitem'] = $_POST['data']['itemid'];
    }
    
    public function getCurrentSubitem() {
        if(isset($_SESSION['pmsconferencecurrentitem'])) {
            return $_SESSION['pmsconferencecurrentitem'];
        }
        return "";
    }
    
    public function loadEditItem() {
        $this->includefile("edititem");
    }

    public function openConference() {
        $this->includefile("conferenceoverview");
    }
    
    public function loadday() {
        $day = $_POST['data']['day'];
        $_SESSION['pmsconferencecurrentstart'] = date("d.m.Y", $day);
        $_SESSION['pmsconferencecurrentend'] = date("d.m.Y", $day+(60*60*24));
    }
    
    public function createEventEntry() {
        $event = $this->getApi()->getPmsConferenceManager()->getConferenceEvent($this->getCurrentEvent());
        $evententry = new \core_pmsmanager_PmsConferenceEventEntry();
        $this->saveEventEntry($evententry, $event);
        $this->includefile("evententries");
    }
    
    public function updateEventEntry() {
        $evententry = $this->getApi()->getPmsConferenceManager()->getEventEntry($_POST['data']['eventid']);
        $event = $this->getApi()->getPmsConferenceManager()->getConferenceEvent($evententry->pmsEventId);
        $this->saveEventEntry($evententry, $event);
        $this->includefile("evententries");
    }
    
    public function openEvent() {
        $this->setCurrentEvent($_POST['data']['eventid']);
        $this->includefile("eventoverview");
    }
    
    public function render() {
        echo "<div class='conferenceoverview'></div>";
        echo "<div class='eventoverview'></div>";
        echo "<div class='conferencesystem'>";
        $this->includefile("header");
        $this->includefile("overview");
        echo "</div>";
    }
    
    public function deleteItem() {
        $this->getApi()->getPmsConferenceManager()->deleteItem($_POST['data']['itemid']);
    }
    
    public function updateDateRange() {
        $_SESSION['pmsconferencecurrentstart'] = $_POST['data']['start'];
        $_SESSION['pmsconferencecurrentend'] = $_POST['data']['end'];
    }
    
    public function getCurrentStart() {
        if(isset($_SESSION['pmsconferencecurrentstart'])) {
            return $_SESSION['pmsconferencecurrentstart'];
        }
        return date("01.m.Y", time());
    }
    
    public function getCurrentEnd() {
        if(isset($_SESSION['pmsconferencecurrentend'])) {
            return $_SESSION['pmsconferencecurrentend'];
        }
        return date("t.m.Y", time());
    }
    
    public function changeUser($user) {
        
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        return $user;
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
        
        $conference = new \core_pmsmanager_PmsConference();
        $conference->forUser = $user->id;
        $conference->createdByUserId = $this->getApi()->getUserManager()->getLoggedOnUser()->id;
        $conference->meetingTitle = "Unkown meeting";
        $conference = $this->getApi()->getPmsConferenceManager()->saveConference($conference);
        $this->setCurrentConference($conference->id);
        echo $conference->id;
        die();
    }
    
    public function deleteCurrentConference() {
        $this->getApi()->getPmsConferenceManager()->deleteConference($this->getSelectedConference());
        $this->setCurrentConference("");
    }
    
    public function updateConferenceTitle() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($this->getSelectedConference());
        $conference->meetingTitle = $_POST['data']['value'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function deleteEvent() {
        $this->getApi()->getPmsConferenceManager()->deleteConferenceEvent($_POST['data']['eventid']);
        echo $_POST['data']['eventid'];
    }
    
    public function updateEventRow() {
        $event = $this->getApi()->getPmsConferenceManager()->getConferenceEvent($_POST['data']['eventid']);
        $event->pmsConferenceItemId = $_POST['data']['itemid'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date']." ".$_POST['data']['startime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['endtime']));
        $event->status = $_POST['data']['status'];
        $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
    }
    
    public function updateMemo() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($this->getSelectedConference());
        $conference->memo = $_POST['data']['memo'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function updateConferenceContact() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($this->getSelectedConference());
        $conference->contact = $_POST['data']['value'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function updateConferenceSource() {
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($this->getSelectedConference());
        $conference->source = $_POST['data']['value'];
        $this->getApi()->getPmsConferenceManager()->saveConference($conference);
    }
    
    public function addEvent() {
        $event = new \core_pmsmanager_PmsConferenceEvent();
        $event->pmsConferenceId = $this->getSelectedConference();
        $event->pmsConferenceItemId = $_POST['data']['itemid'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['starttime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['endtime']));
        $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
        $this->includefile("eventsaddedtoconference");
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user;
    }
    
    public function renameitem() {
        $id = $_POST['data']['itemid'];
        $item = $this->getApi()->getPmsConferenceManager()->getItem($id);
        $item->name = $_POST['data']['name'];
        $this->getApi()->getPmsConferenceManager()->saveItem($item);
        $this->includefile("edititem");
    }
    
    public function createItem() {
        $item = new \core_pmsmanager_PmsConferenceItem();
        $item->name = $_POST['data']['conferenceName'];
        $item->toItemId = $this->getCurrentSubitem();
        $this->getApi()->getPmsConferenceManager()->saveItem($item);
    }
    
    public function addSubItem() {
        $item = new \core_pmsmanager_PmsConferenceItem();
        $item->name = $_POST['data']['conferenceName'];
        $item->toItemId = $_POST['data']['itemid'];
        $this->getApi()->getPmsConferenceManager()->saveItem($item);
        $this->includefile("edititem");
    }

    public function getSelectedConference() {
        if(isset($_SESSION['pmscurrentconference'])) {
            return $_SESSION['pmscurrentconference'];
        }
        return "";
    }

    public function setCurrentConference($id) {
        $_SESSION['pmscurrentconference'] = $id;
    }

    public function getCurrentEvent() {
        if(isset($_SESSION['pmsconferencepmseventid'])) {
            return $_SESSION['pmsconferencepmseventid'];
        }
        return "";
    }
    
    public function setCurrentEvent($eventid) {
        $_SESSION['pmsconferencepmseventid'] = $eventid;
    }

    public function saveEventEntry($evententry, $event) {
        $evententry->text = $_POST['data']['text'];
        $evententry->from=null;
        $evententry->pmsEventId = $event->id;
        if($_POST['data']['starttime']) {
            $evententry->from = $this->convertToJavaDate(strtotime(date("d.m.Y", strtotime($event->from)) . " " . $_POST['data']['starttime']));
        }
        $evententry->to=null;
        if($_POST['data']['endtime']) {
            $evententry->to = $this->convertToJavaDate(strtotime(date("d.m.Y", strtotime($event->from)) . " " . $_POST['data']['endtime']));
        }
        $evententry->count = $_POST['data']['count'];
        $evententry->extendedText = $_POST['data']['extendedText'];
        $conference = $this->getApi()->getPmsConferenceManager()->saveEventEntry($evententry);
    }

    /**
     * @param \core_pmsmanager_PmsConferenceEvent $events
     */
    public function convertEventsByItem($events) {
        $result = array();
        foreach($events as $evnt) {
            if(!isset($result[$evnt->pmsConferenceItemId])) {
                $result[$evnt->pmsConferenceItemId] = array();
            }
            $evnt->fromLong = strtotime($evnt->from);
            $evnt->toLong = strtotime($evnt->to);
            $result[$evnt->pmsConferenceItemId][] = $evnt;
        }
        return $result;
    }

    public function hasEventOnTime($start, $end, $events, $itemIds) {
        $result = array();
        $result['id'] = "";
        $result['state'] = "";
        $result['check'] = "";
        
        foreach($itemIds as $itemId) {
            if(isset($events[$itemId])) {
                foreach($events[$itemId] as $check) {
                    $result['id'] = $check->pmsConferenceId;
                    /* @var $check \core_pmsmanager_PmsConferenceEvent */
                    $result['check'] = "start='$start' end='$end' fromLong='".$check->fromLong."' toLong='".$check->toLong."' starttime='".date("H:i", $start)."' endtime='".date("H:i", $end)."'";
                    if($check->fromLong < $start && $check->toLong > $end) {
                        $result['state'] = "middle";
                        return $result;
                    }
                    if($check->toLong > $start && $check->toLong <= $end && $check->fromLong >= $start && $check->fromLong < $end) {
                        $result['state'] = "both";
                        return $result;
                    }
                    if($check->toLong > $start && $check->toLong <= $end) {
                        $result['state'] = "end";
                        return $result;
                    }
                    if($check->fromLong >= $start && $check->fromLong < $end) {
                        $result['state'] = "start";
                        return $result;
                    }
                }
            }
        }
        return $result;
    }
}
?>

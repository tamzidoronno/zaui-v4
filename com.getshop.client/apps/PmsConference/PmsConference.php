<?php
namespace ns_02b94bcd_39b9_41aa_b40c_348a27ca5d9d;

class PmsConference extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsConference";
    }
    
    public function deleteEntry() {
        $id = $_POST['data']['id'];
        $this->getApi()->getPmsConferenceManager()->deleteEventEntry($id);
    }
    
    public function deleteEventEntry() {
        $id = $_POST['data']['entryid'];
        $this->getApi()->getPmsConferenceManager()->deleteEventEntry($id);
        echo $id;
    }
    
    public function removeGuestFromEvent() {
        $guestId = $_POST['data']['guestId'];
        $eventId = $_POST['data']['eventId'];
        $this->getApi()->getPmsConferenceManager()->removeGuestFromEvent($guestId, $eventId);
        $this->includefile("guestlist");
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
        $this->setCurrentConference($_POST['data']['conferenceid']);
        $this->includefile("conferenceoverview");
    }

    public function reconnectConferenceWithBooking() {
        $conferenceId = $_POST['data']['conferenceid'];
        if(!$conferenceId) return;

        $this->setCurrentConference($conferenceId);
        $conference = $this->getApi()->getPmsConferenceManager()->getConference($conferenceId);
        if(!$conference) return;

        //check if we already have a booking here...
        $existingBooking = $this->getApi()->getPmsManager()->getconferenceBooking($this->getSelectedMultilevelDomainName(), $conferenceId);


        if($existingBooking) return;


        //find bookings that have been created within 2s after the conference
        $bfilter = new \core_pmsmanager_PmsBookingFilter();
        $bfilter->filterType = 'registered';
        $bfilter->startDate = $this->convertToJavaDate( strtotime( $conference->rowCreatedDate) - 1000 );
        $bfilter->endDate = $this->convertToJavaDate( strtotime( $conference->rowCreatedDate) + 2000 );

        $conferenceBookings = (array)$this->getApi()->getPmsManager()->getAllBookings($this->getSelectedMultilevelDomainName(), $bfilter);
        if($conferenceBookings && count($conferenceBookings) > 0 )
        {
            for($i = 0; $i < count( $conferenceBookings ); $i++)
            {
                $booking = $conferenceBookings[ $i ];
                if( isset( $booking->rooms ) && isset( $booking->rooms[0] )  && isset( $booking->rooms[0]->bookingItemTypeId ) &&  $booking->rooms[0]->bookingItemTypeId == 'gspmsconference' )
                {
                    //we seem to have a conference booking here... we check its conferenceId
                    if( isset( $booking->conferenceId ) && $booking->conferenceId == '' )
                    {
                        //this one has no conference... we just hope its our booking and connect to it
                        $booking->conferenceId = $conferenceId;
                        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(),$booking);
                        //$this->includefile("conferencereports");
                        die('<script>document.location.reload();</script>');
                    }
                    else
                    {
                        echo 'not here....';
                    }
                }
            }
        }
        else
        {
            die('no bookings close to our conference creation found');
        }
    }


    public function loadBooking() {
        $pmsBookingGroupView = new \ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01\PmsBookingGroupRoomView();
        $pmsBookingGroupView->setRoomId($_POST['data']['id']);
        $pmsBookingGroupView->renderApplication(true, $this, true);
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
    
    public function loadConferences() {
        $this->includefile("conferencelist");
    }
    
    public function setconference() {
        $this->setCurrentConference($_POST['data']['conferenceid']);
    }
    
    public function changeToMonth() {
        $_POST['data']['start'] = date("01.m.Y", $_POST['data']['time']);
        $_POST['data']['end'] = date("t.m.Y", $_POST['data']['time']);
        $this->updateDateRange();
    }
    
    public function openEvent() {
        $this->setCurrentEvent($_POST['data']['eventid']);
        $this->includefile("eventoverview");
    }
    
    public function quickAddEventToConference() {
        $event = new \core_pmsmanager_PmsConferenceEvent();
        $event->pmsConferenceId = $this->getSelectedConference();
        $event->pmsConferenceItemId = $_POST['data']['itemid'];
        $event->from = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['starttime']));
        $event->to = $this->convertToJavaDate(strtotime($_POST['data']['date'] . " " . $_POST['data']['endtime']));
        $added = $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
        if(!$added) {
            echo "<div class='warningnotabletoadd'>The event you tried to add is not possible to add</div>";
        }
    }
    
    public function render() {
        $hasNewOrderSupport = $this->getApi()->getStoreManager()->supportsCreateOrderOnDemand();
        if(!$hasNewOrderSupport) {
            echo "The pms conference module is in a testing stage.<br>";
            echo "We are not sure at what point we will open this feature publically.<br>";
            return;
        }
        
        $pageId = $this->getPageIdModule();
        
        echo "<div class='conferenceoverview'></div>";
        echo "<div class='eventoverview'></div>";
        if($pageId == "home") {
            echo "<div class='conferencesystem'>";
            $this->includefile("header");
            $this->includefile("overview");
            echo "</div>";
        } else {
            $this->printReport();
        }
    }
    
    
    
    public function deleteItem() {
        $this->getApi()->getPmsConferenceManager()->deleteItem($_POST['data']['itemid']);
    }
    
    public function deleteItemsFilter() {
        unset($_SESSION['pmsconferencecurrentitems']);
    }
    
    public function updateDateRange() {
        $_SESSION['pmsconferencecurrentstart'] = $_POST['data']['start'];
        $_SESSION['pmsconferencecurrentend'] = $_POST['data']['end'];
        
        $itemsfilter = array();
        foreach($_POST['data'] as $idx => $val) {
            if($val == "true" && strpos($idx, "itemsfilter_") === 0) {
                $itemsfilter[] = str_replace("itemsfilter_", "", $idx);
            }
        }
        $_SESSION['pmsconferencecurrentitems'] = serialize($itemsfilter);
    }

    public function getItemsFilterArray() {
        if(isset($_SESSION['pmsconferencecurrentitems'])) {
            return unserialize($_SESSION['pmsconferencecurrentitems']);
        }
        return array();
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
        $saved = $this->getApi()->getPmsConferenceManager()->saveConferenceEvent($event);
        if(!$saved) {
            echo "<div class='warningnotabletoadd'>It is not possible to do this update!</div>";
        }
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
        $this->quickAddEventToConference();
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
        $result['title'] = "";
        
        foreach($itemIds as $itemId) {
            if(isset($events[$itemId])) {
                foreach($events[$itemId] as $check) {
                    $result['id'] = $check->pmsConferenceId;
                    $result['title'] = $check->title;
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
    
    public function loadPmsGuestList() {
        $this->includefile("pmsguestlist");
    }
    
    public function addGuestToEvent() {
        $guestId = $_POST['data']['guestid'];
        $eventId = $_POST['data']['eventid'];
        $this->getApi()->getPmsConferenceManager()->addGuestToEvent($guestId, $eventId);
        $this->includefile("guestlist");
    }

    public function printReport() {
        $id = $this->getPageIdModule();
        
        if(isset($_GET['conferenceid'])) { $this->includefile("conferencereport"); }
        if($id == "eventreport") { $this->includefile("eventreport"); }
        if($id == "report") { $this->includefile("allreport"); }
        if($id == "conferencereports") { $this->includefile("conferencereports"); }
    }

    public function getStatuses() {
        $status = array();
        $status[0] = "Unkown";
        $status[1] = "Confirmed";
        $status[2] = "Not confirmed";
        $status[3] = "Rejected";
        return $status;
    }

    public function getConference($conferenceId) {
        return $this->getApi()->getPmsConferenceManager()->getConference($conferenceId);
    }

}
?>

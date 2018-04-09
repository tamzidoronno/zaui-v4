<?php
namespace ns_74220775_43f4_41de_9d6e_64a189d17e35;

class PmsNewBooking extends \WebshopApplication implements \Application {
    public $canNotAddConferenceRoom = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNewBooking";
    }
    
    public function addConferenceRoom() {
        $start = strtotime($_POST['data']['startdate'] . " " . $_POST['data']['starttime']);
        $end = strtotime($_POST['data']['enddate'] . " " . $_POST['data']['endtime']);
        
        $start = $this->convertToJavaDate($start);
        $end = $this->convertToJavaDate($end);
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $_POST['data']['item']);
        
        $booking = new \core_bookingengine_data_Booking();
        $booking->bookingItemId = $item->id;
        $booking->bookingItemTypeId = $item->bookingItemTypeId;
        $booking->startDate = $start;
        $booking->endDate = $end;

        if(!$this->getApi()->getBookingEngine()->canAddBooking($this->getSelectedMultilevelDomainName(), $booking)) {
            $this->canNotAddConferenceRoom = true;
        } else {
            $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->date = new \core_pmsmanager_PmsBookingDateRange();
            $room->date->start = $start;
            $room->date->end = $end;
            $room->bookingItemId = $item->id;
            $room->bookingItemTypeId = $item->bookingItemTypeId;
            $currentBooking->rooms[] = $room;
            $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
        }
    }
    
    public function render() {
        if(isset($this->msg)) {
            echo $this->msg;
        }
        $conferncerooms = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), 1);
        $hasConferenceClass = "";
        if(sizeof($conferncerooms) > 0) {
            $hasConferenceClass = "hasconference";
        }
        
        
        
        echo "<div style='max-width:1500px; margin:auto;' class='dontExpand'>";
        echo "<div class='addnewroom $hasConferenceClass'>";
        $this->includefile("newbooking");
        echo "</div>";
        if($hasConferenceClass) {
            echo "<div class='conferenceroom'>";
            $this->includefile("conferencerooms");
            echo "</div>";
        }
        echo "<div class='availablerooms $hasConferenceClass'>";
        $this->showAvailableRooms();
        echo "</div>";
        echo "<div style='clear:both;'></div>";
        $this->includefile("roomsadded");
        echo "</div>";
    }
    
    public function showAvailableRooms() {
        $this->includefile("availablerooms");
    }
    
    public function reloadAvailability() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['start']);
        $app->setEndDate($_POST['data']['end']);
        $this->includefile("availablerooms");
    }
    
    
    public function completequickreservation() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $currentBooking->userId = $this->createSetUser();
        $currentBooking->quickReservation = true;
        $currentBooking->avoidCreateInvoice = true;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
        $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
        if(!$res) {
            $this->msg = "";
            $this->msg .= "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            $this->msg .= "<i class='fa fa-warning'></i> ";
            $this->msg .= "Unable to comply, your selection is not possible.";
            $this->msg .= "</div>";
        } else {
            $bookingEngineId = "";
            foreach($res->rooms as $room) {
                $bookingEngineId = $room->bookingId;
                if($bookingEngineId) {
                    break;
                }
            }
            $this->msg = ""; 
            $this->msg .= "<script>";
            $this->msg .= "thundashop.common.goToPageLink('/?page=groupbooking&bookingId=".$res->id."');";
            $this->msg .= "</script>";
        }
    }
    
    public function removeRoom() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $newRoomList = array();
        foreach($currentBooking->rooms as $room) {
            if($room->pmsBookingRoomId == $_POST['data']['id']) {
                continue;
            }
            $newRoomList[] = $room;
        }
        $currentBooking->rooms = $newRoomList;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function loadResult() {
        
    }
    
    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
    
    public function addRoom() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedMultilevelDomainName());
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['from']);
        $app->setEndDate($_POST['data']['to']);
        
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        
        foreach($types as $type) {
            $from = $_POST['data']['from'];
            $to = $_POST['data']['to'];
            $numberOfRooms = $_POST['data']['count_'.$type->id];

            $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
            $start = $this->convertToJavaDate(strtotime($from. " " . $config->defaultStart));
            $end = $this->convertToJavaDate(strtotime($to. " " . $config->defaultEnd));

            for($i = 0; $i < $numberOfRooms; $i++) {
                $room = new \core_pmsmanager_PmsBookingRooms();
                $room->date = new \core_pmsmanager_PmsBookingDateRange();
                $room->date->start = $start;
                $room->date->end = $end;
                $room->bookingItemTypeId = $type->id;
                $currentBooking->rooms[] = $room;
            }

            foreach($currentBooking->rooms as $room) {
                $room->addedToWaitingList = false;
            }

            $available = $this->getNumberOfAvailableForType($type, $currentBooking, $start, $end);
            $prefix = sizeof($currentBooking->rooms);
            for($i = $available; $i < 0; $i++) {
                $prefix--;
                $currentBooking->rooms[$prefix]->addedToWaitingList = true;
            }
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
    }

    public function removeAllSelectedRooms() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $booking->rooms = array();
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    public function getNumberOfAvailableForType($type,$current,$start,$end) {
        $size = $this->getApi()->getPmsManager()->getNumberOfAvailable($this->getSelectedMultilevelDomainName(), $type->id, $start, $end, false);

        foreach($current->rooms as $room) {
            if($room->bookingItemTypeId == $type->id) {
                $size--;
            }
        }
        return $size;
    }
    
    public function createNewCompanyCustomer() {
        $vatnumber = $_POST['data']['vatnumber'];
        $name = $_POST['data']['name'];
        
        $user = $this->getApi()->getUserManager()->createCompany($vatnumber, $name);
        if($user) {
            
            $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $currentBooking->userId = $user->id;
            $currentBooking->quickReservation = true;
            $currentBooking->avoidCreateInvoice = true;
            $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
            $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
            
            $this->msg = "";
            $this->msg .= "<script>";
            $this->msg .= "thundashop.common.goToPageLink('/?page=groupbooking&bookingId=".$res->id."');";
            $this->msg .= "</script>";
        } else {
            $this->msg = "";
            $this->msg .= "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            $this->msg .= "<i class='fa fa-warning'></i> ";
            $this->msg .= "Unable to comply, the company you tried to create already exists.";
            $this->msg .= "</div>";
            $this->createdCustomerFailed = true;
        }
    }
    
    public function createSetUser() {
        $name = "";
        if(isset($_POST['data']['nameofholder'])) {
            $name = $_POST['data']['nameofholder'];
        }
        if(!$name) {
            return "quickreservation";
        }
        
        $user = $this->getApi()->getUserManager()->getUserById($name);
        if($user) {
            return $user->id;
        }
        
        $user = new \core_usermanager_data_User();
        $user->fullName = $name;
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user->id;
    }
    
    public function checkForExisiting() {
        $text = $_POST['data']['text'];
        $users = $this->getApi()->getUserManager()->findUsers($text);
        if(sizeof($users) == 0) {
            return;
        }
        
        echo "<div style='text-align:left; width: 800px; display:inline-block;font-size: 20px; margin-top: 20px; font-weight:bold;'>We already have " . sizeof($text) . " users matching this search, is it one of the below?</div>";
        echo "<table style=width:800px;float:right;text-align:left; margin-bottom: 50px;'>";
        echo "<tr>";
        echo "<th>Name</th>";
        echo "<th>Email</th>";
        echo "<th>Prefix</th>";
        echo "<th>Phone</th>";
        echo "<th>Address</th>";
        echo "<th>Postcal code</th>";
        echo "<th>City</th>";
        echo "<th></th>";
        echo "</tr>";
        
        foreach($users as $user) {
            echo "<tr userid='".$user->id."' name='".$user->fullName."'>";
            echo "<td>" . $user->fullName . "</td>";
            echo "<td>" . $user->emailAddress . "</td>";
            echo "<td>" . $user->prefix . "</td>";
            echo "<td>" . $user->cellPhone . "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->address; } echo "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->postCode; } echo "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->city; } echo "</td>";
            echo "<td style='color:blue; cursor:pointer;' class='selectuser'>Select</td>";
            echo "</tr>";
        }
        echo "</table>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
    }

}
?>

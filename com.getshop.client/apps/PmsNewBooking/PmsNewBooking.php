<?php
namespace ns_74220775_43f4_41de_9d6e_64a189d17e35;

class PmsNewBooking extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNewBooking";
    }

    public function render() {
        if(isset($this->msg)) {
            echo $this->msg;
        }
        echo "<div style='max-width:1500px; margin:auto;' class='dontExpand'>";
        echo "<div class='addnewroom'>";
        $this->includefile("newbooking");
        echo "</div>";
        echo "<div class='availablerooms'>";
        $this->showAvailableRooms();
        echo "</div>";
        echo "<div style='clear:both;'></div>";
        $this->includefile("roomsadded");
        echo "</div>";
    }
    
    public function showAvailableRooms() {
        $this->includefile("availablerooms");
    }
    
    
    public function completequickreservation() {
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $currentBooking->userId = "quickreservation";
        $currentBooking->quickReservation = true;
        $currentBooking->avoidCreateInvoice = true;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
        $res = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedMultilevelDomainName());
        if(!$res) {
            $this->msg = "";
            $this->msg .= "<div style='border: solid 1px; background-color:red; padding: 10px; font-size: 16px; color:#fff;'>";
            $this->msg .= "<i class='fa fa-warning'></i> ";
            $this->msg .= "Unable to comply, your selection is a possible.";
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
            $this->msg .= "thundashop.common.goToPageLink('/?page=groupbooking&bookingEngineId=".$bookingEngineId."');";
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
        return date('m/d/y', $app->getStartDate());
    }
    
    public function loadResult() {
        
    }
    
    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('m/d/y', $app->getEndDate());
    }
    
    public function addRoom() {
        
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['from']);
        $app->setEndDate($_POST['data']['to']);
        
        $type = $_POST['data']['type'];
        $from = strtotime($_POST['data']['from']);
        $to = strtotime($_POST['data']['to']);
        $numberOfRooms = $_POST['data']['numberOfRooms'];
        
        $currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        for($i = 0; $i < $numberOfRooms; $i++) {
            $room = new \core_pmsmanager_PmsBookingRooms();
            $room->date = new \core_pmsmanager_PmsBookingDateRange();
            $room->date->start = $this->convertToJavaDate(strtotime($_POST['data']['from']. " " . $config->defaultStart));
            $room->date->end = $this->convertToJavaDate(strtotime($_POST['data']['to']. " " . $config->defaultEnd));
            $room->bookingItemTypeId = $_POST['data']['type'];
            $currentBooking->rooms[] = $room;
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedMultilevelDomainName(), $currentBooking);
    }
    
}
?>

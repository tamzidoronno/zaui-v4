<?php
namespace ns_b675ce83_d771_4332_ba09_a54ed8537282;

class PmsBookingMyBookingList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingMyBookingList";
    }

    public function loadEditRoom() {
        $this->includefile("editroomform");
    }

    
    public function getTmpUser() {
        if(isset($_SESSION['mybookinglist_tmp_user'])) {
            return $_SESSION['mybookinglist_tmp_user'];
        }
        return $this->getApi()->getUserManager()->getLoggedOnUser()->id;
    }
    
    public function setTmpUser() {
        $_SESSION['mybookinglist_tmp_user'] = $_POST['data']['userid'];
    }
    
    public function render() {
        if($this->isEditorMode()) {
            $this->includefile("allusersdropdown");
        }
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
        }
        echo "<span class='editroomform'></span>";
        $this->includefile("bookinglist");
    }
    
    public function updateBooking() {
        $bookingid = $_POST['data']['bookingid'];
        $roomid = $_POST['data']['roomid'];
        
        $room = new \core_pmsmanager_PmsBookingRooms();
        $room->date = new \core_pmsmanager_PmsBookingDateRange();
        $room->date->start = $this->convertToJavaDate(strtotime($_POST['data']['startdate']));
        $room->date->end = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $room->numberOfGuests = $_POST['data']['guestcount'];
        $room->guests = array();
        $room->pmsBookingRoomId = $_POST['data']['roomid'];
        for($i = 0; $i < $room->numberOfGuests; $i++) {
            $guest = new \core_pmsmanager_PmsGuests();
            $guest->name = $_POST['data']['name_'.$i];
            $guest->phone = $_POST['data']['phone_'.$i];
            $guest->email = $_POST['data']['email_'.$i];
            $room->guests[] = $guest;
        }
        
        $this->getApi()->getPmsManager()->updateRoomByUser($this->getSelectedName(), $bookingid, $room);
    }
    
    public function deleteRoom() {
        $bookingId = $_POST['data']['bookingid'];
        $roomId = $_POST['data']['roomid'];
        $this->getApi()->getPmsManager()->removeFromBooking($this->getSelectedName(), $bookingId, $roomId);
    }
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }

    /**
     * @return \core_bookingengine_data_BookingItem[]
     */
    public function getBookingItems() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        return $this->indexList($items);
    }

    /**
     * @return \core_bookingengine_data_BookingItemType[]
     */
    public function getBookingItemTypes() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        return $this->indexList($types);
    }

    public function getBookings() {
        if(!$this->getTmpUser()) {
            return $this->getApi()->getPmsManager()->getAllBookingsForLoggedOnUser($this->getSelectedName());
        }
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->userId = $this->getTmpUser();
        
        return $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
      
    }

}
?>

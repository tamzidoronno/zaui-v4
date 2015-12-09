<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingContactData";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        $this->includefile("roomcontactdata");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function setContactData() {
        $current = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        
        
        foreach($_POST['data']['roomdata'] as $roomId => $guests) {
            $result = array();
            foreach($guests as $index => $guest) {
                $guestData = new \core_pmsmanager_PmsGuests();
                $guestData->email = $guest['email'];
                $guestData->phone = $guest['phone'];
                $guestData->name = $guest['name'];
                $result[] = $guestData;
            }
            foreach($current->rooms as $room) {
                /* @var $room \core_pmsmanager_PmsBookingRooms */
                if($room->pmsBookingRoomId == $roomId) {
                    $room->guests = $result;
                }
            }
        }
        
        //Setting contact data.
        $current->contactData->type = 1;
        $current->contactData->address = $_POST['data']['billingdata']['address'];
        $current->contactData->postalCode = $_POST['data']['billingdata']['postalCode'];
        $current->contactData->city = $_POST['data']['billingdata']['city'];
        $current->contactData->email = $_POST['data']['billingdata']['email'];
        if(isset($_POST['data']['billingdata']['orgid'])) {
            $current->contactData->orgid = $_POST['data']['billingdata']['orgid'];
            $current->contactData->name = $_POST['data']['billingdata']['name'];
            $current->contactData->type = 2;
        } else {
            $current->contactData->birthday = $_POST['data']['billingdata']['birthday'];
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $current);
        
        $validation = $this->getApi()->getPmsManager()->validateCurrentBooking($this->getSelectedName());
//        print_r($validation);
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
}
?>

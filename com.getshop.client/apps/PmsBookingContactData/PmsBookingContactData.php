<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    var $validation;
    var $bookingCompleted = false;
    
    public function getDescription() {
        return "Asks the user for to enter the nessesary contact data to complete the booking";
    }

    public function getName() {
        return "PmsBookingContactData";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        if($this->bookingCompleted) {
            $this->includefile("completedbooking");
        } else {
            $this->includefile("roomcontactdata");
        }
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
        if(isset($_POST['data']['billingdata']['username'])) {
            $current->contactData->type = 3;
            $current->contactData->username = $_POST['data']['billingdata']['username'];
            $current->contactData->password = $_POST['data']['billingdata']['password'];
        } else {
            $current->contactData->type = 1;
            $current->contactData->address = $_POST['data']['billingdata']['address'];
            $current->contactData->postalCode = $_POST['data']['billingdata']['postalCode'];
            $current->contactData->city = $_POST['data']['billingdata']['city'];
            $current->contactData->email = $_POST['data']['billingdata']['email'];
            $current->contactData->name = $_POST['data']['billingdata']['name'];
            $current->contactData->prefix = $_POST['data']['billingdata']['prefix'];
            $current->contactData->phone = $_POST['data']['billingdata']['phone'];
            $current->language = $this->getFactory()->getSelectedLanguage();
            if(isset($_POST['data']['billingdata']['orgid'])) {
                $current->contactData->orgid = $_POST['data']['billingdata']['orgid'];
                $current->contactData->type = 2;
            } else {
                $current->contactData->birthday = $_POST['data']['billingdata']['birthday'];
            }
        }
        $current->contactData->agreedToTerms = ($_POST['data']['agreetoterms'] == "true");
        
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $current);
        
        $this->validation = $this->getApi()->getPmsManager()->validateCurrentBooking($this->getSelectedName());

        $check = (array)$this->validation; 
        if(!$this->validation || empty($check)) {
            $result = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedName());
            if($result == -1) {
                $this->validation = new \stdClass();
                $this->validation->{'common'} = "An unknown error occured while completing the booking. The error has been logged and will be investegated.";
            } else if($result == -2) {
                $this->validation = new \stdClass();
                $this->validation->{'common'} = "The reservation could not be completed because the room you are trying to book has been taken since you started booking";
            } else {
                $this->bookingCompleted = true;
            }
        }
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }

    public function getValidation() {
        return $this->validation;
    }

    public function validateRoom($id, $type, $offset) {
        $validation = $this->getValidation();
        $key = "room_".$id."_".$offset."_".$type;
        if($validation) {
            if(isset($validation->{$key})) {
                echo "<span class='errordesc'>";
                switch($type) {
                    case "name":
                        echo "* " . $this->__w("Full name please");
                        break;
                    case "email":
                        echo "* " . $this->__w("Invalid email");
                        break;
                    case "phone":
                        echo "* " . $this->__w("Invalid phone number");
                        break;
                }
                echo "</span>";
            }
        }
    }
    
    public function validateContactData($field) {
        $validation = $this->getValidation();
        $key = "contact_".$field;
        if($validation) {
            if(isset($validation->{$key})) {
                echo "<span class='errordesc'>";
                echo "* " . $this->__w("Field is required");
                echo "</span>";
            }
        }
        if($field == "username") {
            if(isset($validation->{$field})) {
                echo "<span class='errordesc'>";
                echo $this->__w("Username or password is incorrect");
                echo "</span>";
            }
        }
        if($field == "agreedToTerms") {
            if(isset($validation->{$field})) {
                echo "<span class='errordesc'>";
                echo "* " . $this->__w("You need to agree to the terms and conditions first");
                echo "</span>";
            }
        }
    }

    public function displayFailedBookingErrors() {
        if(isset($this->validation->{'common'})) {
            echo $this->validation->{'common'};
        }
    }

    public function getNameText() {
        if($this->getConfigurationSetting("name_text")) {
            return $this->getConfigurationSetting("name_text");
        }
        return $this->__w("Name");
    }

}
?>

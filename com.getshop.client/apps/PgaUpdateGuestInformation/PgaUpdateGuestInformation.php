<?php
namespace ns_d8ac717e_8e03_4b59_a2c3_e61b064a21c2;

class PgaUpdateGuestInformation extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaUpdateGuestInformation";
    }

    public function render() {
        $this->includefile("guests");
    }
    
    public function saveGuestInformation() {
        $token = $this->getModalVariable("token");
        $roomid = $this->getModalVariable("roomid");
        
        $guestcount = 0;
        foreach ($_POST['data'] as $key => $value) {
            if (strstr($key, "guestid_")) {
                $guestcount++;
            }
        }
        
        $pgaBooking = new \core_pga_PgaBooking();
        $pgaBooking->pmsBookingRoomId = $roomid;
        
        $obj = $this->getStdErrorObject(); // Get a default error message
        $errors = array();
        
        for ($i = 1; $i <= $guestcount; $i++) {
            $guest = new \core_pmsmanager_PmsGuests();
            $guest->guestId = $_POST['data']['guestid_'.$i];
            $guest->name = $_POST['data']['name_'.$i];
            $guest->prefix = $_POST['data']['prefix_'.$i];
            $guest->email = $_POST['data']['email_'.$i];
            $guest->phone = $_POST['data']['phone_'.$i];
            
            if (isset($_POST['data']['passportid_'.$i])) {
                $guest->passportId = $_POST['data']['passportid_'.$i];
                if (!$guest->passportId) {
                    $errors['passportid_'.$i] = "true";
                }
            }
            
            $pgaBooking->guests[] = $guest;
        }
        
        
        if (count($errors)) {
            $obj->fields->errorMessage = "<i class='fa fa-warning'></i> ".$this->__f("Please check the fields in red."); // The message you wish to display in the gserrorfield
            foreach ($errors as $key => $value) {
                $obj->gsfield->{$key} = 1; // Will highlight the field that has gsname "hours"
            }
            $this->doError($obj); // Code will stop here.
        }

        $this->getApi()->getPgaManager()->updateGuests($this->getSelectedMultilevelDomainName(), $token, $pgaBooking);

    }
}
?>

<?php
namespace ns_dbe8930f_05d9_44f7_b399_4e683389f5cc;

class PmsConferenceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsConferenceList";
    }

    public function render() {
        $this->includefile("conferencelist");
    }
    
    public function updateDateRange() {
        $_SESSION['conferencelistperiodestart'] = $_POST['data']['start'];
        $_SESSION['conferencelistperiodeend'] = $_POST['data']['end'];
    }
    
    public function getRoomFromBooking($pmsBooking) {
        echo $pmsBooking->conferenceId . "<bR>";
    }

    public function loadBooking() {
        $pmsBookingGroupView = new \ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01\PmsBookingGroupRoomView();
        $pmsBookingGroupView->setRoomId($_POST['data']['id']);
        $pmsBookingGroupView->renderApplication(true, $this, true);
    }

    public function getStartDate() {
        if(isset($_SESSION['conferencelistperiodestart'])) {
            return $this->convertToJavaDate(strtotime($_SESSION['conferencelistperiodestart']));
        }
        return $this->convertToJavaDate(time());
    }

    public function getEndDate() {
        if(isset($_SESSION['conferencelistperiodeend'])) {
            return $this->convertToJavaDate(strtotime($_SESSION['conferencelistperiodeend']));
        }
        return $this->convertToJavaDate(time()+(86400*30));
    }

}
?>

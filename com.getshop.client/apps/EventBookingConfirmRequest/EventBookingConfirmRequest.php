<?php
namespace ns_bb707ed9_80ab_4afe_a2aa_6790e63a2b8e;

class EventBookingConfirmRequest extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventBookingConfirmRequest";
    }

    public function render() {
        if (isset($_GET['requestId'])) {
            $_SESSION['EventBookingConfirmRequest_requestId'] = $_GET['requestId'];
        }
        
        if (!isset($_SESSION['EventBookingConfirmRequest_requestId'])) {
            if ($_POST['event'] == "acceptRequest") {
                echo $this->__f("Thank you, candidate has been approved");
            }
            if ($_POST['event'] == "rejectRequest") {
                echo $this->__f("Thank you, candidate has been removed from the event");
            }
            return;
        }
        
        $this->includefile("confirmRequest");
    }
    
    public function acceptRequest() {
        $this->getApi()->getEventBookingManager()->handleEventRequest("booking", $_SESSION['EventBookingConfirmRequest_requestId'], true);
        unset($_SESSION['EventBookingConfirmRequest_requestId']);
    }
    
    public function rejectRequest() {
        $this->getApi()->getEventBookingManager()->handleEventRequest("booking", $_SESSION['EventBookingConfirmRequest_requestId'], false);
        unset($_SESSION['EventBookingConfirmRequest_requestId']);
    }
}
?>

<?php
namespace ns_fd348fe7_3d46_44b9_b75e_f65f827737a0;

class EventTransferUser extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventTransferUser";
    }

    public function render() {
        if (isset($_POST['data']['fromeventid'])) {
            echo "User has been transferred";
        } else {
            $events = $this->getApi()->getEventBookingManager()->getEvents("booking");
            foreach ($events as $event) {
                $date = \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\Event::formatMainStartDates($event);
                $name = $event->bookingItemType->name;
                $loc = $event->location->name." - ".$event->subLocation->name;

                $button = "Full";

                if ($event->canBookWaitingList)
                    $button = "Move waitinglist";

                if ($event->canBook)
                    $button = "Move";

                if ($button) {
                    $userId = $this->getModalVariable("userid");
                    $from = $this->getModalVariable("eventid");
                    $to = $event->id;

                    $button = "<div gsclick='transferUser' fromeventid='$from' toeventid='$to' userid='$userId' class='shop_button'>$button</div>";
                }

                echo "<div class='transferrow'>$date &nbsp;&nbsp; | <span style='display: inline-block; width: 200px;'> $loc </span> | &nbsp;&nbsp; <span style='display: inline-block; width: 500px;'> $name </span> $button </div>";
            }
        }
    }
    
    public function transferUser() {
        $this->getApi()->getEventBookingManager()->moveUserToEvent("booking", $_POST['data']['userid'], $_POST['data']['fromeventid'], $_POST['data']['toeventid']);
    }
}
?>
<?php
namespace ns_74ea4e90_2d5a_4290_af0c_230a66e09c78;

class Booking extends \MarketingApplication implements \Application {
    
    public function getDescription() {
        return $this->__f("A booking application, used with the Calendar Application");
    }

    public function getName() {
        return $this->__f("Booking");
    }

    public function render() {
        if (isset($_GET['entryId'])) {
            $_SESSION['entryId'] = $_GET['entryId'];
        }
        
        if (isset($_SESSION['entryId'])) 
            $_GET['entryId'] = $_SESSION['entryId'];
                
        $this->includefile("schema");
    }
    
    public function getAttendees($entry) {
        if (count($entry->attendees) == 0) {
            return array();
        }
        
        if (!$this->hasReadAccess()) {
            return $entry->attendees;
        }
        
        return $this->getApi()->getUserManager()->getUserList($entry->attendees);
    }
    
    public function getCountOfFreePositions($entry) {
        $attendees = $this->getAttendees($entry);
        return $entry->maxAttendees - count($attendees);
    }
    
    public function getSummary() {
        $entry = $this->getApi()->getCalendarManager()->getEntry($_POST['data']['entryId']);
        
        $adults = 0;
        $children = 0;
        
        foreach ($_POST['data']['persons'] as $person) {
            if ($person['children'] == "true") {
                $children++;
            } else {
                $adults++;
            }
        }
        
        $priceAdults = $entry->event->priceAdults * $adults;
        $priceChildren = $entry->event->priceChild * $children;
        $total = $priceAdults + $priceChildren;
        echo "<div class='booking_result'>";
        echo "<div class='resultrow'><div class='summary_row_col1'></div><div class='summaryprice'>Pris</div></div>";
        echo "<div class='resultrow'><div class='summary_row_col1'>Antall voksne: $adults </div><div class='summaryprice'>Kr $priceAdults,-</div></div>";
        echo "<div class='resultrow'><div class='summary_row_col1'>Antall barn: $children </div><div class='summaryprice'>Kr $priceChildren,-</div></div>";
        echo "<div class='resultrow sumup'><div class='summary_row_col1 '>Totalt </div><div class='summaryprice'>Kr $total,-</div></div>";
        echo "<div>";
        
    }
}
?>

<?php
namespace ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5;

class PmsCheckList extends \MarketingApplication implements \Application {
    private $currentError = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCheckList";
    }

    public function render() {
        $this->printMonthSelector();
        if (isset($_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'])) {
            $this->printResult();
        }
    }

    /**
     * 
     * @return \core_checklist_CheckListError
     */
    public function getCurrentError() {
        return $this->currentError;
    }

    public function printMonthSelector() {
        $this->getCurrentMonth();
        echo "<div style='text-align: center' gstype='form' method='dateChanged'>";
        echo "Select month<br/> <select gsname='date' class='gsniceselect1'>";
        $now = time();
        for ($i=0;$i<24;$i++) {
            $year = date('Y', $now);
            $month = date('m', $now);
            $selected = $this->getCurrentMonth() == $year."_".$month ? "selected='true'" : "";
            echo "<option $selected value='".$year."_".$month."'>".date('M Y', $now)."</option>";
            $now = strtotime('-1 month', $now);
        }
        echo "</select>";
        echo "<br/><div class='shop_button' gstype='submit'>Show</div>";
        echo "</div>";
        
    }

    public function printResult() {
        $monthText = $this->getCurrentMonth();
        $monthText = explode("_", $monthText);
        $year = $monthText[0];
        $month = $monthText[1];
        $monthEnd = (int)$monthText[1]+1;
        
        $startTime = strtotime("01.$month.$year 00:00:00");
        $from = $this->convertToJavaDate($startTime);
        $end = $this->convertToJavaDate(strtotime("+1 month", $startTime));
        $result = $this->getApi()->getChecklistManager()->getErrors($this->getSelectedMultilevelDomainName(), $from, $end);

        $grouped = array();
        
        foreach ($result as $error) {
            if (!isset($grouped[$error->filterType])) {
                $grouped[$error->filterType] = array();
            }
            
            $grouped[$error->filterType][] = $error;
        }
        
        foreach ($grouped as $type => $errors) {
            echo "<div class='typebox'>";
                $this->printHeader($type);
                foreach ($errors as $error) {
                    $this->currentError = $error;
                    echo "<div class='errorrow'>";
                        $this->includefile($error->filterType);
                    echo "</div>";
                }
            echo "</div>";
        }
    }

    public function getCurrentMonth() {
        if (isset($_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'])) {
            return $_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'];
        }
        
        $year = date('Y');
        $month = date('m');
        return $year."_".$month;
   }

   public function dateChanged() {
       $_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'] = $_POST['data']['date'];
   }
   
   public function markBookingAsIgnoreUnsettledAmount() {
       $this->getApi()->getPmsManager()->markIgnoreUnsettledAmount($this->getSelectedMultilevelDomainName(), $_POST['data']['bookingid']);
   }

    public function printHeader($type) {
        if ($type == "UnsettledAmountProcessor") {
            echo "<div class='description'><h2>Unsettled Amount</h2><br/>Bookings with unsettled amounts are normally because you have forgotten to charge your guest for everything, please check them.</div>";   
        } elseif($type === "UnpaidPaymentLinksProcessor") {
            echo "<div class='description'><h2>".$this->__f("Payment links")."</h2>Normally paymentlinks are used to aquire prepaid bookings, if a booking has ended and the paymentlink still exists it could be a potential problem, please check them.</div>";
        } elseif($type === "ExpediaVirtualCreditCardCheckProcessor") {
            echo "<div class='description'><h2>".$this->__f("Expedia")."</h2>Its important to keep track of the unpaid expedia.com bookings, this is normally Virtual Credit Cards that has to be manually processed by someone.</div>";
        } elseif($type === "BookingComVirtualCreditCardCheckProcessor") {
            echo "<div class='description'><h2>".$this->__f("Booking.com")."</h2>Its important to keep track of the unpaid booking.com bookings, this is normally Virtual Credit Cards that has to be manually processed by someone.</div>";
        } elseif($type === "DiffByPaidAmountToTotal") {
            echo "<div class='description'><h2>".$this->__f("Different in paid and charged amount")."</h2>This check is checking if you have rooms that are having a difference in what is marked as paid and what charged.</div>";
        } elseif($type === "AccruedPaymentChecklist") {
            echo "<div class='description'><h2>".$this->__f("Pay after stay activated")."</h2>Make sure that the pay after stay are deactivated after the stay has ended.</div>";
        } else {
            echo "<h2>".$type."</h2>";
        }
    }

    public function markOrderAsBillable() {
        $this->getApi()->getOrderManager()->markOrderAsBillabe($_POST['data']['orderid']);
    }
}
?>

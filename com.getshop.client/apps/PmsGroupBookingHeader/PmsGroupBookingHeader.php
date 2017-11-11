<?php
namespace ns_cbcf3e53_c035_43c2_a1ca_c267b4a8180f;

class PmsGroupBookingHeader extends \MarketingApplication implements \Application {
    private $currentBooking = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGroupBookingHeader";
    }

    public function render() {
        $this->setBookingEngineId();
        $this->includefile("groupheader");
    }

    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getCurrentBooking() {
        if (!$this->currentBooking) {
            $this->currentBooking = $this->getApi()->getPmsManager()->getBookingFromBookingEngineId($this->getSelectedMultilevelDomainName(), $_SESSION['PmsSearchBooking_groupbookingengineid']);
        }
        
        return $this->currentBooking;
    }

    public function setBookingEngineId() {
        if (isset($_GET['bookingEngineId'])) {
            $_SESSION['PmsSearchBooking_groupbookingengineid'] = $_GET['bookingEngineId'];
        }
    }
    
    

}
?>

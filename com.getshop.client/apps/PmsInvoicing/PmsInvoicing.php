<?php
namespace ns_6f51a352_a5ee_45ca_a8e2_e187ad1c02a5;

class PmsInvoicing extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsInvoicing";
    }

    public function render() {
        $this->includefile("invoicepayment");
    }
    
    public function loadBooking() {
        $pmsBookingGroupView = new \ns_3e2bc00a_4d7c_44f4_a1ea_4b1b953d8c01\PmsBookingGroupRoomView();
        $pmsBookingGroupView->setRoomId($_POST['data']['id']);
        $pmsBookingGroupView->renderApplication(true, $this, true);
    }
    
}
?>

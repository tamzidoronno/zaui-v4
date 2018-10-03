<?php
namespace ns_d79569c6_ff6a_4ab5_8820_add42ae71170;

class BookingComCollectPayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Booking.com started to introduce payments trough booking.com in 2017. Whenever someone pay directly to booking.com this application is used.";
    }

    public function getName() {
        return "Booking.com";
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }

    public function render() {
        
    }
    
    
    public function saveSettings() {
        $this->setConfigurationSetting("automarkpaid", $_POST['automarkpaid']);
    }
    
    
}
?>

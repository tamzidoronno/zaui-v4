<?php
namespace ns_ed7efba0_de37_4cd8_915b_cc7be10b8b8b;

class PmsBookingProductList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingProductList";
    }

    public function getAllProducts() {
        return $this->getApi()->getProductManager()->getAllProducts();
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }

    public function getCurrentBooking() {
        $booking = new \core_pmsmanager_PmsBooking();
        $booking->products[] = "78f15eb3-a998-471f-ae80-10cc61bf39a0";
        return $booking;
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine.";
            return;
        }
        $this->includefile("productlist");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
}
?>

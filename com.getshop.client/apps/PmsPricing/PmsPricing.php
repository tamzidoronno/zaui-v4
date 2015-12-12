<?php
namespace ns_1be25b17_c17e_4308_be55_ae2988fecc7c;

class PmsPricing extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsPricing";
    }

    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "PLease specify a booking engine first";
        } else {
            $this->includefile("pricingview");
        }
    }


    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function getStart() {
        return date("m.d.Y", time());
    }

    public function getEnd() {
        return date("m.d.Y", time()+(86400*365));        
    }

    public function getPrices() {
        return $this->getApi()->getPmsManager()->getPrices($this->getSelectedName(), 
                $this->convertToJavaDate(strtotime($this->getStart())),
                $this->convertToJavaDate(strtotime($this->getEnd())));
    }

}
?>

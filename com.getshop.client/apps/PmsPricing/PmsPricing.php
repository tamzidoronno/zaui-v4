<?php
namespace ns_1be25b17_c17e_4308_be55_ae2988fecc7c;

class PmsPricing extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Set prices for your booking, by setting default prices or daily prices for a given time periode";
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
    
    public function setNewPrices() {
        $prices = $_POST['data']['prices'];
        
        $pricingObject = new \core_pmsmanager_PmsPricing();
        $pricingObject->specifiedPrices = $prices;
        $pricingObject->defaultPriceType = $_POST['data']['pricetype'];
        
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $pricingObject);
    }

    public function selectDates() {
        $start = $_POST['data']['start'];
        $end = $_POST['data']['end'];
        
        $_SESSION['pmspricing'][$this->getSelectedName()]['start'] = $start;
        $_SESSION['pmspricing'][$this->getSelectedName()]['end'] = $end;
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
        if(isset($_SESSION['pmspricing'][$this->getSelectedName()]['start'])) {
            return $_SESSION['pmspricing'][$this->getSelectedName()]['start'];
        }
        return date("d.m.Y", time());
    }

    public function getEnd() {
        if(isset($_SESSION['pmspricing'][$this->getSelectedName()]['end'])) {
            return $_SESSION['pmspricing'][$this->getSelectedName()]['end'];
        }
        return date("d.m.Y", time()+(86400*90));        
    }

    public function getPrices() {
        return $this->getApi()->getPmsManager()->getPrices($this->getSelectedName(), 
            $this->convertToJavaDate(strtotime($this->getStart())),
            $this->convertToJavaDate(strtotime($this->getEnd())));
    }

}
?>

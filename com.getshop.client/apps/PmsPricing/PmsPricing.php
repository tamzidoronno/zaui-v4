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
        $pricingObject = new \core_pmsmanager_PmsPricing();
        if(isset($_POST['data']['prices'])) {
            $prices = $_POST['data']['prices'];
            $pricingObject->dailyPrices = $prices;
        }

        $pricingObject->defaultPriceType = $_POST['data']['pricetype'];
        $pricingObject->pricesExTaxes = $_POST['data']['prices_ex_taxes'] == "true";
        $pricingObject->privatePeopleDoNotPayTaxes = $_POST['data']['privatePeopleDoNotPayTaxes'] == "true";

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

    public function updateProgressivePrices() {
        $itemTypes = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $result = [];
        foreach($itemTypes as $type) {
            $typeId = $type->id;
            for($i = 0; $i < 10; $i++) {
                $entry = new \core_pmsmanager_ProgressivePriceAttribute();
                $entry->numberOfTimeSlots = $_POST['data']['date_slot_'.$i."_".$typeId];
                $entry->price = $_POST['data']['price_slot_'.$i."_".$typeId];
                if($entry->numberOfTimeSlots) {
                    if(!isset($result[$typeId])) {
                        $result[$typeId] = array();
                    }
                    $result[$typeId][] = $entry;
                }
            }
        }
        
        
        $pricingObject = new \core_pmsmanager_PmsPricing();
        $pricingObject->progressivePrices = $result;

        $pricingObject->defaultPriceType = $_POST['data']['pricetype'];

        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $pricingObject);
    }
    
    public function getPrices() {
        return $this->getApi()->getPmsManager()->getPrices($this->getSelectedName(), 
            $this->convertToJavaDate(strtotime($this->getStart())),
            $this->convertToJavaDate(strtotime($this->getEnd())));
    }

}
?>

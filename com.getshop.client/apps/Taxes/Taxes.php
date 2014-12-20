<?php

namespace ns_deab499e_b133_4cb6_a1b2_532018c31d46;

class Taxes extends \ApplicationBase implements \Application {

    function __construct() {
        
    }

    public function renderConfig() {
        $taxes = $this->getApi()->getProductManager()->getTaxes();

        if (sizeof($taxes) > 0) {
            $taxgroups = array();
            foreach ($taxes as $tax) {
                $taxgroups[$tax->groupNumber] = $tax;
            }

            $setting = new \core_common_Settings();
            $setting->id = "shipmentent";
            $setting->value = $taxgroups[0]->taxRate;
            @$this->configuration->settings->{"shipmentent"} = $setting;
            
            foreach($taxgroups as $number => $taxobj) {
                if($number == 0) {
                    continue;
                }
                $setting = new \core_common_Settings();
                $setting->id = "tax_group_". $number;
                $setting->value = $taxgroups[$number]->taxRate;
                $this->configuration->settings->{"tax_group_". $number} = $setting;
            }
        }

        $this->includefile("taxesconfig");
    }

    public function getDescription() {
        return $this->__f("Handles taxes for products and shipment.");
    }

    public function getName() {
        return $this->__f("Taxes");
    }
    
    public function applicationDeleted() {
        $taxes = array();
        $this->getApi()->getProductManager()->setTaxes($taxes);
    }
    
    public function saveTaxes() {
        $result = array();
        $grp = new \core_productmanager_data_TaxGroup();
        $grp->groupNumber = 0;
        $grp->taxRate = $_POST['shipment'];
        $result[] = $grp;

        for ($i = 1; $i <= 5; $i++) {
            $grp = new \core_productmanager_data_TaxGroup();
            $grp->groupNumber = $i;
            $grp->taxRate = $_POST['group'.$i];
            $result[] = $grp;
        }
        
        $this->getApi()->getProductManager()->setTaxes($result);
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        
    }

    public function render() {
        
    }

}

?>

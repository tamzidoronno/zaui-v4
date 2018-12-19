<?php
namespace ns_d0c34aa6_36c7_40dd_9ef2_4fa8844f442d;

class EcommerceTaxes extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceTaxes";
    }

    public function render() {
        $this->includefile("taxconfig");
    }
    
    public function saveTaxes() {
        $result = array();
        $grp = new \core_productmanager_data_TaxGroup();
        $grp->groupNumber = 0;
        $grp->taxRate = 0;
        $result[] = $grp;

        for ($i = 1; $i <= 5; $i++) {
            $grp = new \core_productmanager_data_TaxGroup();
            $grp->groupNumber = $i;
            $grp->taxRate = $_POST['data']['group'.$i];
            $grp->accountingTaxAccount = $_POST['data']['accounttaxcode'.$i];
            $result[] = $grp;
        }
        
        $this->getApi()->getProductManager()->setTaxes($result);
    }
}
?>

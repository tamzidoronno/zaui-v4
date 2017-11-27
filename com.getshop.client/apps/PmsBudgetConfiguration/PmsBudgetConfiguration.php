<?php
namespace ns_bb158014_c205_4d6a_a191_3315bdaa78dc;

class PmsBudgetConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBudgetConfiguration";
    }

    public function render() {
        $this->includefile("budget");
    }
    
    public function saveBudget() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        for($i = 1; $i <= 12; $i++) {
            $config->budget->{$i}->coverage_percentage = $_POST['data']['budget_percentage_'.$i];
            $config->budget->{$i}->budget_amount = $_POST['data']['budget_amount_'.$i];
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
}
?>

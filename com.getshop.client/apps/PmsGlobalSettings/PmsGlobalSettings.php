<?php
namespace ns_9cea7eba_7807_4e4c_8d60_e7d58fbad13a;

class PmsGlobalSettings extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGlobalSettings";
    }

    public function render() {
        $this->includefile("globalsettings");
    }
    
    public function saveSettings() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        foreach($_POST['data'] as $field => $val) {
            $config->{$field} = $val;
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
        $this->getApi()->getStoreManager()->setDefaultMultilevelName($_POST['data']['defaultmultilevelname']);
        $this->changeMultiLevelName($_POST['data']['defaultmultilevelname']);
    }
    
    public function correctAllOrders() {
        $this->getApi()->getPmsInvoiceManager()->recalculateAllBookings($this->getSelectedMultilevelDomainName(), $_POST['data']['prompt']);
    }
    public function clearBookingSystem() {
        $this->getApi()->getPmsManager()->deleteAllBookings($this->getSelectedMultilevelDomainName(), $_POST['data']['prompt']);
    }

  
}
?>

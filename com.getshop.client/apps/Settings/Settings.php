<?php
namespace ns_d755efca_9e02_4e88_92c2_37a3413f3f41;

class Settings extends \SystemApplication implements \Application {
    
    public function getDescription() {}
    
    public function getName() {
        return "Store settings";
    }
    
    public function postProcess() {}
    public function preProcess() {}
    
	public function render() {
    }
    
    public function renderConfig() {
        $this->includefile("storesettings");
    }
    
    public function getMainEmailAddress() {
        if(isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";    
    }
    
    public function isAvailable() {
        return false;
    }
	
	public function saveStoreSettings() {
		echo "Saving";
	}
	
	public function deleteStore() {
		echo "Deleting store";
	}
}
?>
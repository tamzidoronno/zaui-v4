<?php
namespace ns_9e0bbaa4_961b_4235_8a1f_47eb4414ed91;

class PmsCleaningConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCleaningConfiguration";
    }

    public function render() {
        $this->includefile("configuration");
    }
    
    public function saveCleaningConfig() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        foreach($_POST['data'] as $key => $value) {
            $config->{$key} = $value;
        }
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
}
?>

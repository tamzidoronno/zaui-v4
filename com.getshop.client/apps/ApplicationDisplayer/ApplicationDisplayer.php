<?php
namespace ns_c841f5a5_ecd5_4007_b9da_2c7538c07212;

class ApplicationDisplayer extends \SystemApplication implements \Application {
    private $name = "ApplicationDisplayer";
    private $showAddSingleton = false;
    
    public function showAddSingleton() {
        return $this->showAddSingleton;
    }
    
    public function getAvailablePositions() {
        return "middle";
    }
    
    public function getDescription() {
    }

    public function getName() {
        return $this->name;
    }
    
    public function setName($name) {
        $this->name = $name;
    }

    public function postProcess() {
    }

    public function preProcess() {
    }
    
    public function render() {
        $this->includefile("GSApplicationSet", 'ApplicationManager');
    }
   
    public function renderConfig() {
        $this->showAddSingleton = true;
        $this->includefile("GSApplicationSet", 'ApplicationManager');
    }

    public function isAdded($id) {
        return $this->getFactory()->getApplicationPool()->isSingletonAndAddedBySettingsId($id);
    }
}

?>
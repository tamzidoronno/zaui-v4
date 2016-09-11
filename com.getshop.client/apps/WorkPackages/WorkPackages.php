<?php
namespace ns_c0f2b792_479c_49ad_8e46_34e50f7f4e88;

class WorkPackages extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "WorkPackages";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }
    
    public function searchWorkPackages() {
        
    }
    
    public function createWorkPackage() {
        $wp = new \core_c3_WorkPackage();
        $this->savePackage($wp);
    }
    
    public function deleteWorkPackage() {
        $this->getApi()->getC3Manager()->deleteWorkPackage($_POST['value']);
    }
    
    public function saveWorkPackage() {
        $wp = $this->getApi()->getC3Manager()->getWorkPackage($_POST['value']);
        $wp->description = $_POST['description'];
        $wp->owner = $_POST['owner'];
        $this->savePackage($wp);
    }

    public function savePackage($wp) {
        $wp->name = $_POST['name'];
        $package = $this->getApi()->getC3Manager()->saveWorkPackage($wp);
        $_POST['value'] = $package->id;
    }

}
?>

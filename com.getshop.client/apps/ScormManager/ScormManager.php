<?php
namespace ns_9fa1d284_3eb2_4ca5_b0bc_fe45cde07f66;

class ScormManager extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ScormManager";
    }
    
    public function saveScormPackage() {
        $dataObject = array();
        
        foreach ($_POST['data'] as $key => $value) {
            if ($value == "false") {
                continue;
            }
            
            $splittedString = explode("_", $key);
            $scormId = $splittedString[1];
            $groupId = $splittedString[0];
            $dataObject[$scormId][] = $groupId;
        }
        
        $inPackages = $this->getScorms();
        
        foreach ($inPackages as $ipackage) {
            $package = new \core_scormmanager_ScormPackage();
            $package->id = $ipackage->id;
            $package->activatedGroups = @$dataObject[$ipackage->id];
            $package->name = $this->getScormName($package->id, $inPackages);
            $this->getApi()->getScormManager()->saveSetup($package);
        }
    }
    
    private function getScormName($id, $scorms) {
        foreach ($scorms as $scorm) {
            if ($scorm->id == $id) {
                return $scorm->name;
            }
        }
        
        return "";
    }

    public function render() {
        $this->includefile("admin");
    }
    
    public function getScorms() {
        $response = file_get_contents('http://moodle.getshop.com/mod/scorm/scormlist.php');
        
        
        return json_decode($response);
    }

    public function isActivated($packages, $groupId, $scormId) {
        foreach ($packages as $package) {
            if ($package->id == $scormId) {
                return in_array($groupId, $package->activatedGroups);
            }
        }
        
        return false;
    }
}
?>

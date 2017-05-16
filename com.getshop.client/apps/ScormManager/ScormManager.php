<?php
namespace ns_9fa1d284_3eb2_4ca5_b0bc_fe45cde07f66;

class ScormManager extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ScormManager";
    }
    
    public function moveDown() {
        $package = $this->getApi()->getScormManager()->getPackage($_POST['data']['packageid']);
        $index = intval($_POST['data']['index']);
        
        $item = $package->groupedScormPackages[ $index ];
        $package->groupedScormPackages[ $index ] = $package->groupedScormPackages[ $index + 1 ];
        $package->groupedScormPackages[ $index + 1 ] = $item;
        
        $this->getApi()->getScormManager()->saveSetup($package);
    }
    
    public function moveUp() {
        $package = $this->getApi()->getScormManager()->getPackage($_POST['data']['packageid']);
        $index = intval($_POST['data']['index']);
        
        $item = $package->groupedScormPackages[ $index ];
        $package->groupedScormPackages[ $index ] = $package->groupedScormPackages[ $index - 1 ];
        $package->groupedScormPackages[ $index - 1 ] = $item;
        
        $this->getApi()->getScormManager()->saveSetup($package);
    }
    
    public function saveScormPackage() {
        $dataObject = array();
        
        foreach ($_POST['data'] as $key => $value) {
            if ($value == "false" ) {
                continue;
            }
            
            $splittedString = explode("_", $key);
            if($splittedString[0] == "content") {
                continue;
            }
            
            if (count($splittedString) < 2) {
                continue;
            }
            
            $scormId = $splittedString[1];
            $groupId = $splittedString[0];
            $dataObject[$scormId][] = $groupId;
        }
        
        $alreadySavedPackages = $this->getApi()->getScormManager()->getAllPackages();
        
        $inPackages = $this->getScorms();
        
        foreach ($inPackages as $ipackage) {
            if ($this->isGroupedScormPackage($alreadySavedPackages, $ipackage->id)) {
                continue;
            }
                    
            $package = new \core_scormmanager_ScormPackage();
            $package->id = $ipackage->id;
            $package->activatedGroups = @$dataObject[$ipackage->id];
            $package->name = $this->getScormName($package->id, $inPackages);
            $package->isRequired = $_POST['data']['required_'.$ipackage->id] === "true";
            $this->getApi()->getScormManager()->saveSetup($package);
        }
        
        // Save all grouped scorm packages
        foreach ($alreadySavedPackages as $package) {
            if (!count($package->groupedScormPackages)) {
                continue;
            }
            
            $package->isRequired = $_POST['data']['required_'.$package->id] === "true";
            $package->activatedGroups = @$dataObject[$package->id];
            $this->getApi()->getScormManager()->saveSetup($package);
        }
        
        foreach ($_POST['data'] as $key => $value) {
            if ($value == "false" ) {
                continue;
            }
            
            $splittedString = explode("_", $key);
            if($splittedString[0] !== "content") {
                continue;
            }
            
            $id = $splittedString[1];
            $dbObject = new \core_scormmanager_ScormCertificateContent();
            $dbObject->scormId = $id;
            $dbObject->content = $value;
            $this->getApi()->getScormManager()->saveScormCertificateContent($dbObject);
        }
    }
    
    public function deletePackage() {
        $this->getApi()->getScormManager()->deleteScormPackage($_POST['data']['packageid']);
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
    
public function getPackage($packages, $scormId) {
        foreach ($packages as $package) {
            if ($package->id == $scormId) {
                return $package;
            }
        }
        
        return null;
    }
    
    public function getScormContentForCertificate($id) {
        $dbObj = $this->getApi()->getScormManager()->getScormCertificateContent($id);
        if ($dbObj) {
            return $dbObj->content;
        } else {
            return "";
        }
    }
    
    public function isGroupedScormPackage($packages, $scormId) {
        foreach ($packages as $package) {
            if ($package->groupedScormPackages) {
                foreach ($package->groupedScormPackages as $id) {
                    if ($scormId === $id)
                        return true;
                }
            }
        }
        
        return false;
    }
    
    public function createGroupedScormPackage() {
        $scormPackage = new \core_scormmanager_ScormPackage();
        $scormPackage->name = $_POST['data']['name'];
        $scormPackage->groupedScormPackages = array();
        foreach ($_POST['data'] as $key => $value) {
            if (strstr($key, "scormpackage_") && $_POST['data'][$key] === "true") {
                $vals = explode("_", $key);
                $scormPackage->groupedScormPackages[] = $vals[1];
            }
        }
        
        $this->getApi()->getScormManager()->saveSetup($scormPackage);
    }

    public function getScorm($scorms, $scormId) {
        foreach ($scorms as  $scorm) {
            if ($scorm->id == $scormId)
                return $scorm;
        }
        
        return null;
    }

}
?>
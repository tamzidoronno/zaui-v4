<?php
namespace ns_3cea6008_1b87_4a2e_aeb2_1c7fa2069279;

class EditLocation extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EditLocation";
    }

    public function preProcess() {
        if (isset($_POST['data']['locationId'])) {
            $_GET['locationId'] = $_POST['data']['locationId'];
        }
    }
    
    public function render() {
        if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("editlocation");
        }
    }
    
    public function addSubLocation() {
        $subLocation = new \core_eventbooking_SubLocation();
        $subLocation->name = $_POST['data']['sublocationname'];
        $location = $this->getLocation();
        if (!$location->locations) 
            $location->locations = [];
        
        $location->locations[] = $subLocation;
        $this->getApi()->getEventBookingManager()->saveLocation($this->getBookingEgineName(), $location);
   }

    public function getLocation() {
        if (isset($_POST['data']['locationId'])) {
            $locationId = $_POST['data']['locationId'];
        } else {
            $locationId = $_GET['locationId'];
        }
        return $this->getApi()->getEventBookingManager()->getLocation($this->getBookingEgineName(), $locationId);
    }

    public function saveall() {
        $location = $this->getLocation();
        $location->name = $_POST['data']['name'];
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        
        foreach ($location->locations as $subLocation) {
            $id = $subLocation->id;
            $subLocation->name= $_POST['data'][$id."_name"];
            $subLocation->description = $_POST['data'][$id."_description"];
            $subLocation->contactPerson = $_POST['data'][$id."_contactperson"];
            $subLocation->contactEmail = $_POST['data'][$id."_contactemail"];
            $subLocation->privateDescription = $_POST['data'][$id."_privatedescription"];
            $subLocation->lat = $_POST['data'][$id."_lat"];
            $subLocation->lon = $_POST['data'][$id."_lon"];
            $subLocation->cellPhone = $_POST['data'][$id."_cellphone"];
            $subLocation->extraDescription = $_POST['data'][$id."_extradescription"];
            
//            $subLocation->groupLocationInformation = [];
            $groupInfos = new \stdClass();
            foreach ($groups as $group) {
                $groupInfo = new \core_eventbooking_GroupLocationInformation();
                $key = $group->id."_".$subLocation->id."_";
                $groupInfo->name = $_POST['data'][$key."name"];
                $groupInfo->cellPhone = $_POST['data'][$key."cellPhone"];
                $groupInfo->email = $_POST['data'][$key."email"];
                $groupInfo->other = $_POST['data'][$key."other"];
                $groupInfo->groupId = $group->id;
                $groupInfos->{$group->id} = $groupInfo;
            }
            
            $subLocation->groupLocationInformation = $groupInfos;
        }

        
        $this->getApi()->getEventBookingManager()->saveLocation($this->getBookingEgineName(), $location);
    }
    
    public function deleteLocation() {
        $this->getApi()->getEventBookingManager()->deleteSubLocation($this->getBookingEgineName(), $_POST['data']['subLocationId']);
    }
}
?>


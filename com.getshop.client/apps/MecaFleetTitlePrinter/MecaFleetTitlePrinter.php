<?php
namespace ns_cc36d832_6593_404a_85bd_0464e148e48b;

class MecaFleetTitlePrinter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetTitlePrinter";
    }

    public function render() {
        $fleet = $this->getApi()->getMecaManager()->getFleetPageId($this->getPage()->getId());
        if ($fleet) {
                $this->includefile('mecafleettitleprint');
        } else {
            echo "<div class='title'>DEMO</div>";
        }
    }
    public function updateFleet() {
        if (isset($_POST['data']['fleetName']) && $_POST['data']['fleetName']) {
            $fleet = $this->getApi()->getMecaManager()->getFleetPageId($this->getPage()->getId());
            
            $fleet->name = $_POST['data']['fleetName'];
            $fleet->contactName = $_POST['data']['contactName'];
            $fleet->contactEmail = $_POST['data']['contactEmail'];
            $fleet->contactPhone = $_POST['data']['contactPhone'];
            $fleet->followup = $_POST['data']['fleetFollowup'];
            
            $fleet->discount = $_POST['data']['discount'];
            $fleet->rentalcar = $_POST['data']['rentalcar'];
            $fleet->contactDetails = @$_POST['data']['contactDetails'];
            $fleet->contactOther = $_POST['data']['contactOther'];
            
            if (isset($_POST['data']['frequency_monthly'])) {
                $fleet->naggingInterval = "frequency_monthly";
            }
            
            if (isset($_POST['data']['frequency_quartly'])) {
                $fleet->naggingInterval = "frequency_quartly";
            }
            
            if (isset($_POST['data']['frequency_halfayear'])) {
                $fleet->naggingInterval = "frequency_halfayear";
            }
            
            if ($fleet->followup === "false") {
                $allNumberUnique = $this->checkIfDuplicatedNumbers($fleet->cars);
                if (!$allNumberUnique) {
                        $obj = $this->getStdErrorObject(); // Get a default error message
                        $obj->fields->errorMessage = "På automatisk oppføling må alle bilene ha unike mobilnr."; // The message you wish to display in the gserrorfield
                        $obj->gsfield->fleetFollowup = 1; // Will highlight the field that has gsname "hours"
                        $this->doError($obj); // Code will stop here.
                }
            }
            $this->getApi()->getMecaManager()->saveFleet($fleet);
        }
    }

    /**
     * 
     * @param \core_mecamanager_MecaCar[] $cars
     */
    public function checkIfDuplicatedNumbers($cars) {
        $grouped = array();
        foreach ($cars as $carId) {
            $car = $this->getApi()->getMecaManager()->getCar($carId);
            $grouped[$car->cellPhone][] = $car;
        }
        
        foreach ($grouped as $phoneNumber => $checkCars) {
            if (count($checkCars) > 1) {
                return false;
            }
        }
        
 
        return true;
    }

}
?>

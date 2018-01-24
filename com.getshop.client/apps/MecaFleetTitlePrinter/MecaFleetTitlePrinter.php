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
            
            if (isset($_POST['data']['frequency_none'])) {
                $fleet->naggingInterval = "frequency_none";
            }
            
            if (isset($_POST['data']['frequency_daily'])) {
                $fleet->naggingInterval = "frequency_daily";
            }
            
            if (isset($_POST['data']['frequency_weekly'])) {
                $fleet->naggingInterval = "frequency_weekly";
            }
            
            $this->getApi()->getMecaManager()->saveFleet($fleet);
        }
    }
}
?>

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
            $fleet->contactDetails = $_POST['data']['contactDetails'];
            
            $this->getApi()->getMecaManager()->saveFleet($fleet);
        }
    }
}
?>

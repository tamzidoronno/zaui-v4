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
            echo "<div class='title'>$fleet->name</div>";
        } else {
            echo "<div class='title'>DEMO</div>";
        }
    }
}
?>

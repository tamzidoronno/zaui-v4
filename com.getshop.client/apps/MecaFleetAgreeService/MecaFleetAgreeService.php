<?php
namespace ns_027f0fd0_2f7a_489b_abd9_5869eab5b474;

class MecaFleetAgreeService extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetAgreeService";
    }

    public function render() {
        $this->includefile("agree");
    }
    
    public function requestDate() {
        $nextDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $this->getApi()->getMecaManager()->requestNextService($_POST['data']['carid'], $nextDate);
    }
}
?>

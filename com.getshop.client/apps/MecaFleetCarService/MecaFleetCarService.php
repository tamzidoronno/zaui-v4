<?php
namespace ns_c5ecb4c4_1f18_4016_8c15_0ea519c91974;

class MecaFleetCarService extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetCarService";
    }

    public function render() {
        $this->includefile("carservice");
    }
    
    public function save() {
        $mecaCar = $this->getApi()->getMecaManager()->getCarByPageId($this->getPage()->getId());
        $mecaCar->lastService = $this->convertToJavaDate(strtotime($_POST['data']['lastservicedate']));
        $mecaCar->kilometersBetweenEachService = $_POST['data']['servicekm'];
        $mecaCar->monthsBetweenServices = $_POST['data']['months'];
        $mecaCar->lastServiceKilomters = $_POST['data']['lastservicekm'];
        
        $this->getApi()->getMecaManager()->saveFleetCar($this->getPage()->getId(), $mecaCar);
    }
}
?>

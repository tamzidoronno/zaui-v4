<?php
namespace ns_cf32e5a8_faf9_4039_92cd_176b5d3113ec;

class MecaFleetCarCreate extends \MarketingApplication implements \Application {  
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetCarCreate";
    }

    public function render() {
        $this->includefile("createcar");
    }
    
    public function saveCar() {
        
        $mecaCar = $this->getApi()->getMecaManager()->getCarByPageId($this->getPage()->getId());
        
        if (!$mecaCar) {
            $mecaCar = new \core_mecamanager_MecaCar();
        }

        $mecaCar->cellPhone = $_POST['data']['phone'];
        $mecaCar->carOwner = $_POST['data']['carowner'];
        $mecaCar->carInfo = $_POST['data']['carinfo'];
        $mecaCar->kilometers = $_POST['data']['kmstand'];
        $mecaCar->licensePlate = $_POST['data']['regnr'];
        $mecaCar->prevControll = $this->convertToJavaDate(strtotime($_POST['data']['eukontroll'] . " - 2 year"));
        
        $this->getApi()->getMecaManager()->saveFleetCar($this->getPage()->getId(), $mecaCar);
    }
    
    public function sendKilomterRequest() {
        $this->getApi()->getMecaManager()->sendKilometerRequest($_POST['data']['carid']);
    }
}
?>

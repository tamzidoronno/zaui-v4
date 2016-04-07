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
        $mecaCar->kilometers = $_POST['data']['kmstand'];
        $mecaCar->licensePlate = $_POST['data']['regnr'];
        $mecaCar->prevControll = $this->convertToJavaDate(strtotime($_POST['data']['eukontroll']));
        
        
        $this->getApi()->getMecaManager()->saveFleetCar($this->getPage()->getId(), $mecaCar);
    }
}
?>

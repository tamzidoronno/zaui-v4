<?php
namespace ns_3bbe1c5d_2f02_40dd_a16a_55aa46953300;

class MecaFleetUpdateKilometers extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetUpdateKilometers";
    }

    public function render() {
        $this->includefile("updatekilomters");
    }
    
    public function update() {
        foreach ($_POST['data'] as $key => $value) {
            if (!$value) {
                continue;
            }
            
            if (!strstr($key, "car_")) {
                continue;
            }
            
            $splitted = explode("_", $key);
            $id = $splitted[1];
            
            $car = $this->getApi()->getMecaManager()->getCar($id);
            $this->getApi()->getMecaManager()->sendKilometers($car->cellPhone, $value);
        }
    }
}
?>